package com.vpe.finalstore.product.repositories;

import com.vpe.finalstore.product.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
}