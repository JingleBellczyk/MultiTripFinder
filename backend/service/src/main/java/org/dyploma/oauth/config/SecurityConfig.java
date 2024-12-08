package org.dyploma.oauth.config;

import jakarta.servlet.http.HttpServletResponse;
import org.dyploma.oauth.domain.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserAccessFilter userAccessFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    @Value("${frontend.url}")
    private String frontendUrl;

    public SecurityConfig(UserAccessFilter userAccessFilter, CustomOAuth2UserService customOAuth2UserService) {
        this.userAccessFilter = userAccessFilter;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterAfter(userAccessFilter, OAuth2LoginAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/searchList/**", "/tripList/**", "/user/**", "/searchTag/**", "/tripTag/**", "/auth/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/search").permitAll()
                                .requestMatchers(HttpMethod.GET, "/user").hasRole("A")
                                .requestMatchers(HttpMethod.DELETE, "/user/**").hasRole("A")
                                .anyRequest().denyAll()
                )
                .oauth2Login(oauth2 -> {
                    oauth2.failureHandler((request, response, exception) -> {
                        response.sendRedirect(frontendUrl + "/loginFailed");
                    });
                    oauth2.defaultSuccessUrl(frontendUrl, true);
                    oauth2.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService));
                })
                .oauth2ResourceServer(auth -> auth.jwt(Customizer.withDefaults()))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID"))
                .oauth2ResourceServer(auth ->
                        auth
                                .jwt(Customizer.withDefaults())
                )
                .exceptionHandling(exception ->
                        exception
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                    response.setContentType("application/json");
                                    response.getWriter().write("{\"message\": \"Access Denied\", \"redirect\": \"/denied\"}");
                                })
                );

        return http.build();
    }

/*
            @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable) // Disable CSRF if needed, especially for stateless APIs
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().permitAll() // Allow all requests without authorization
                    );

            return http.build();
        }
*/


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:80", "http://frontend:80", "http://localhost", "http://mtf.norwayeast.cloudapp.azure.com"));
        configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "DELETE", "PUT"));
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("https://accounts.google.com");
    }
}
