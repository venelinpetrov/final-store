package com.vpe.finalstore.auth.config;

import com.vpe.finalstore.users.entities.User;
import com.vpe.finalstore.users.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;

    public User getCurrentuser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Integer) authentication.getPrincipal();

        return userRepository.findByUserId(userId).orElse(null);
    }
}
