package com.netcracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private UUID userId;

    private String fullName;

    private String username;

    private String email;

    private UUID ownerId;
}
