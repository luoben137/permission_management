package com.permission.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private Boolean enabled;
    private Set<Long> roleIds;
}

