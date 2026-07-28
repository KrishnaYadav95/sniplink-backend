package com.example.UrlShortener.controller;

import com.example.UrlShortener.model.User;
import com.example.UrlShortener.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "https://sniplink-frontend1.vercel.app")
public class UserController {

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        User savedUser = userDetailService.register(user);

        return ResponseEntity.ok(savedUser);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                user.getPassword()
                        )
                );


        SecurityContextHolder.getContext()
                .setAuthentication(authentication);


        return ResponseEntity.ok(
                new LoginResponse(
                        "Login successful",
                        user.getUsername()
                )
        );
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