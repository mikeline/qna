package com.netcracker.services.repo;

import com.netcracker.models.Tag;
import com.netcracker.models.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface TopicRepo extends JpaRepository<Topic, UUID> {
//
//    @Query("select topic from Topic topic left join topic.tags tags where tags.id in :tags")
//    List<Topic> searchTag(Set<String> tags);
}
