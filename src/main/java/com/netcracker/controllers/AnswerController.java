package com.netcracker.controllers;

import com.netcracker.models.Answer;
import com.netcracker.models.User;
import com.netcracker.services.repo.AnswerRepo;
import com.netcracker.services.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/answer")
public class AnswerController {

    private final AnswerRepo answerRepo;

    private final UserService userService;

    @RequestMapping(value = "/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<Answer> get(@PathVariable("id") String id) {

        Answer res = answerRepo.getOne(UUID.fromString(id));

        return res == null ? new ResponseEntity<>(NOT_FOUND) : new ResponseEntity<>(res, OK);
    }

    @RequestMapping(value = "/", method = GET)
    @ResponseBody
    public ResponseEntity<Collection<Answer>> getAll() {

        return new ResponseEntity<>(answerRepo.findAll(), OK);
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public ResponseEntity<Answer> create(@RequestBody Answer answer) {
        Answer res = answerRepo.save(answer);

        return new ResponseEntity<>(res, CREATED);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") String id) {

        answerRepo.deleteById(UUID.fromString(id));

        return new ResponseEntity(NO_CONTENT);
    }

    @RequestMapping(method = PUT)
    @ResponseBody
    public ResponseEntity<Answer> update(@RequestBody Answer answer) {

        Answer res = answerRepo.save(answer);

        return new ResponseEntity<>(res, OK);
    }

    @RequestMapping(value = "/correct/{id}", method = PUT)
    @ResponseBody
    public ResponseEntity makeCorrect(@PathVariable UUID id, HttpServletRequest req) {

        User user = userService.whoAmI(req);

        Answer answer = answerRepo.getOne(id);
        if(answer.getTopic().getTopicPost().getUser().getId() == user.getId())
        {
            answer.setCorrect(true);
            answerRepo.save(answer);
        }
        else
        {
            return new ResponseEntity(FORBIDDEN);
        }

        return new ResponseEntity(NO_CONTENT);
    }


}
