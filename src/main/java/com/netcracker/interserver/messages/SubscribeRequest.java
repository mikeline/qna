package com.netcracker.interserver.messages;

import com.netcracker.models.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeRequest {
    private Node node;
//    private

    enum SubEnum {
        SUBSCRIBE,
        UNSUBSCRIBE
    }
}
