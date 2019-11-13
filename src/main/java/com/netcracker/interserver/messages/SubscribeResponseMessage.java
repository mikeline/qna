package com.netcracker.interserver.messages;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeResponseMessage {

    private boolean accepted;
}
