package com.netcracker.utils;

import org.springframework.security.core.GrantedAuthority;

public enum QnaRole implements GrantedAuthority {
    ordinary,
    moderator,
    administrator;

    @Override
    public String getAuthority() {
        return name();
    }
}
