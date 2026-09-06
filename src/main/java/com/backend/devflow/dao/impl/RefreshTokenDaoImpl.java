package com.backend.devflow.dao.impl;

import com.backend.devflow.dao.IRefreshTokenDao;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenDaoImpl implements IRefreshTokenDao {
    private static final String HASH_REFERENCE = "refreshToken";

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOperations;

    @Override
    public String getRefreshTokenByUsername(String username) {
        return hashOperations.get(HASH_REFERENCE, username);
    }

    @Override
    public void storeRefreshToken(String token, String username) {
        hashOperations.put(HASH_REFERENCE, username, token);
    }

    @Override
    public void deleteRefreshToken(String username) {
        hashOperations.delete(HASH_REFERENCE, username);
    }
}
