package com.cudev.demo_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserRoleDto {
    private String userName;

    private Set<String> roles;
}
