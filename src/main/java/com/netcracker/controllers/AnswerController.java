package com.netcracker.controllers;

import com.netcracker.models.Answer;
import com.netcracker.services.repo.AnswerRepo;
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
@RequestMapping("/answer")
public class AnswerController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Resource
    private AnswerRepo answerRepo;

    @RequestMapping(value = "/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<Answer> get(@PathVariable("id") String id) {
        LOG.info("get request is received [id={}]", id);

        Answer res = answerRepo.getOne(UUID.fromString(id));

        return res == null ? new ResponseEntity<>(NOT_FOUND) : new ResponseEntity<>(res, OK);
    }

    @RequestMapping(value = "/", method = GET)
    @ResponseBody
    public ResponseEntity<Collection<Answer>> getAll() {
        LOG.info("getAll request is received.");

        return new ResponseEntity<>(answerRepo.findAll(), OK);
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public ResponseEntity<Answer> create(@RequestBody Answer answer) {
        LOG.info("create request is received [book={}]", answer);

        Answer res = answerRepo.save(answer);

        return new ResponseEntity<>(res, CREATED);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") String id) {
        LOG.info("delete request is received [id={}]", id);

        answerRepo.deleteById(UUID.fromString(id));

        return new ResponseEntity(NO_CONTENT);
    }

    @RequestMapping(method = PUT)
    @ResponseBody
    public ResponseEntity<Answer> update(@RequestBody Answer answer) {
        LOG.info("update request is received");

        Answer res = answerRepo.save(answer);

        return new ResponseEntity<>(res, OK);
    }
}
