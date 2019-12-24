package com.netcracker.services.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcracker.dto.*;
import com.netcracker.models.Post;
import com.netcracker.models.SearchResult;
import com.netcracker.models.Topic;
import com.netcracker.models.User;
import com.netcracker.services.repo.SearchResultRepo;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.Collection;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.Comment;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@Service
@RequiredArgsConstructor
public class ScreenService {

    @PersistenceContext
    private final EntityManager entityManager;

    private final UserService userService;

    private final PostService postService;

    private final TopicService topicService;

    private final SearchResultRepo searchResultRepo;

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    @Transactional
    public QuestionListDto getQuestions(int page) {

        List<Topic> topics = topicService.getLimitedTopics(page, 20);

        return convertTopicsToQuestionListDto(topics);
    }

    public QuestionListDto convertTopicsToQuestionListDto(List<Topic> topics) {
        QuestionListDto questionListDto = new QuestionListDto();
        for (Topic t: topics) {
            User user = t.getTopicPost().getUser();
            UserDto userDto = modelMapper.map(user, UserDto.class);
            QuestionDto questionDto = new QuestionDto(
                    t.getId(),
                    t.getTitle(),
                    modelMapper.map(t.getTopicPost(), PostDto.class),
                    userDto,
                    t.getAnswers().size());
            questionListDto.getQuestions().add(questionDto);
        }
        return questionListDto;
    }


    public ResponseEntity<UUID> getLatestQuestionsId() {

        UUID resultId = UUID.randomUUID();
        SearchResult searchResult = new SearchResult();
        searchResult.setId(resultId);
        searchResultRepo.save(searchResult);

        return new ResponseEntity<>(resultId, OK);
    }

    public ResponseEntity<QuestionListDto> getLatestQuestions(UUID searchId) {

        Query query = entityManager.createQuery("select s.result from SearchResult s where s.searchId = :searchId order by s.lastUpdated desc",
                SearchResult.class).setParameter("searchId", searchId);
        query.setFirstResult(0).setMaxResults(100);

        List<QuestionDto> questions = (List<QuestionDto>) query.getResultList().stream()
                                                           .map(res -> {
                                                               try {
                                                                   return objectMapper.readValue(res.toString(), QuestionDto.class);
                                                               } catch (JsonProcessingException e) {
                                                                   e.printStackTrace();
                                                                   return res;
                                                               }
                                                           })
                                                           .collect(Collectors.toList());

        QuestionListDto questionListDto = new QuestionListDto(questions);

        return new ResponseEntity<>(questionListDto, OK);
    }

    @Transactional
    public TreadDto getTread(UUID id) {

        Topic t = topicService.getOneTopic(id);

        User user = t.getTopicPost().getUser();
        UserDto userDto = modelMapper.map(user, UserDto.class);
        QuestionDto questionDto = new QuestionDto(
                t.getId(),
                t.getTitle(),
                modelMapper.map(t.getTopicPost(), PostDto.class),
                userDto,
                t.getAnswers().size());

        List<AnswerDto> answersDto = new ArrayList();
        answersDto.addAll(
                t.getAnswers().stream()
                        .map(answer -> {
                           AnswerDto answerDto = new AnswerDto();
                           answerDto.setPostDto(modelMapper.map(answer.getAnswerPost(), PostDto.class));
                           answerDto.setAuthor(modelMapper.map(answer.getAnswerPost().getUser(), UserDto.class));
                           answerDto.setCommentListDto(
                                   answer.getAnswerPost().getComments().stream().map(comment -> {
                                       CommentDto commentDto = new CommentDto();
                                       commentDto.setAuthor(modelMapper.map(comment.getCommentPost().getUser(), UserDto.class));
                                       commentDto.setPostDto(modelMapper.map(comment.getCommentPost(), PostDto.class));
                                       return commentDto;
                                   }).collect(Collectors.toList()));
                           return answerDto;
                        }).collect(Collectors.toList())
        );

        List<CommentDto> commentsDto = new ArrayList<>();
        commentsDto.addAll(
                t.getTopicPost().getComments().stream()
                .map(comment -> {
                    CommentDto commentDto = new CommentDto();
                    commentDto.setAuthor(modelMapper.map(comment.getCommentPost().getUser(), UserDto.class));
                    commentDto.setPostDto(modelMapper.map(comment.getCommentPost(), PostDto.class));
                    return commentDto;
                }).collect(Collectors.toList())
        );

        TreadDto treadDto = new TreadDto(questionDto, commentsDto, answersDto);

        return treadDto;
    }

}
