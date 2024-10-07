package org.dyploma.userinfo.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {
}
