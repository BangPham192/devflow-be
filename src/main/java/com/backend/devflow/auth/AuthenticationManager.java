package com.backend.devflow.auth;

import com.backend.devflow.exception.ExceptionHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticationManager {

    public static UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            ExceptionHelper.UNAUTHORIZED.throwCustomException("User is not authenticated", null);
        }

        if (!authentication.isAuthenticated()) {
            ExceptionHelper.UNAUTHORIZED.throwCustomException("User is not authenticated", null);
        }

        if (!(authentication.getPrincipal() instanceof  UserDetails)) {
            ExceptionHelper.UNAUTHORIZED.throwCustomException("User is not authenticated", null);
        }

        return (UserDetails) authentication.getPrincipal();
    }
}
