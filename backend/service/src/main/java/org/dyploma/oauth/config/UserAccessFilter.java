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

@Component
public class UserAccessFilter extends OncePerRequestFilter {

    private final UserAccountService userAccountService;
    private final List<String> restrictedEndpoints;

    @Autowired
    public UserAccessFilter(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
        restrictedEndpoints = List.of("searchList", "user", "tripList", "searchTag", "tripTag");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        String[] uriSegments = requestURI.split("/");
        if (uriSegments.length > 2 && restrictedEndpoints.contains(uriSegments[1])) {
            String userId = uriSegments[2];

            if (userId != null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !(authentication.getPrincipal() instanceof OAuth2User)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("You must be authenticated to access this resource.");
                    return;
                }
                String email = ((OAuth2User) authentication.getPrincipal()).getAttribute("email");

                UserAccount authenticatedUser = userAccountService.getUserByEmail(email);

                if (!authenticatedUser.getId().equals(Integer.valueOf(userId)) && authenticatedUser.getRole() != 'A') {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("You do not have permission to access this resource.");
                    return;
                }
            }
            filterChain.doFilter(request, response);
        }
        filterChain.doFilter(request, response);
    }
}
