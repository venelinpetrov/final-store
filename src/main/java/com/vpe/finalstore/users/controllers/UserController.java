package com.vpe.finalstore.users.controllers;

import com.vpe.finalstore.customer.repositories.CustomerRepository;
import com.vpe.finalstore.users.dtos.UserCreateDto;
import com.vpe.finalstore.users.dtos.UserDto;
import com.vpe.finalstore.users.dtos.UserUpdateDto;
import com.vpe.finalstore.users.exceptions.UserNotFoundException;
import com.vpe.finalstore.users.mappers.UserMapper;
import com.vpe.finalstore.users.repositories.UserRepository;
import com.vpe.finalstore.users.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CustomerRepository customerRepository;
    private final UserService userService;


    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateDto data, UriComponentsBuilder uriBuilder) {
        // Note that we don't need to check for user existence beforehand as this is handled
        // by the unique constraint on user.email column
        var user = userService.createUser(data);

        var userDto = userMapper.toDto(user);
        var uri = uriBuilder
            .path("/users/{id}")
            .buildAndExpand(userDto.getUserId())
            .toUri();

        return ResponseEntity.created(uri).body(userDto);
    }

    @PreAuthorize("hasAuthority(T(com.vpe.finalstore.users.enums.RoleEnum).ADMIN.authority()) or #userId == authentication.principal")
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UserUpdateDto data
    ) {
        var user = userRepository.findByUserId(userId).orElseThrow(UserNotFoundException::new);

        user.getCustomer().setName(data.getName());
        user.setEmail(data.getEmail());
        user.getCustomer().setPhone(data.getPhone());

        userRepository.save(user);
        customerRepository.save(user.getCustomer());

        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
