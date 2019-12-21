package com.netcracker.interserver.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Query {
    private String query;
    private UUID sendTo;
    private UUID filterId;
    private UUID id;
}
