package org.dyploma.useraccount;

import com.openapi.model.User;
import com.openapi.model.UserPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


class UserAccountControllerTest {

    @Mock
    private UserAccountService userAccountService;

    @InjectMocks
    private UserAccountController userAccountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        // Given
        User user = new User().email("test@example.com").role(User.RoleEnum.U);
        var userAccount = UserAccountMapper.mapToUserAccount(user);
        when(userAccountService.getOrCreateUser(userAccount)).thenReturn(userAccount);

        // When
        ResponseEntity<User> response = userAccountController.createUser(user);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals("test@example.com", Objects.requireNonNull(response.getBody()).getEmail());
        verify(userAccountService, times(1)).getOrCreateUser(any());
    }

    @Test
    void testFindUser() {
        // Given
        Integer userId = 1;
        var userAccount = UserAccount.builder().id(userId).email("test@example.com").role('U').build();
        when(userAccountService.getUserById(userId)).thenReturn(userAccount);

        // When
        ResponseEntity<User> response = userAccountController.findUser(userId);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals(userId, Objects.requireNonNull(response.getBody()).getId());
        assertEquals("test@example.com", response.getBody().getEmail());
        verify(userAccountService, times(1)).getUserById(userId);
    }

    @Test
    void testDeleteUser() {
        // Given
        Integer userId = 1;
        doNothing().when(userAccountService).deleteUser(userId);

        // When
        ResponseEntity<Void> response = userAccountController.deleteUser(userId);

        // Then
        assertEquals(200, response.getStatusCode().value());
        verify(userAccountService, times(1)).deleteUser(userId);
    }

    @Test
    void testListUsers() {
        // Given
        int page = 0;
        int size = 5;

        // Symulujemy listę z jednym elementem
        var userAccounts = List.of(
                UserAccount.builder().id(1).email("user1@example.com").role('U').build()
        );

        // Konfigurujemy Page z 1 elementem na stronie
        Page<UserAccount> pageResult = new PageImpl<>(userAccounts, PageRequest.of(page, size), 1);
        when(userAccountService.listUsers(any())).thenReturn(pageResult);

        // When
        ResponseEntity<UserPage> response = userAccountController.listUsers(page, size);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(1, response.getBody().getTotalPages());
        assertEquals(1, response.getBody().getTotalElements());
        verify(userAccountService, times(1)).listUsers(any());
    }

    @Test
    void testListUsersWithMoreThanOnePage() {
        // Given
        int page = 0;
        int size = 5;

        var userAccounts = List.of(
                UserAccount.builder().id(1).email("user1@example.com").role('U').build(),
                UserAccount.builder().id(2).email("user2@example.com").role('U').build(),
                UserAccount.builder().id(3).email("user3@example.com").role('U').build(),
                UserAccount.builder().id(4).email("user4@example.com").role('U').build(),
                UserAccount.builder().id(5).email("user5@example.com").role('U').build(),
                UserAccount.builder().id(6).email("user6@example.com").role('U').build()
        );

        Page<UserAccount> pageResult = new PageImpl<>(userAccounts, PageRequest.of(page, size), userAccounts.size());
        when(userAccountService.listUsers(any())).thenReturn(pageResult);

        // When
        ResponseEntity<UserPage> response = userAccountController.listUsers(page, size);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getTotalPages());
        assertEquals(5, response.getBody().getContent().size());
        assertEquals(6, response.getBody().getTotalElements());
        verify(userAccountService, times(1)).listUsers(any());
    }
}
