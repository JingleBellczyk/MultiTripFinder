package org.dyploma.securityTests;

import org.dyploma.oauth.controller.OAuth2Controller;
import org.dyploma.useraccount.UserAccountServiceImpl;
import org.dyploma.useraccount.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OAuth2ControllerTest {

    @Mock
    private UserAccountServiceImpl userAccountService;

    @InjectMocks
    private OAuth2Controller oAuth2Controller;

    @Mock
    private OAuth2AuthenticationToken authentication;

    @Mock
    private DefaultOidcUser principal;

    @Mock
    private UserAccount mockUser;

    @Mock
    private OidcIdToken idToken;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(authentication.getPrincipal()).thenReturn(principal);

        when(principal.getIdToken()).thenReturn(idToken);
        when(idToken.getTokenValue()).thenReturn("mockToken");
    }

    @Test
    public void testGetToken() {
        String token = oAuth2Controller.getToken(authentication);

        assertEquals("mockToken", token);
    }

    @Test
    public void testUserInfo_Success() {
        String email = "test@example.com";
        when(principal.getAttribute("email")).thenReturn(email);
        when(userAccountService.getOrCreateUser(any(UserAccount.class))).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(1);
        when(mockUser.getEmail()).thenReturn(email);
        when(mockUser.getRole()).thenReturn('U');

        ResponseEntity<Map<String, Object>> response = oAuth2Controller.user(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("email"));
        assertEquals(email, response.getBody().get("email"));
    }

    @Test
    public void testUserInfo_ErrorNoEmail() {
        when(principal.getAttribute("email")).thenReturn(null);

        ResponseEntity<Map<String, Object>> response = oAuth2Controller.user(authentication);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("error"));
    }

}
