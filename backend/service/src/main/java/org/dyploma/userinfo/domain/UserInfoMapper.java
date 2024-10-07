package org.dyploma.userinfo.domain;

import java.util.Optional;
import java.util.UUID;

import org.dyploma.userinfo.dto.UserInfoRequest;
import org.dyploma.userinfo.dto.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
interface UserInfoMapper {
    @Mapping(source = "uuid", target = "id")
    UserInfoResponse userInfoToUserInfoResponse(UserInfo userInfo);
    UserInfo userInfoRequestToUserInfo(UserInfoRequest userInfo);
}
