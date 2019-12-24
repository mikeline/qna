package com.netcracker.controllers;

import com.netcracker.dto.QuestionListDto;
import com.netcracker.dto.TreadDto;
import com.netcracker.models.Topic;
import com.netcracker.services.service.ScreenService;
import com.netcracker.services.service.TopicService;
import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RestController
@RequestMapping("/")
public class ScreenController {

    private final ScreenService screenService;
    private final TopicService topicService;

    @GetMapping(value = "questions", params = "page")
    @ResponseBody
    public ResponseEntity<QuestionListDto> getQuestions(@RequestParam int page) {

        QuestionListDto questions = screenService.getQuestions(page);

        return new ResponseEntity<>(questions, OK);
    }

    @GetMapping("question/{id}")
    @ResponseBody
    public ResponseEntity<TreadDto> getTread(@PathVariable String id) {

        TreadDto tread = screenService.getTread(UUID.fromString(id));

        return new ResponseEntity<>(tread, OK);
    }

    @GetMapping(value = "questions", params = "tags")
    @ResponseBody
    public ResponseEntity<List<Topic>> getPostsByTags(@RequestParam String[] tags) {

        List<Topic> topics = topicService.searchTags(tags);

        return new ResponseEntity<>(topics, OK);
    }

    @GetMapping(value = "questions/latest")
    @ResponseBody
    public ResponseEntity<UUID> getLatestQuestionsId() {
        return screenService.getLatestQuestionsId();
    }

    @GetMapping(value = "questions/latest/{id}")
    @ResponseBody
    public ResponseEntity<QuestionListDto> getLatestQuestions(@PathVariable String id) {
        return screenService.getLatestQuestions(UUID.fromString(id));
    }


}
