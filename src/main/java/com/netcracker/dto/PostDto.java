package com.netcracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private String body;

    private LocalDateTime dateCreated;

    private LocalDateTime dateUpdated;

    private UUID id;

    private UUID ownerId;
}
