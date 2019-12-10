package com.netcracker.services.repo;

import com.netcracker.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepo extends JpaRepository<Tag, String> {

}
