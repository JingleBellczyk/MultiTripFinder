package org.dyploma.userinfo.domain;

import org.dyploma.userinfo.dto.UserInfoRequest;
import org.dyploma.userinfo.dto.UserInfoResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
class UserInfoServiceImpl implements org.dyploma.userinfo.UserInfoService {
    @Override
    public UserInfoResponse getUserInfoByEmail(String email) {
        return null;
    }

    @Override
    public UserInfoResponse getUserInfoById(UUID uuid) {
        return null;
    }

    @Override
    public List<UserInfoResponse> getAll() {
        return null;
    }

    @Override
    public UserInfoResponse createUserInfo(UserInfoRequest userInfoRequest) {
        return null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }
}
