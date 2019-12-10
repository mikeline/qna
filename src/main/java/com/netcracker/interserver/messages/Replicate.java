package com.netcracker.interserver.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Replicate {
    private ReplicationOperation replicationOperation;
    private List<? extends Replicable> data; //станет ли ломбок копировать список?
}
