package com.example.UrlShortener.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Base64;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    public SecretKey getKey(){
        byte[] decoded= Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decoded);
    }

    public String generateToken( String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60))
                .signWith(getKey())
                .compact();
    }

    public Claims getClaims(String token){
    return Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }


    public String getUsername(String token){
        return getClaims(token).getSubject();
    }
    public boolean isTokenValid(String token , UserDetails userDetails){
        final String username= getUsername(token);
        return username.equals(userDetails.getUsername()) && getClaims(token).getExpiration().after(new java.util.Date());
    }

    public boolean isTokenExpired(String token){
       return getClaims(token).getExpiration().before(new java.util.Date());
    }
    public java.util.Date getExpiration(String token){
        return getClaims(token).getExpiration();
    }
}
