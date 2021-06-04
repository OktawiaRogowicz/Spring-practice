package com.example.accessingdatamysql.enumeration;

import lombok.Getter;

import static com.example.accessingdatamysql.constant.Authority.*;

@Getter
public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES);

    private String[] authorities;

    Role(String ... authorities) {
        this.authorities = authorities;
    }

}
