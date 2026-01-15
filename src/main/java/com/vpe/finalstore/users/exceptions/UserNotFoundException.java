package com.vpe.finalstore.users.exceptions;

import com.vpe.finalstore.exceptions.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("User not found");
    }
}
