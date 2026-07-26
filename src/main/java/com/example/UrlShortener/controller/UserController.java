package com.example.UrlShortener.controller;

import com.example.UrlShortener.model.User;
import com.example.UrlShortener.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    public UserDetailService userDetailService;

    @PostMapping("/register")
    public User register(@RequestBody User user){
       return  userDetailService.register(user);
    }
}
