package com.vpe.finalstore.users.exceptions;

import com.vpe.finalstore.exceptions.NotFoundException;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException() {
        super("Role not found");
    }
}
