package com.netcracker.utils;

import org.springframework.security.core.GrantedAuthority;

public enum QnaRole implements GrantedAuthority {
    ROLE_CLIENT,
    ROLE_MODER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
