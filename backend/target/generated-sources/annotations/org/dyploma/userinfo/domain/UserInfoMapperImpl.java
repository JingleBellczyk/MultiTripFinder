package org.dyploma.userinfo.domain;

import java.util.UUID;
import javax.annotation.processing.Generated;
import org.dyploma.userinfo.dto.UserInfoRequest;
import org.dyploma.userinfo.dto.UserInfoResponse;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-09-21T21:09:55+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
class UserInfoMapperImpl implements UserInfoMapper {

    @Override
    public UserInfoResponse userInfoToUserInfoResponse(UserInfo userInfo) {
        if ( userInfo == null ) {
            return null;
        }

        UUID id = null;
        String firstName = null;
        String lastName = null;
        String email = null;

        id = userInfo.getUuid();
        firstName = userInfo.getFirstName();
        lastName = userInfo.getLastName();
        email = userInfo.getEmail();

        UserInfoResponse userInfoResponse = new UserInfoResponse( id, firstName, lastName, email );

        return userInfoResponse;
    }

    @Override
    public UserInfo userInfoRequestToUserInfo(UserInfoRequest userInfo) {
        if ( userInfo == null ) {
            return null;
        }

        UserInfo.UserInfoBuilder userInfo1 = UserInfo.builder();

        userInfo1.firstName( userInfo.firstName() );
        userInfo1.lastName( userInfo.lastName() );
        userInfo1.email( userInfo.email() );

        return userInfo1.build();
    }
}
