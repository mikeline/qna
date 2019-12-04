package com.netcracker.services.service;

import com.netcracker.models.Tag;
import com.netcracker.models.Topic;
import com.netcracker.search.TopicSearch;
import com.netcracker.services.repo.TopicRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepo topicRepo;

    private final PostService postService;

    private final TopicSearch topicSearch;

    public Topic getOneTopic(UUID id) {
        return topicRepo.getOne(id);
    }

    public List<Topic> findAllTopics() {
        return topicRepo.findAll();
    }

    public Topic createTopic(Topic topic) {
        topic.setTopicPost(postService.getPostById(UUID.fromString(topic.getPostIdString())));
        return topicRepo.save(topic);
    }

    public Topic updateTopic(Topic topic) {
        return topicRepo.save(topic);
    }

    public void deleteTopicById(UUID id) {
        topicRepo.deleteById(id);
    }

    public List<Topic> searchTopics(String text) {
        return topicSearch.searchTopics(text);
    }

    public List<Topic> searchTags(Set<Tag> tags) {
        return topicRepo.searchTag(tags);
    }
}
