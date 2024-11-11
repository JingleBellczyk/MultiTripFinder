package org.dyploma.userinfo.domain;

import com.openapi.api.UserApi;
import com.openapi.model.UserCreation;
import com.openapi.model.UserInfo;
import com.openapi.model.UserInfoPage;
import org.dyploma.userinfo.UserInfoService;
import org.dyploma.userinfo.dto.UserInfoRequest;
import org.dyploma.userinfo.dto.UserInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@RestController
@RequestMapping("/api/userinfo")
public class UserInfoController implements UserApi {
    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }
}
