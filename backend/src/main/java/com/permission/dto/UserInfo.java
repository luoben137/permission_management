package com.permission.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserInfo {
    private Long id;
    private String username;
    private Boolean enabled;
    private Set<String> roles;
    private Set<String> permissions;
}

