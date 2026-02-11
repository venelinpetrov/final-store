package com.vpe.finalstore.users.services;

import com.vpe.finalstore.customer.entities.Customer;
import com.vpe.finalstore.customer.repositories.CustomerRepository;
import com.vpe.finalstore.users.dtos.UserCreateDto;
import com.vpe.finalstore.users.entities.User;
import com.vpe.finalstore.users.enums.RoleEnum;
import com.vpe.finalstore.users.exceptions.RoleNotFoundException;
import com.vpe.finalstore.users.mappers.UserMapper;
import com.vpe.finalstore.users.repositories.RoleRepository;
import com.vpe.finalstore.users.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Collections;
import java.util.Set;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public User createUser(UserCreateDto data) {
        var userEntity = userMapper.toEntity(data);

        userEntity.setPasswordHash(passwordEncoder.encode(data.getPassword()));

        var role = roleRepository.getRoleByName(RoleEnum.USER).orElseThrow(RoleNotFoundException::new);
        userEntity.setRoles(Set.of(role));
        userEntity.setIsActive(true);
        userRepository.save(userEntity);

        Customer customer = new Customer();
        customer.setName(data.getName());
        customer.setPhone(data.getPhone());
        customer.setUser(userEntity);
        customerRepository.save(customer);

        userEntity.setCustomer(customer);

        return userEntity;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.emptyList()
        );
    }
}
