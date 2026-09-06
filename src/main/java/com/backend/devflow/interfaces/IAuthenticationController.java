package com.backend.devflow.interfaces;

import com.backend.devflow.dtos.AuthTokenDto;
import com.backend.devflow.request.UserCreateRequest;
import com.backend.devflow.request.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public interface IAuthenticationController {

    @PostMapping("/login")
    AuthTokenDto login(@RequestBody LoginRequest request);

    @PostMapping("/user")
    ResponseEntity<Void> createUser(@RequestBody @Valid UserCreateRequest request);
}
