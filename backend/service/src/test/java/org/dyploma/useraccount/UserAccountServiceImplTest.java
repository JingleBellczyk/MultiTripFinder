package org.dyploma.useraccount;

import org.dyploma.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        // Arrange
        UserAccount existingUser = new UserAccount();
        existingUser.setEmail("test@example.com");
        when(userAccountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        // Act
        UserAccount result = userAccountService.getOrCreateUser(existingUser);

        // Assert
        assertEquals(existingUser, result, "Should return the existing user");
        verify(userAccountRepository, times(1)).findByEmail("test@example.com");
        verify(userAccountRepository, never()).save(any());
    }

    @Test
    public void testGetOrCreateUserWhenUserDoesNotExist() {
        // Arrange
        UserAccount newUser = new UserAccount();
        newUser.setEmail("newuser@example.com");
        when(userAccountRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(userAccountRepository.save(newUser)).thenReturn(newUser);

        // Act
        UserAccount result = userAccountService.getOrCreateUser(newUser);

        // Assert
        assertEquals(newUser, result, "Should return the newly created user");
        verify(userAccountRepository, times(1)).findByEmail("newuser@example.com");
        verify(userAccountRepository, times(1)).save(newUser);
    }

    @Test
    public void testGetUserByIdWhenUserExists() {
        // Arrange
        UserAccount existingUser = new UserAccount();
        existingUser.setId(1);
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(existingUser));

        // Act
        UserAccount result = userAccountService.getUserById(1);

        // Assert
        assertEquals(existingUser, result, "Should return the user with the given ID");
    }

    @Test
    public void testGetUserByIdWhenUserDoesNotExist() {
        // Arrange
        when(userAccountRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userAccountService.getUserById(1));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testDeleteUserWhenUserExists() {
        // Arrange
        UserAccount existingUser = new UserAccount();
        existingUser.setId(1);
        when(userAccountRepository.existsById(1)).thenReturn(true);

        // Act
        userAccountService.deleteUser(1);

        // Assert
        verify(userAccountRepository, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteUserWhenUserDoesNotExist() {
        // Arrange
        when(userAccountRepository.existsById(1)).thenReturn(false);

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userAccountService.deleteUser(1));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testListUsers() {
        // Przygotowanie danych testowych
        UserAccount user1 = UserAccount.builder().id(1).email("user1@example.com").role('U').build();
        UserAccount user2 = UserAccount.builder().id(2).email("user2@example.com").role('A').build();
        List<UserAccount> userList = List.of(user1, user2);

        Pageable pageable = PageRequest.of(0, 2);
        Page<UserAccount> userPage = new PageImpl<>(userList, pageable, userList.size());

        // Konfiguracja zachowania mocka
        when(userAccountRepository.findAll(pageable)).thenReturn(userPage);

        // Wywołanie testowanej metody
        Page<UserAccount> result = userAccountService.listUsers(pageable);

        // Weryfikacja
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getContent().get(0).getId());
        assertEquals("user1@example.com", result.getContent().get(0).getEmail());

        // Sprawdzenie interakcji z mockiem
        verify(userAccountRepository, times(1)).findAll(pageable);
    }
}
