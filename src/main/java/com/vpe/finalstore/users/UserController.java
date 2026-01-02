package com.vpe.finalstore.users;

import com.vpe.finalstore.users.dtos.UserCreateDto;
import com.vpe.finalstore.users.dtos.UserDto;
import com.vpe.finalstore.users.enums.RoleEnum;
import com.vpe.finalstore.users.mappers.UserMapper;
import com.vpe.finalstore.users.repositories.RoleRepository;
import com.vpe.finalstore.users.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

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

        var userDto = userMapper.toDto(userEntity);
        var uri = uriBuilder
            .path("/users/{id}")
            .buildAndExpand(userDto.getUserId())
            .toUri();

        return ResponseEntity.created(uri).body(userDto);
    }
}
