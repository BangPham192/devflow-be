package com.backend.devflow.services;

import com.backend.devflow.dtos.UserDto;
import com.backend.devflow.mapper.UserMapper;
import com.backend.devflow.models.Role;
import com.backend.devflow.models.User;
import com.backend.devflow.models.UserRole;
import com.backend.devflow.repository.UserRepository;
import com.backend.devflow.repository.UserRoleRepository;
import com.backend.devflow.request.UserCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository  userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void createUser(UserCreateRequest request) {
        User user = this.userMapper.toUser(request);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setPublicId(UUID.randomUUID());
        userRepository.save(user);

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRoleName(Role.valueOf(request.getRole()));
        userRoleRepository.save(userRole);
    }

    public UserDto getMySelf(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        UserDto userDto = this.userMapper.toUserDto(user);
        userDto.setRoles(user.getUserRoles()
            .stream()
            .map(UserRole::getRoleName)
            .map(Role::name)
            .toList()
        );
        return userDto;
    }
}
