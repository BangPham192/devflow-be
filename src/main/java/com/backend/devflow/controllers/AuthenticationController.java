package com.backend.devflow.controllers;

import com.backend.devflow.dao.impl.RefreshTokenDaoImpl;
import com.backend.devflow.dtos.AuthTokenDto;
import com.backend.devflow.dtos.UserDto;
import com.backend.devflow.interfaces.IAuthenticationController;
import com.backend.devflow.models.User;
import com.backend.devflow.request.LoginRequest;
import com.backend.devflow.request.RefreshTokenRequest;
import com.backend.devflow.request.UserCreateRequest;
import com.backend.devflow.services.JwtService;
import com.backend.devflow.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController extends BaseController implements IAuthenticationController {
    private final UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenDaoImpl refreshTokenDao;

    @Autowired
    public AuthenticationController(UserService userService, JwtService jwtService, RefreshTokenDaoImpl refreshTokenDao) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenDao = refreshTokenDao;
    }

    @Override
    public AuthTokenDto login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            AuthTokenDto authTokenDto = new AuthTokenDto();
            authTokenDto.setAccessToken(jwtService.generateToken(request.getUsername(), 5 * 60 * 1000)); // 5 minutes in milliseconds
            authTokenDto.setRefreshToken(jwtService.generateToken(request.getUsername(), 7L * 24 * 60 * 60 * 1000)); // 7 days in milliseconds
            // store the refresh token in Redis
            refreshTokenDao.storeRefreshToken(authTokenDto.refreshToken, request.getUsername());
            return authTokenDto;
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> createUser(UserCreateRequest request) {
        try {
            // Email identifies a user; a second role is granted through users_roles,
            // never by creating another User row.
            if (userService.getUserByEmail(request.getEmail()) != null) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            userService.createUser(request);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public AuthTokenDto refreshToken(RefreshTokenRequest request) {
        try {
            String userName = jwtService.extractUsername(request.getRefreshToken());
            // add logic validate token from redis
            String refreshTokenFromRedis = refreshTokenDao.getRefreshTokenByUsername(userName);
            if (refreshTokenFromRedis == null || !refreshTokenFromRedis.equals(request.getRefreshToken())) {
                throw new RuntimeException("Invalid refresh token");
            }
            User user = userService.getUserByEmail(userName);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            // generate token
            String newToken =  jwtService.generateToken(userName, 5L * 60 * 1000); // 5 minutes
            String newRefreshToken = jwtService.generateToken(userName, 7 * 24 * 60 * 60 * 1000);
            AuthTokenDto authTokenDto = new AuthTokenDto();
            authTokenDto.setAccessToken(newToken);
            authTokenDto.setRefreshToken(newRefreshToken);

            // store refresh token
            refreshTokenDao.storeRefreshToken(authTokenDto.refreshToken, userName);
            return authTokenDto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public UserDto getMySelf() {
        UserDetails currentUser = getCurrentUser();
        return userService.getMySelf(currentUser.getUsername());
    }
}
