package com.vpe.finalstore.tags.repositories;

import com.vpe.finalstore.tags.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
}