package org.dyploma.oauth.config;

import jakarta.servlet.http.HttpServletResponse;
import org.dyploma.useraccount.UserAccount;
import org.dyploma.useraccount.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.dyploma.oauth.domain.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserAccessFilter userAccessFilter;
    private final CustomOAuth2UserService customOAuth2UserService;

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
                                .requestMatchers("/searchList/**", "/tripList/**", "/user/**", "/searchTag/**", "tripTag/**", "/auth/**").authenticated()
                                .requestMatchers(HttpMethod.POST, "/search").permitAll()

/*                        .requestMatchers(HttpMethod.OPTIONS, "/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/auth/**", "/searchList/**", "/searchTag/**", "/tripList/**", "/tripTag/**").authenticated()
                        .requestMatchers(HttpMethod.POST,"/searchList/**", "/searchTag/**", "/tripList/**", "/tripTag/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/searchList/**", "/searchTag/**", "/tripList/**", "/tripTag/**","/user").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/searchList/**", "/searchTag/**", "/tripList/**", "/tripTag/**").authenticated()*/
                                .requestMatchers(HttpMethod.GET, "/user").hasRole("A")
                                .requestMatchers(HttpMethod.DELETE, "/user/**").hasRole("A")
/*                        .requestMatchers(HttpMethod.GET, "/user").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/user/**").hasAuthority("ROLE_ADMIN")*/
                                .anyRequest().denyAll()
                )
/*                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("http://localhost:3000/", true)
                        .successHandler((request, response, authentication) -> {
                            OAuth2AuthenticationToken oauth2Authentication = (OAuth2AuthenticationToken) authentication;
                            Collection<GrantedAuthority> authorities = mapAuthorities(oauth2Authentication);
                            Authentication newAuthentication = new OAuth2AuthenticationToken(
                                    oauth2Authentication.getPrincipal(),
                                    authorities,
                                    oauth2Authentication.getAuthorizedClientRegistrationId()
                            );

                            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
                            Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
                            System.out.println("Current Authentication: " + currentAuthentication);
                            System.out.println("Authorities: " + currentAuthentication.getAuthorities());

                            response.sendRedirect("http://localhost:3000/");
                        })
                )*/
                .oauth2Login(oauth2 -> {
                    oauth2.defaultSuccessUrl("http://localhost:3000/", true);
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

/*        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable) // Disable CSRF if needed, especially for stateless APIs
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().permitAll() // Allow all requests without authorization
                    );

            return http.build();
        }*/
    }
/*    private final UserAccountRepository userAccountRepository;

    @Autowired
    public SecurityConfig(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    // Modified to take OAuth2AuthenticationToken as a parameter
    private Collection<GrantedAuthority> mapAuthorities(OAuth2AuthenticationToken authentication) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        OAuth2User oAuth2User = authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        UserAccount userAccount = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Character userRole = userAccount.getRole();
        if (userRole == 'A') {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        else if (userRole == 'U'){
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return authorities;
    }*/


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
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
