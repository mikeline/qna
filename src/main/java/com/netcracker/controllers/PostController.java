package com.netcracker.controllers;


import com.netcracker.models.Post;
import com.netcracker.services.repo.PostRepo;
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

@SuppressWarnings("unused")
@RestController
@RequestMapping("/post")
public class PostController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Resource
    private PostRepo postRepo;

    @RequestMapping(value = "/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<Post> get(@PathVariable("id") String id) {
        LOG.info("get request is received [id={}]", id);

        Post res = postRepo.getOne(UUID.fromString(id));

        return res == null ? new ResponseEntity<>(NOT_FOUND) : new ResponseEntity<>(res, OK);
    }

    @RequestMapping(value = "/", method = GET)
    @ResponseBody
    public ResponseEntity<Collection<Post>> getAll() {
        LOG.info("getAll request is received.");

        return new ResponseEntity<>(postRepo.findAll(), OK);
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public ResponseEntity<Post> create(@RequestBody Post post) {
        LOG.info("create request is received [book={}]", post);

        Post res = postRepo.save(post);

        return new ResponseEntity<>(res, CREATED);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") String id) {
        LOG.info("delete request is received [id={}]", id);

        postRepo.deleteById(UUID.fromString(id));

        return new ResponseEntity(NO_CONTENT);
    }

    @RequestMapping(method = PUT)
    @ResponseBody
    public ResponseEntity<Post> update(@RequestBody Post post) {
        LOG.info("update request is received");

        Post res = postRepo.save(post);

        return new ResponseEntity<>(res, OK);
    }

}
