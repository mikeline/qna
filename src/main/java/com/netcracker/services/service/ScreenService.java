package com.netcracker.services.service;

import com.netcracker.dto.*;
import com.netcracker.models.Post;
import com.netcracker.models.Topic;
import com.netcracker.models.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.w3c.dom.Comment;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreenService {

    private final UserService userService;

    private final PostService postService;

    private final TopicService topicService;

    private final ModelMapper modelMapper;

    @Transactional
    public QuestionListDto getQuestions(int page) {

        QuestionListDto questionListDto = new QuestionListDto();

        List<Topic> topics = topicService.getLimitedTopics(page, 20);

        for (Topic t: topics) {
            User user = t.getTopicPost().getUser();
            UserDto userDto = modelMapper.map(user, UserDto.class);
            QuestionDto questionDto = new QuestionDto(
                    t.getTopicId(),
                    t.getTitle(),
                    modelMapper.map(t.getTopicPost(), PostDto.class),
                    userDto,
                    t.getAnswers().size());
            questionListDto.getQuestions().add(questionDto);
        }

        return questionListDto;
    }

    @Transactional
    public TreadDto getTread(UUID id) {

        Topic t = topicService.getOneTopic(id);

        User user = t.getTopicPost().getUser();
        UserDto userDto = modelMapper.map(user, UserDto.class);
        QuestionDto questionDto = new QuestionDto(
                t.getTopicId(),
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
