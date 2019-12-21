package com.netcracker.services.repo;

import com.netcracker.models.SearchResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SearchResultRepo extends JpaRepository<SearchResult, UUID> {
}
