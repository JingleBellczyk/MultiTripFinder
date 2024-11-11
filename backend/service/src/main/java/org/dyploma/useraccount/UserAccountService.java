package org.dyploma.useraccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount getOrCreateUser(String email) {
        return userAccountRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserAccount newUser = new UserAccount();
                    newUser.setEmail(email);
                    newUser.setRole('U');
                    return userAccountRepository.save(newUser);
                });
    }
}
