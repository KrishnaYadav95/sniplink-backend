package com.example.UrlShortener.controller;

import com.example.UrlShortener.model.User;
import com.example.UrlShortener.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    public UserDetailService userDetailService;

    @Autowired
    public AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        System.out.println("REGISTER ENDPOINT HIT: " + user.getUsername());
        return userDetailService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        System.out.println("LOGIN ENDPOINT HIT: " + user.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "Login successful";
    }
}