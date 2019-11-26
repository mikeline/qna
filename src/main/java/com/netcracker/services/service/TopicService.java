package com.netcracker.services.service;

import com.netcracker.models.Topic;
import com.netcracker.search.GeneralSearch;
import com.netcracker.services.repo.TopicRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepo topicRepo;

    private final PostService postService;

    private final GeneralSearch generalSearch;

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

}
