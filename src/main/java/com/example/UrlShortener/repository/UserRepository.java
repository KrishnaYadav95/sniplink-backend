package com.example.UrlShortener.repository;

import com.example.UrlShortener.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}
