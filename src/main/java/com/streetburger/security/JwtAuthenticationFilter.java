package com.streetburger.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.streetburger.model.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.streetburger.repository.UserRepository;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String phoneNumber = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("JwtAuthenticationFilter: JWT Token found: " + jwt);
            try {
                phoneNumber = jwtUtil.extractPhoneNumber(jwt);
                System.out.println("JwtAuthenticationFilter: Extracted Phone: " + phoneNumber);
            } catch (Exception e) {
                System.out.println("JwtAuthenticationFilter: Error extracting phone: " + e.getMessage());
            }
        } else {
            System.out.println(
                    "JwtAuthenticationFilter: No Bearer token found in header. Header: " + authorizationHeader);
        }

        if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<User> userOptional = userRepository.findByPhoneNumber(phoneNumber);

            if (userOptional.isPresent()) {
                if (jwtUtil.validateToken(jwt, phoneNumber)) {
                    User user = userOptional.get();
                    System.out.println("JwtAuthenticationFilter: Token valid. Setting authentication for user: "
                            + user.getPhoneNumber() + " Role: " + user.getRole());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("JwtAuthenticationFilter: Token validation failed");
                }
            } else {
                System.out.println("JwtAuthenticationFilter: User not found in DB for phone: " + phoneNumber);
            }
        }

        filterChain.doFilter(request, response);
    }
}
