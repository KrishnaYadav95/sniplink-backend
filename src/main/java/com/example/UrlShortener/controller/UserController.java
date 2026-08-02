package com.example.UrlShortener.controller;

import com.example.UrlShortener.model.User;
import com.example.UrlShortener.service.UserDetailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /*
     * Writes the authenticated SecurityContext into the HttpSession so that the
     * JSESSIONID cookie actually maps to a logged-in user on later requests.
     */
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        User savedUser = userDetailService.register(user);

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                user.getPassword()
                        )
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        /*
         * This line was missing, and without it form login silently did nothing.
         *
         * In Spring Security 6 SecurityContextHolder is a request-scoped
         * ThreadLocal cleared once the response is written. Setting the
         * Authentication on it does NOT persist anything, so the very next
         * request arrived anonymous even though login had "succeeded".
         */
        securityContextRepository.saveContext(
                new SecurityContextImpl(authentication), request, response);

        return ResponseEntity.ok(
                new LoginResponse("Login successful", user.getUsername())
        );
    }

    /**
     * Lets the frontend discover an existing session.
     *
     * This is the endpoint the frontend needs after an OAuth redirect: the
     * provider sends the whole browser back to the SPA, destroying all in-memory
     * JavaScript state, so asking the backend is the only way to learn who just
     * logged in.
     */
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        boolean anonymous = authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken;

        if (anonymous) {
            return ResponseEntity.status(401).body(Map.of("authenticated", false));
        }

        return ResponseEntity.ok(
                Map.of("authenticated", true, "username", resolveUsername(authentication))
        );
    }

    /*
     * Google exposes the identity as "email"; GitHub uses "login" and may not
     * return an email at all. Form login has no attributes, just a name.
     */
    private String resolveUsername(Authentication authentication) {

        if (authentication.getPrincipal() instanceof OAuth2User oauthUser) {

            Map<String, Object> attributes = oauthUser.getAttributes();

            Object candidate = attributes.get("email");
            if (candidate == null) candidate = attributes.get("login");
            if (candidate == null) candidate = attributes.get("name");

            if (candidate != null) return candidate.toString();
        }

        return authentication.getName();
    }

    static class LoginResponse {

        private String message;
        private String username;

        public LoginResponse(String message, String username) {
            this.message = message;
            this.username = username;
        }

        public String getMessage() {
            return message;
        }

        public String getUsername() {
            return username;
        }
    }
}