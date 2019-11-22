package com.netcracker.interserver.messages;

import com.netcracker.models.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeReply {
    private boolean subscribed;
    private Node node;
}
