package com.netcracker.service.repo;

import com.netcracker.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerRepo extends JpaRepository<Answer, UUID> {
}
