package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    Set<Tag> findByNameIn(Set<String> names);
}