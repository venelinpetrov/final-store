package com.vpe.finalstore.users.enums;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum RoleEnum {
    USER,
    ADMIN;

    public String role() {
        return name();
    }

    public String authority() {
        return "ROLE_" + name();
    }

    public GrantedAuthority toAuthority() {
        return new SimpleGrantedAuthority("ROLE_" + name());
    }
}
