package com.netcracker.interserver.messages;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class UserAuthenticationReply {

    private String token;

    public UserAuthenticationReply(String token) {
        this.token = token;
    }

}
