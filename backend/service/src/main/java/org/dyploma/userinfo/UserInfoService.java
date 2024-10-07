package org.dyploma.userinfo;

import org.dyploma.userinfo.dto.UserInfoRequest;
import org.dyploma.userinfo.dto.UserInfoResponse;

import java.util.List;
import java.util.UUID;

public interface UserInfoService {
    UserInfoResponse getUserInfoByEmail(String email);

    UserInfoResponse getUserInfoById(UUID uuid);

    List<UserInfoResponse> getAll();

    UserInfoResponse createUserInfo(UserInfoRequest userInfoRequest);

    boolean existsByEmail(String email);
}
