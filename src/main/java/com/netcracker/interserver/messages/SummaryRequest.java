package com.netcracker.interserver.messages;

import com.netcracker.models.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryRequest {
    private Node subscriber;
}
