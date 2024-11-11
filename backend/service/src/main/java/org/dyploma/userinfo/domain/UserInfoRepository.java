package org.dyploma.userinfo.domain;

import org.springframework.data.jpa.repository.JpaRepository;



interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
}
