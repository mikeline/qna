package com.netcracker.controllers;

import com.netcracker.models.Node;
import com.netcracker.services.repo.NodeRepo;
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
@RequestMapping("/node")
public class NodeController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final NodeRepo nodeRepo;

    @RequestMapping(value = "/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<Node> get(@PathVariable("id") String id) {
        LOG.info("get request is received [id={}]", id);

        Node res = nodeRepo.getOne(UUID.fromString(id));

        return res == null ? new ResponseEntity<>(NOT_FOUND) : new ResponseEntity<>(res, OK);
    }

    @RequestMapping(value = "/", method = GET)
    @ResponseBody
    public ResponseEntity<Collection<Node>> getAll() {
        LOG.info("getAll request is received.");

        return new ResponseEntity<>(nodeRepo.findAll(), OK);
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public ResponseEntity<Node> create(@RequestBody Node node) {
        LOG.info("create request is received [book={}]", node);

        Node res = nodeRepo.save(node);

        return new ResponseEntity<>(res, CREATED);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") String id) {
        LOG.info("delete request is received [id={}]", id);

        nodeRepo.deleteById(UUID.fromString(id));

        return new ResponseEntity(NO_CONTENT);
    }

    @RequestMapping(method = PUT)
    @ResponseBody
    public ResponseEntity<Node> update(@RequestBody Node node) {
        LOG.info("update request is received");

        Node res = nodeRepo.save(node);

        return new ResponseEntity<>(res, OK);
    }
}
