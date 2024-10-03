package org.dyploma.userinfo.domain;

import com.example.api.UserApi;
import com.example.model.UserCreation;
import com.example.model.UserInfo;
import com.example.model.UserInfoPage;
import org.dyploma.userinfo.UserInfoService;
import org.dyploma.userinfo.dto.UserInfoRequest;
import org.dyploma.userinfo.dto.UserInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/userinfo")
public class UserInfoController implements UserApi {
    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Override
    public ResponseEntity<UserInfo> createUser(UserCreation userCreation) {
        return null;
    }

    @Override
    public ResponseEntity<UserInfo> findUser(String userId) {
        return UserApi.super.findUser(userId);
    }

    @Override
    public ResponseEntity<UserInfoPage> listUsers(String search, Integer page, Integer size, List<String> sort) {
        return UserApi.super.listUsers(search, page, size, sort);
    }

    @Override
    public ResponseEntity<Void> deleteUser(String userId) {
        return UserApi.super.deleteUser(userId);
    }
}
