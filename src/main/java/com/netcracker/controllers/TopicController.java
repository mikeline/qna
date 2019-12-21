package com.netcracker.controllers;


import com.netcracker.models.Tag;
import com.netcracker.models.Topic;
import com.netcracker.services.repo.TopicRepo;
import com.netcracker.services.service.NodeService;
import com.netcracker.services.service.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RestController
@RequestMapping("/topic")
public class TopicController {

    private final TopicService topicService;
    private final NodeService nodeService;

    @RequestMapping(value = "/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<Topic> get(@PathVariable("id") String id) {

        Topic res = topicService.getOneTopic(UUID.fromString(id));

        return res == null ? new ResponseEntity<>(NOT_FOUND) : new ResponseEntity<>(res, OK);
    }

    @RequestMapping(value = "/", method = GET)
    @ResponseBody
    public ResponseEntity<Collection<Topic>> getAll() {

        return new ResponseEntity<>(topicService.findAllTopics(), OK);
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public ResponseEntity<Topic> create(@RequestBody Topic topic) {
        topic.getTopicPost().setOwnerId(nodeService.getSelfUUID());
        Topic res = topicService.createTopic(topic);

        return new ResponseEntity<>(res, CREATED);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") String id) {

        topicService.deleteTopicById(UUID.fromString(id));

        return new ResponseEntity(NO_CONTENT);
    }

    @RequestMapping(method = PUT)
    @ResponseBody
    public ResponseEntity<Topic> update(@RequestBody Topic topic) {
        topic.getTopicPost().setOwnerId(nodeService.getSelfUUID());
        Topic res = topicService.updateTopic(topic);

        return new ResponseEntity<>(res, OK);
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODER') or hasRole('ROLE_CLIENT')")
//    @RequestMapping(value = "/search/{text}", method = GET)
//    @ResponseBody
//    public ResponseEntity<List<Topic>> search(@PathVariable("text") String text) {
//
//        List<Topic> res = topicService.searchTopics(text);
//
//        return new ResponseEntity<>(res, OK);
//    }

//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODER') or hasRole('ROLE_CLIENT')")
//    @RequestMapping(value = "/searchTag", method = GET)
//    @ResponseBody
//    public ResponseEntity<List<Topic>> searchTag(@RequestBody Set<Tag> tags) {
//        tags.stream().map(Tag::getName).collect(Collectors.toList());
//        return new ResponseEntity<>(topicService.searchTags(tags.toArray()), OK);
//    }
}
