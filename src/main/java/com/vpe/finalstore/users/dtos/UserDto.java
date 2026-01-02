package com.vpe.finalstore.users.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDto {
    private Integer userId;
    private String name;
    private String email;
}
