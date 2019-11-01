package com.netcracker.service.repo;

import com.netcracker.models.Node;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NodeRepo extends JpaRepository<Node, UUID> {
}
