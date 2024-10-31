package org.dyploma.oauth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class OAuth2Controller {
    private final OAuth2AuthorizedClientService clientService;

    @Autowired
    public OAuth2Controller(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/auth/token")
    public String getToken(OAuth2AuthenticationToken authentication) {
        return ((DefaultOidcUser) authentication.getPrincipal()).getIdToken().getTokenValue();
    }

    @GetMapping("/auth/user-info")
    public Map<String, Object> user(OAuth2AuthenticationToken authentication){
        return authentication.getPrincipal().getAttributes();
    }
}