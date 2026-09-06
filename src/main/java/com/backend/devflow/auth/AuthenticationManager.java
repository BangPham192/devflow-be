package com.backend.devflow.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticationManager {

    public static UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("Authentication object is null");
        }

        if (!authentication.isAuthenticated()) {
            throw new RuntimeException("Authentication object is not authenticated");
        }

        if (!(authentication.getPrincipal() instanceof  UserDetails)) {
            throw new RuntimeException("Authentication object is not instance of UserDetails");
        }

        return (UserDetails) authentication.getPrincipal();
    }
}
