package org.dyploma.useraccount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserAccountService {
    UserAccount getOrCreateUser(UserAccount userAccount);
    UserAccount getUserById(Integer userId);
    Boolean userExistsByEmail(String email);
    UserAccount getUserByEmail(String email);
    void deleteUser(Integer userId);
    Page<UserAccount> listUsers(Pageable pageable);
}
