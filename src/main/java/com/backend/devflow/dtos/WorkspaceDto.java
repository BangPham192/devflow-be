package com.backend.devflow.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceDto {
    private UUID publicId;
    private String name;
    private String description;
    private String status;
    private UserDto user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
