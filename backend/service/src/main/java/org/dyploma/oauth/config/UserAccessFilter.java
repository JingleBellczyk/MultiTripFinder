package org.dyploma.oauth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.dyploma.useraccount.UserAccount;
import org.dyploma.useraccount.UserAccountService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class UserAccessFilter extends OncePerRequestFilter {

    private UserAccountService userAccountService;
    private List<String> privateUserEndpoints;

    @Autowired
    public UserAccessFilter(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
        privateUserEndpoints = List.of("searchList", "user", "tripList", "searchTag", "tripTag");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // Assuming the userId is part of the path like /searchList/{userId}
        String[] uriSegments = requestURI.split("/");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoggerFactory.getLogger(UserAccessFilter.class).error("Authentication: " + authentication);
        // Check if the userId is present in the path (e.g., after '/searchList/')
        if (uriSegments.length > 2 && privateUserEndpoints.contains(uriSegments[1])) {
            String userId = uriSegments[2];

            if (userId != null) {
                // Get the authenticated user's email (or any identifier)

                if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
                    // Deny access if the user is not authenticated
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("You must be authenticated to access this resource.");
                    return;
                }
                String email = ((OAuth2User) authentication.getPrincipal()).getAttribute("email");

                // Retrieve the user by email and check if the user ID matches
                UserAccount authenticatedUser = userAccountService.getUserByEmail(email);

                if (!authenticatedUser.getId().equals(Integer.valueOf(userId))) {
                    // Deny access if the IDs don't match
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("You do not have permission to access this resource.");
                    return;
                }
            }

            // Continue the filter chain if the check passes
            filterChain.doFilter(request, response);
        }
        filterChain.doFilter(request, response);
    }
}
