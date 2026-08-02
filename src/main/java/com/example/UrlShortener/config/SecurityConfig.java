package com.example.UrlShortener.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                "/user/register",
                                "/user/login"
                        ).permitAll()

                        // The frontend calls this on load to ask "is there a
                        // session?". Must be reachable while anonymous so it can
                        // answer 401 instead of being blocked by the filter chain.
                        .requestMatchers("/user/me").permitAll()

                        .requestMatchers(
                                "/oauth2/**",
                                "/login/**"
                        ).permitAll()

                        // Following a short link must work for anyone, logged in
                        // or not. That is the whole point of a URL shortener.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/url/original-url/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                /*
                 * THE core fix.
                 *
                 * By default Spring Security answers an unauthenticated request
                 * by redirecting to the login page -- and with oauth2Login that
                 * means a 302 towards the OAuth provider. The browser's fetch()
                 * follows it, lands on accounts.google.com, which sends no
                 * Access-Control-Allow-Origin header, so the browser aborts. The
                 * frontend only sees an opaque network failure, indistinguishable
                 * from a dead server -- exactly why it kept reporting
                 * "backend not running".
                 */
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                )

                .oauth2Login(oauth -> oauth
                        .successHandler((request, response, authentication) ->
                                response.sendRedirect(frontendUrl + "/?login=success")
                        )
                        .failureHandler((request, response, exception) ->
                                response.sendRedirect(frontendUrl + "/?error=oauth")
                        )
                )

                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.NO_CONTENT.value())
                        )
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Must match the frontend origin exactly: scheme + host, no trailing
        // slash and no path. A trailing slash silently never matches.
        configuration.setAllowedOrigins(List.of(frontendUrl));

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(List.of("*"));

        // Required for the browser to store and send JSESSIONID cross-site.
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}