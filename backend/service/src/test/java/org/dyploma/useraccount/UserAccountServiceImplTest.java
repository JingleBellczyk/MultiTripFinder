package org.dyploma.useraccount;

import org.dyploma.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserAccountServiceImplTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    private UserAccountServiceImpl userAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userAccountService = new UserAccountServiceImpl(userAccountRepository);
    }

    @Test
    public void testGetOrCreateUserWhenUserExists() {
        // Given
        UserAccount existingUser = new UserAccount();
        existingUser.setEmail("test@example.com");
        when(userAccountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        // When
        UserAccount result = userAccountService.getOrCreateUser(existingUser);

        // Then
        assertEquals(existingUser, result, "Should return the existing user");
        verify(userAccountRepository, times(1)).findByEmail("test@example.com");
        verify(userAccountRepository, never()).save(any());
    }

    @Test
    public void testGetOrCreateUserWhenUserDoesNotExist() {
        // Given
        UserAccount newUser = new UserAccount();
        newUser.setEmail("newuser@example.com");
        when(userAccountRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(userAccountRepository.save(newUser)).thenReturn(newUser);

        // When
        UserAccount result = userAccountService.getOrCreateUser(newUser);

        // Then
        assertEquals(newUser, result, "Should return the newly created user");
        verify(userAccountRepository, times(1)).findByEmail("newuser@example.com");
        verify(userAccountRepository, times(1)).save(newUser);
    }

    @Test
    public void testGetUserByIdWhenUserExists() {
        // Given
        UserAccount existingUser = new UserAccount();
        existingUser.setId(1);
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(existingUser));

        // When
        UserAccount result = userAccountService.getUserById(1);

        // Then
        assertEquals(existingUser, result, "Should return the user with the given ID");
    }

    @Test
    public void testGetUserByIdWhenUserDoesNotExist() {
        // Given
        when(userAccountRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userAccountService.getUserById(1));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testDeleteUserWhenUserExists() {
        // Given
        UserAccount existingUser = new UserAccount();
        existingUser.setId(1);
        when(userAccountRepository.existsById(1)).thenReturn(true);

        // When
        userAccountService.deleteUser(1);

        // Then
        verify(userAccountRepository, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteUserWhenUserDoesNotExist() {
        // Given
        when(userAccountRepository.existsById(1)).thenReturn(false);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userAccountService.deleteUser(1));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testListUsers() {
        // Given
        UserAccount user1 = UserAccount.builder().id(1).email("user1@example.com").role('U').build();
        UserAccount user2 = UserAccount.builder().id(2).email("user2@example.com").role('A').build();
        List<UserAccount> userList = List.of(user1, user2);

        Pageable pageable = PageRequest.of(0, 2);
        Page<UserAccount> userPage = new PageImpl<>(userList, pageable, userList.size());

        when(userAccountRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<UserAccount> result = userAccountService.listUsers(pageable);

        // Then
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getContent().get(0).getId());
        assertEquals("user1@example.com", result.getContent().get(0).getEmail());

        verify(userAccountRepository, times(1)).findAll(pageable);
    }
}
