package org.dyploma.userinfo.domain;

import org.dyploma.userinfo.UserInfoService;
import org.dyploma.userinfo.dto.UserInfoRequest;
import org.dyploma.userinfo.dto.UserInfoResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

//TODO Controller must implement api from openapi
@RestController
@RequestMapping("/api/userinfo")
public class UserInfoController {
    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @GetMapping
    public List<UserInfoResponse> getAll() {
        return userInfoService.getAll();
    }

    @GetMapping("/{userId}")
    public UserInfoResponse getUserInfoById(@PathVariable UUID userId) {
        return userInfoService.getUserInfoById(userId);
    }

    @GetMapping("/{email}")
    public UserInfoResponse getUserInfoByEmail(@PathVariable String email) {
        return userInfoService.getUserInfoByEmail(email);
    }

    @PostMapping
    public UserInfoResponse createUserInfo(@RequestBody UserInfoRequest userInfoRequest) {
        return userInfoService.createUserInfo(userInfoRequest);
    }
}
