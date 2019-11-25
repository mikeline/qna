package com.netcracker.interserver.messages;

import com.netcracker.utils.QnaRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class UserAuthenticationRequest {

    private String username;

    private String password;

    public UserAuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
