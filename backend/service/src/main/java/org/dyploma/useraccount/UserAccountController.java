package org.dyploma.useraccount;

import com.openapi.api.UserApi;
import com.openapi.model.User;
import com.openapi.model.UserPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:80","http://frontend:80"})
public class UserAccountController implements UserApi {

    private final UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public ResponseEntity<User> createUser(User user) {
        return ResponseEntity.ok(UserAccountMapper.mapToUserApi(userAccountService.getOrCreateUser(UserAccountMapper.mapToUserAccount(user))));
    }

    @Override
    public ResponseEntity<User> findUser(Integer userId) {
        return ResponseEntity.ok(UserAccountMapper.mapToUserApi(userAccountService.getUserById(userId)));
    }

    @Override
    public ResponseEntity<Void> deleteUser(Integer userId) {
        userAccountService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<UserPage> listUsers(Integer page, Integer size) {
        return ResponseEntity.ok(UserAccountMapper.mapToUserPageApi(userAccountService.listUsers(Pageable.ofSize(size).withPage(page))));
    }
}
