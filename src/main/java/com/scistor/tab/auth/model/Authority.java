package com.scistor.tab.auth.model;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    ADMIN,
    USER,
    GUEST;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
