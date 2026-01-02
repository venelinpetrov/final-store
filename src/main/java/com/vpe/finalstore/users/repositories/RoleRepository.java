package com.vpe.finalstore.users.repositories;

import com.vpe.finalstore.users.entities.Role;
import com.vpe.finalstore.users.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role getRoleByName(RoleEnum name);
}