package org.dyploma.oauth.domain;

import org.dyploma.useraccount.UserAccount;
import org.dyploma.useraccount.UserAccountService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserAccountService userAccountService;

    public CustomOAuth2UserService(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Get the user's email from Google
        String email = oAuth2User.getAttribute("email");

        // Fetch the user from the database
        UserAccount userAccount = userAccountService.getUserByEmail(email);

        if (userAccount == null) {
            throw new RuntimeException("User not found in the database");
        }

        // Add the role as a GrantedAuthority
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + userAccount.getRole())
        );

        // Return a new OAuth2User with updated authorities
        return new DefaultOAuth2User(
                authorities,
                oAuth2User.getAttributes(),
                "email" // Set the key used to extract the email
        );
    }
}