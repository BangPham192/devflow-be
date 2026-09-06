package com.backend.devflow.controllers;

import com.backend.devflow.auth.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;

public class BaseController {
    public static UserDetails getCurrentUser() {
        return AuthenticationManager.getCurrentUser();
    }
}
