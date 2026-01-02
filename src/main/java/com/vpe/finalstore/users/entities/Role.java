package com.vpe.finalstore.users.entities;

import com.vpe.finalstore.users.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private RoleEnum name;

    @Column(name = "description")
    private String description;

}