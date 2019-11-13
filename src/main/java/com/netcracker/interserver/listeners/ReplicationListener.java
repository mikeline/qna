package com.netcracker.interserver.listeners;

import com.netcracker.models.*;
import com.netcracker.services.repo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Log4j
@RequiredArgsConstructor
@RabbitListener(id = ReplicationListener.ID)
public class ReplicationListener {
    public static final String ID = "ReplicationListener";

    private static AnswerRepo answerRepo;
    private static CommentRepo commentRepo;
    private static PostRepo postRepo;
    private static TopicRepo topicRepo;
    private static UserRepo userRepo;

    @RabbitHandler
    public void handleAnswers(@Payload Answer answer) {
        answerRepo.save(answer);
    }

    @RabbitHandler
    public void handleComment(@Payload Comment comment) {
        commentRepo.save(comment);
    }

    @RabbitHandler
    public void handlePost(@Payload Post post) {
        postRepo.save(post);
    }

    @RabbitHandler
    public void handleTopic(@Payload Topic topic) {
        topicRepo.save(topic);
    }

    @RabbitHandler
    public void handleUser(@Payload User user) {
        userRepo.save(user);
    }

}
