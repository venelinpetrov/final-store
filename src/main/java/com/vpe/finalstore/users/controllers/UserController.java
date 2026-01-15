package com.vpe.finalstore.users.controllers;

import com.vpe.finalstore.customer.entities.Customer;
import com.vpe.finalstore.customer.repositories.CustomerRepository;
import com.vpe.finalstore.users.dtos.UserCreateDto;
import com.vpe.finalstore.users.dtos.UserDto;
import com.vpe.finalstore.users.dtos.UserUpdateDto;
import com.vpe.finalstore.users.enums.RoleEnum;
import com.vpe.finalstore.users.exceptions.UserNotFoundException;
import com.vpe.finalstore.users.mappers.UserMapper;
import com.vpe.finalstore.users.repositories.RoleRepository;
import com.vpe.finalstore.users.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;


    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserCreateDto data, UriComponentsBuilder uriBuilder) {
        // TODO abstract this into a service

        if (userRepository.existsByEmail(data.getEmail())) {
            return ResponseEntity.badRequest().build();
        }

        var userEntity = userMapper.toEntity(data);

        userEntity.setPasswordHash(passwordEncoder.encode(data.getPassword()));

        // TODO Get OR create the role
        var role = roleRepository.getRoleByName(RoleEnum.USER);
        userEntity.setRoles(Set.of(role));
        userRepository.save(userEntity);

        Customer customer = new Customer();
        customer.setName(data.getName());
        customer.setPhone(data.getPhone());
        customer.setUser(userEntity);
        customerRepository.save(customer);

        var userDto = userMapper.toDto(userEntity);
        var uri = uriBuilder
            .path("/users/{id}")
            .buildAndExpand(userDto.getUserId())
            .toUri();

        return ResponseEntity.created(uri).body(userDto);
    }

    @PreAuthorize("hasAuthority(T(com.vpe.finalstore.users.enums.RoleEnum).ADMIN.authority()) or #userId == authentication.principal")
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("userId") Integer userId,
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
