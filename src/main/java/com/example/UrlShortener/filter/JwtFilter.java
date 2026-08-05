package com.example.UrlShortener.filter;
import com.example.UrlShortener.service.UserDetailService;
import com.example.UrlShortener.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {


    private final JwtUtil jwtUtil;

    private final UserDetailService userDetailService;
   //if you make jwtutil and userdetailservice private , final then this below constructor injection get automatically implemeted

    public JwtFilter(JwtUtil jwtUtil, UserDetailService userDetailService) {
        this.jwtUtil = jwtUtil;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
try {
    String readHeader = request.getHeader("Authorization");

    if (readHeader != null && readHeader.startsWith("Bearer ")) {
        String token = readHeader.substring(7);
        String username = jwtUtil.getUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailService.loadUserByUsername(username);
            if (jwtUtil.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }
    filterChain.doFilter(request, response);
} catch (Exception e) {
    SecurityContextHolder.clearContext();
    filterChain.doFilter(request, response);
}
    }
}
