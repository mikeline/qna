package com.netcracker.controllers;

import com.netcracker.models.Comment;
import com.netcracker.services.repo.CommentRepo;
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
@RequestMapping("/comment")
public class CommentController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final CommentRepo commentRepo;

    @RequestMapping(value = "/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<Comment> get(@PathVariable("id") String id) {
        LOG.info("get request is received [id={}]", id);

        Comment res = commentRepo.getOne(UUID.fromString(id));

        return res == null ? new ResponseEntity<>(NOT_FOUND) : new ResponseEntity<>(res, OK);
    }

    @RequestMapping(value = "/", method = GET)
    @ResponseBody
    public ResponseEntity<Collection<Comment>> getAll() {
        LOG.info("getAll request is received.");

        return new ResponseEntity<>(commentRepo.findAll(), OK);
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public ResponseEntity<Comment> create(@RequestBody Comment comment) {
        LOG.info("create request is received [book={}]", comment);

        Comment res = commentRepo.save(comment);

        return new ResponseEntity<>(res, CREATED);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") String id) {
        LOG.info("delete request is received [id={}]", id);

        commentRepo.deleteById(UUID.fromString(id));

        return new ResponseEntity(NO_CONTENT);
    }

    @RequestMapping(method = PUT)
    @ResponseBody
    public ResponseEntity<Comment> update(@RequestBody Comment comment) {
        LOG.info("update request is received");

        Comment res = commentRepo.save(comment);

        return new ResponseEntity<>(res, OK);
    }
}
