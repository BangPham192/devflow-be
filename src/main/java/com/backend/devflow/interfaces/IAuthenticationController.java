package com.backend.devflow.interfaces;

import com.backend.devflow.dtos.AuthTokenDto;
import com.backend.devflow.dtos.UserDto;
import com.backend.devflow.request.RefreshTokenRequest;
import com.backend.devflow.request.UserCreateRequest;
import com.backend.devflow.request.LoginRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public interface IAuthenticationController {

    @PostMapping("/login")
    AuthTokenDto login(@RequestBody LoginRequest request);

    @PostMapping("/user")
    ResponseEntity<Void> createUser(@RequestBody @Valid UserCreateRequest request);

    @PostMapping("/refresh-token")
    AuthTokenDto refreshToken(@RequestBody @Valid RefreshTokenRequest request);

    @GetMapping("/my-self")
    UserDto getMySelf();
}
