package org.dyploma.securityTests;

import lombok.extern.slf4j.Slf4j;
import org.dyploma.oauth.config.UserAccessFilter;
import org.dyploma.oauth.controller.OAuth2Controller;
import org.dyploma.useraccount.UserAccount;
import org.dyploma.useraccount.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Import({UserAccessFilter.class, OAuth2Controller.class})
@ExtendWith(MockitoExtension.class)
public class AccessEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserAccountService userAccountService;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilters(new UserAccessFilter(userAccountService))
                .build();
    }

    @Test
    public void testGetUsers_authenticatedUser() throws Exception {
        Authentication authentication = mock(Authentication.class);
        OAuth2User oauth2User = mock(OAuth2User.class);

        when(oauth2User.getAttribute("email")).thenReturn("userexample@user.com");
        when(authentication.getPrincipal()).thenReturn(oauth2User);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setEmail("userexample@user.com");
        mockUserAccount.setRole('U');
        mockUserAccount.setId(4);
        when(userAccountService.getUserByEmail("userexample@user.com")).thenReturn(mockUserAccount);

        ResultActions result = mockMvc.perform(get("/user"));

        result.andExpect(status().isForbidden()) // Expect HTTP 403 Forbidden
                .andExpect(content().string("Only admins can access this resource."));
    }


    @Test
    public void testGetUsers_unauthenticatedUser() throws Exception {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null); // No authentication
        SecurityContextHolder.setContext(securityContext);

        ResultActions result = mockMvc.perform(get("/user"));

        result.andExpect(status().isUnauthorized())
                .andExpect(content().string("You must be authenticated to access this resource."));
    }

    @Test
    public void testGetUsers_adminAccess() throws Exception {
        Authentication authentication = mock(Authentication.class);
        OAuth2User oauth2User = mock(OAuth2User.class);

        when(oauth2User.getAttribute("email")).thenReturn("userexample@user.com");
        when(authentication.getPrincipal()).thenReturn(oauth2User);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setEmail("userexample@user.com");
        mockUserAccount.setRole('A');
        mockUserAccount.setId(1);
        when(userAccountService.getUserByEmail("userexample@user.com")).thenReturn(mockUserAccount);

        ResultActions result = mockMvc.perform(get("/user"));

        result.andExpect(status().isOk());
    }

    @Test
    public void testSearchList_accessOwnResource() throws Exception {
        Authentication authentication = mock(Authentication.class);
        OAuth2User oauth2User = mock(OAuth2User.class);

        when(oauth2User.getAttribute("email")).thenReturn("user1@example.com");
        when(authentication.getPrincipal()).thenReturn(oauth2User);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setEmail("user1@example.com");
        mockUserAccount.setRole('U');
        mockUserAccount.setId(1);
        when(userAccountService.getUserByEmail("user1@example.com")).thenReturn(mockUserAccount);

        // Act: Perform a request to access the user's own resources
        ResultActions result = mockMvc.perform(get("/searchList/1"));

        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    public void testSearchList_accessOtherUserResource() throws Exception {
        Authentication authentication = mock(Authentication.class);
        OAuth2User oauth2User = mock(OAuth2User.class);

        when(oauth2User.getAttribute("email")).thenReturn("user1@example.com");
        when(authentication.getPrincipal()).thenReturn(oauth2User);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserAccount mockUserAccount = new UserAccount();
        mockUserAccount.setEmail("user1@example.com");
        mockUserAccount.setRole('U'); // Regular user
        mockUserAccount.setId(1);    // User ID 1
        when(userAccountService.getUserByEmail("user1@example.com")).thenReturn(mockUserAccount);

        ResultActions result = mockMvc.perform(get("/searchList/2"));

        result.andExpect(status().isForbidden())
                .andExpect(content().string("You do not have permission to access this resource."));
    }

    @Test
    public void testSearchList_unauthenticatedUser() throws Exception {
        SecurityContextHolder.clearContext();

        ResultActions result = mockMvc.perform(get("/searchList/1"));

        result.andExpect(status().isUnauthorized())
                .andExpect(content().string("You must be authenticated to access this resource."));
    }

}