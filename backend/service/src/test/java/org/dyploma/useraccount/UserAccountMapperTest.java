package org.dyploma.useraccount;

import com.openapi.model.PageInfo;
import com.openapi.model.User;
import com.openapi.model.UserPage;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserAccountMapperTest {

    @Test
    void testMapToUserAccount() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");

        // When
        UserAccount userAccount = UserAccountMapper.mapToUserAccount(user);

        // Then
        assertThat(userAccount).isNotNull();
        assertThat(userAccount.getEmail()).isEqualTo("test@example.com");
        assertThat(userAccount.getRole()).isEqualTo('U');
    }

    @Test
    void testMapToUserApi() {
        // Given
        UserAccount userAccount = UserAccount.builder()
                .id(1)
                .email("test@example.com")
                .role('U')
                .build();

        // When
        User user = UserAccountMapper.mapToUserApi(userAccount);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getRole()).isEqualTo(User.RoleEnum.U);
    }

    @Test
    void testMapToUserPageApiWithMultiplePages() {
        // Given
        int pageNumber = 0;
        int pageSize = 2;

        // Tworzymy 3 użytkowników
        UserAccount userAccount1 = UserAccount.builder()
                .id(1)
                .email("test1@example.com")
                .role('U')
                .build();
        UserAccount userAccount2 = UserAccount.builder()
                .id(2)
                .email("test2@example.com")
                .role('U')
                .build();
        UserAccount userAccount3 = UserAccount.builder()
                .id(3)
                .email("test3@example.com")
                .role('U')
                .build();

        List<UserAccount> userAccounts = List.of(userAccount1, userAccount2, userAccount3);
        Page<UserAccount> userAccountPage = new PageImpl<>(userAccounts.subList(0, 2), PageRequest.of(pageNumber, pageSize), userAccounts.size());

        // When
        UserPage userPage = UserAccountMapper.mapToUserPageApi(userAccountPage);

        // Then
        assertThat(userPage).isNotNull();
        assertThat(userPage.getContent()).hasSize(2);
        assertThat(userPage.getTotalPages()).isEqualTo(2);
        assertThat(userPage.getTotalElements()).isEqualTo(3);

        PageInfo pageInfo = userPage.getPage();
        assertThat(pageInfo).isNotNull();
        assertThat(pageInfo.getNumber()).isEqualTo(0);
    }
}
