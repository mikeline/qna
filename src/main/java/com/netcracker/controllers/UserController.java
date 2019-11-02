package com.netcracker.controllers;

import com.netcracker.models.User;
import com.netcracker.services.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
@SuppressWarnings("unused")
@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserRepo userRepo;

    @RequestMapping(value = "/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<User> get(@PathVariable("id") String id) {
        LOG.info("get request is received [id={}]", id);

        User res = userRepo.getOne(UUID.fromString(id));

        return res == null ? new ResponseEntity<>(NOT_FOUND) : new ResponseEntity<>(res, OK);
    }

    @RequestMapping(value = "/", method = GET)
    @ResponseBody
    public ResponseEntity<Collection<User>> getAll() {
        LOG.info("getAll request is received.");

        return new ResponseEntity<>(userRepo.findAll(), OK);
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public ResponseEntity<User> create(@RequestBody User user) {
        LOG.info("create request is received [book={}]", user);

        User res = userRepo.save(user);

        return new ResponseEntity<>(res, CREATED);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") String id) {
        LOG.info("delete request is received [id={}]", id);

        userRepo.deleteById(UUID.fromString(id));

        return new ResponseEntity(NO_CONTENT);
    }

    @RequestMapping(method = PUT)
    @ResponseBody
    public ResponseEntity<User> update(@RequestBody User user) {
        LOG.info("update request is received");

        User res = userRepo.save(user);

        return new ResponseEntity<>(res, OK);
    }

}
