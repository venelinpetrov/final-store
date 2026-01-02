package com.vpe.finalstore.users.repositories;

import com.vpe.finalstore.users.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    public Boolean existsByEmail(String email);
    public Optional<User> findByEmail(String email);
}