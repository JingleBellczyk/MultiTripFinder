package org.dyploma.useraccount;

import com.openapi.model.PageInfo;
import com.openapi.model.User;
import com.openapi.model.UserPage;
import org.dyploma.search.presentation.SearchMapper;
import org.springframework.data.domain.Page;

public class UserAccountMapper {
    public static UserAccount mapToUserAccount(User user) {
        return  UserAccount.builder()
                .email(user.getEmail())
                .role('U')
                .build();
    }

    public static User mapToUserApi(UserAccount userAccount) {
        return new User()
                .id(userAccount.getId())
                .role(User.RoleEnum.valueOf(userAccount.getRole().toString()))
                .email(userAccount.getEmail());
    }

    public static UserPage mapToUserPageApi(Page<UserAccount> userAccountPage) {
        UserPage userPageApi = new UserPage();
        userPageApi.setContent(userAccountPage.getContent().stream()
                .map(UserAccountMapper::mapToUserApi)
                .toList());
        userPageApi.setTotalPages(userAccountPage.getTotalPages());
        userPageApi.setTotalElements((int) userAccountPage.getTotalElements());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setNumber(userAccountPage.getNumber());
        pageInfo.setSize(userAccountPage.getSize());
        userPageApi.setPage(pageInfo);
        return userPageApi;
    }
}
