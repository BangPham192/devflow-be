package com.backend.devflow.dao;

public interface IRefreshTokenDao {
    String getRefreshTokenByUsername(String username);
    void storeRefreshToken(String token, String username);
    void deleteRefreshToken(String username);
}
