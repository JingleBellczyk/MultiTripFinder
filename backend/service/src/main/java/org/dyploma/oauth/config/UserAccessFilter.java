package org.dyploma.oauth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.dyploma.useraccount.UserAccount;
import org.dyploma.useraccount.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/*@Component
public class UserAccessFilter extends OncePerRequestFilter {

    private final UserAccountService userAccountService;
    private final List<String> restrictedEndpointsWithUserId;
    private final String adminOnlyEndpoint;

    @Autowired
    public UserAccessFilter(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
        restrictedEndpointsWithUserId = List.of("searchList", "tripList", "searchTag", "tripTag");
        adminOnlyEndpoint = "user";
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String[] uriSegments = requestURI.split("/");

        boolean isUserPrivateEndpoint = uriSegments.length > 2 && restrictedEndpointsWithUserId.contains(uriSegments[1]);
        boolean isAdminEndpoint = uriSegments.length == 2 && adminOnlyEndpoint.equals(uriSegments[1]);

        if(!isUserPrivateEndpoint && !isAdminEndpoint) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("You must be authenticated to access this resource.");
            return;
        }

        String email = ((OAuth2User) authentication.getPrincipal()).getAttribute("email");
        UserAccount authenticatedUser = userAccountService.getUserByEmail(email);

        if (isUserPrivateEndpoint) {
            String userId = uriSegments[2];
            if (!authenticatedUser.getId().equals(Integer.valueOf(userId)) && authenticatedUser.getRole() != 'A') {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("You do not have permission to access this resource.");
                return;
            }
        }
        else {
            if (authenticatedUser.getRole() != 'A') {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Only admins can access this resource.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}*/
