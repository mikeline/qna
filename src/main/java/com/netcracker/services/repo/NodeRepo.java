package com.netcracker.services.repo;

import com.netcracker.models.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface NodeRepo extends JpaRepository<Node, UUID> {
    @Query("select n from Node n where n.nodeRole = 'SELF'")
    List<Node> findByNodeRoleSelf();

    @Query("select n from Node n where n.nodeRole = 'SUBSCRIBER'")
    List<Node> findByNodeRoleSubscriber();

    @Query("select n from Node n where n.nodeRole = 'PRODUCER'")
    List<Node> findByNodeRoleProducer();
}
