package com.netcracker.services.repo;

import com.netcracker.models.UserPostVote;
import com.netcracker.models.UserPostVoteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPostVoteRepo extends JpaRepository<UserPostVote, UserPostVoteId> {
}
