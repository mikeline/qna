package com.netcracker.services.service;

import com.netcracker.models.Post;
import com.netcracker.models.Tag;
import com.netcracker.models.Topic;
import com.netcracker.models.User;
import com.netcracker.search.GeneralSearch;
import com.netcracker.services.repo.PostRepo;
import com.netcracker.services.repo.TopicRepo;
import lombok.RequiredArgsConstructor;
import javax.persistence.Query;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TopicService {
    @PersistenceContext
    private final EntityManager entityManager;

    private final TopicRepo topicRepo;

    private final PostRepo postRepo;

    private final PostService postService;

    private final GeneralSearch generalSearch;

    public Topic getOneTopic(UUID id) {
        return topicRepo.getOne(id);
    }

    public List<Topic> findAllTopics() {
        return topicRepo.findAll();
    }

    public Topic createTopic(Topic topic, User user) {
        topic.getTopicPost().setUser(user);
        return topicRepo.save(topic);
    }

    public Topic updateTopic(Topic topic) {
        return topicRepo.save(topic);
    }

    public void deleteTopicById(UUID id) {
        topicRepo.deleteById(id);
    }

    public List<Topic> getLimitedTopics(int pageNumber, int pageSize) {
        Query query = entityManager.createQuery("from Topic", Topic.class);
        query.setFirstResult((pageNumber - 1) * 20).setMaxResults(pageSize);

        return query.getResultList();

    }

    public List<Topic> searchTags(String[] tags) {
        return topicRepo.searchTag(new HashSet<String>(Arrays.asList(tags)));
    }
}
