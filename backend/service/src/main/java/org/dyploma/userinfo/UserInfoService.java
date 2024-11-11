package org.dyploma.userinfo;

import org.dyploma.userinfo.dto.UserInfoRequest;
import org.dyploma.userinfo.dto.UserInfoResponse;

import java.util.List;


public interface UserInfoService {
    UserInfoResponse getUserInfoByEmail(String email);

    UserInfoResponse getUserInfoById(Integer Integer);

    List<UserInfoResponse> getAll();

    UserInfoResponse createUserInfo(UserInfoRequest userInfoRequest);

    boolean existsByEmail(String email);
}
