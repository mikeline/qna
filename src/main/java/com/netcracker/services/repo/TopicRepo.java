package com.netcracker.services.repo;

import com.netcracker.models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TopicRepo extends JpaRepository<Topic, UUID> {

}
