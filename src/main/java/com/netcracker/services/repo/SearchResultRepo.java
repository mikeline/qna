package com.netcracker.services.repo;

import com.netcracker.models.SearchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SearchResultRepo extends JpaRepository<SearchResult, UUID> {

    @Query("delete from SearchResult where dateCreated <= :beginDate")
    void deleteOldRows(@Param("beginDate") LocalDateTime beginDate);
}
