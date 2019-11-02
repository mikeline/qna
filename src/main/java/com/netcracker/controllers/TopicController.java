package com.netcracker.controllers;


import com.netcracker.models.Topic;
import com.netcracker.services.repo.TopicRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RequiredArgsConstructor
@SuppressWarnings("unused")
@RestController
@RequestMapping("/topic")
public class TopicController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final TopicRepo topicRepo;

    @RequestMapping(value = "/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<Topic> get(@PathVariable("id") String id) {
        LOG.info("get request is received [id={}]", id);

        Topic res = topicRepo.getOne(UUID.fromString(id));

        return res == null ? new ResponseEntity<>(NOT_FOUND) : new ResponseEntity<>(res, OK);
    }

    @RequestMapping(value = "/", method = GET)
    @ResponseBody
    public ResponseEntity<Collection<Topic>> getAll() {
        LOG.info("getAll request is received.");

        return new ResponseEntity<>(topicRepo.findAll(), OK);
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public ResponseEntity<Topic> create(@RequestBody Topic topic) {
        LOG.info("create request is received [book={}]", topic);

        Topic res = topicRepo.save(topic);

        return new ResponseEntity<>(res, CREATED);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") String id) {
        LOG.info("delete request is received [id={}]", id);

        topicRepo.deleteById(UUID.fromString(id));

        return new ResponseEntity(NO_CONTENT);
    }

    @RequestMapping(method = PUT)
    @ResponseBody
    public ResponseEntity<Topic> update(@RequestBody Topic topic) {
        LOG.info("update request is received");

        Topic res = topicRepo.save(topic);

        return new ResponseEntity<>(res, OK);
    }
}
