package com.netcracker.interserver.messages;

import com.netcracker.utils.QnaRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthenticationRequest {

    private String username;

    private String password;
}
