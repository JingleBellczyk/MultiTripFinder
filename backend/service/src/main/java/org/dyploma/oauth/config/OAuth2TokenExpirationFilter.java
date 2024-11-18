package org.dyploma.oauth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.NonNull;

import java.io.IOException;
import java.time.Instant;


@Component
public class OAuth2TokenExpirationFilter extends OncePerRequestFilter {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public OAuth2TokenExpirationFilter(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    authentication.getAuthorizedClientRegistrationId(),
                    authentication.getName());

            if (client != null) {
                OAuth2AccessToken accessToken = client.getAccessToken();

                if (accessToken != null && accessToken.getExpiresAt() != null) {
                    if (Instant.now().isAfter(accessToken.getExpiresAt())) {
                        // Token is expired, clear the context
                        SecurityContextHolder.clearContext();
                    } else {
                        // Token is valid, set SecurityContextHolder
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
}

