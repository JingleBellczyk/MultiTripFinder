package org.dyploma.oauth.controller;

import org.dyploma.useraccount.UserAccount;
import org.dyploma.useraccount.UserAccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class OAuth2Controller {
    private final OAuth2AuthorizedClientService clientService;
    private final UserAccountServiceImpl userAccountService;

    @Autowired
    public OAuth2Controller(OAuth2AuthorizedClientService clientService, UserAccountServiceImpl userAccountService) {
        this.clientService = clientService;
        this.userAccountService = userAccountService;
    }

    @GetMapping("/auth/token")
    public String getToken(OAuth2AuthenticationToken authentication) {
        return ((DefaultOidcUser) authentication.getPrincipal()).getIdToken().getTokenValue();
    }

    @GetMapping("/auth/user-info")
    public ResponseEntity<Map<String, Object>> user(OAuth2AuthenticationToken authentication) {
        String email = authentication.getPrincipal().getAttribute("email");

        if (email == null) {
            // Return a 400 Bad Request if email is missing
            return ResponseEntity.badRequest().body(Map.of("error", "Email attribute not found in authentication token"));
        }

        UserAccount user = userAccountService.getOrCreateUser(UserAccount.builder().role('U').email(email).build());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole());
        userInfo.putAll(authentication.getPrincipal().getAttributes());

        return ResponseEntity.ok(userInfo);
    }

}