package org.dyploma.useraccount;

import jakarta.transaction.Transactional;
import org.dyploma.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountServiceImpl(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount getOrCreateUser(UserAccount userAccount) {
        return userAccountRepository.findByEmail(userAccount.getEmail())
                .orElseGet(() -> userAccountRepository.save(userAccount));
    }

    @Override
    public UserAccount getUserById(Integer userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public Boolean userExistsByEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    @Override
    public UserAccount getUserByEmail(String email) {
        return userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public void deleteUser(Integer userId) {
        if (!userAccountRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        userAccountRepository.deleteById(userId);
    }

    @Override
    public Page<UserAccount> listUsers(Pageable pageable) {
        return userAccountRepository.findAll(pageable);
    }
}
