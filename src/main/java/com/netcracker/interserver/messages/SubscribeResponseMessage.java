package com.netcracker.interserver.messages;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SubscribeResponseMessage {

    final boolean accepted;
}
