package com.netcracker.interserver.listeners;

import com.netcracker.interserver.messages.Replicable;
import com.netcracker.interserver.messages.Replicate;
import com.netcracker.models.*;
import com.netcracker.services.repo.*;
import com.netcracker.services.service.NodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j
@RequiredArgsConstructor
@RabbitListener(id = ReplicationListener.ID)
public class ReplicationListener {
    public static final String ID = "ReplicationListener";

    private final AnswerRepo answerRepo;
    private final CommentRepo commentRepo;
    private final NodeService nodeService;
    private final PostRepo postRepo;
    private final TopicRepo topicRepo;
    private final UserRepo userRepo;

    @RabbitHandler
    public void handleReplication(@Payload Replicate replicate, Message msg) {
        log.debug(msg);
        log.debug(replicate);

        ArrayList<Answer> answers = new ArrayList<>();
        ArrayList<Comment> comments = new ArrayList<>();
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<Post> posts = new  ArrayList<>();
        ArrayList<Topic> topics = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();

        for(Replicable r :replicate.getData()) {
            if (r instanceof Answer) {
                answers.add((Answer) r);
            }
            if (r instanceof Comment) {
                comments.add((Comment) r);
            }
            if (r instanceof Node) {
                nodes.add((Node) r); //fixme
            }
            if (r instanceof Post) {
                posts.add((Post) r);
            }
            if (r instanceof Topic) {
                topics.add((Topic) r);
            }
            if (r instanceof User) {
                users.add((User) r);
            }
        }

        answerRepo.saveAll(answers);
        commentRepo.saveAll(comments);
        nodeService.saveAllReplication(nodes);
        postRepo.saveAll(posts);
        topicRepo.saveAll(topics);
        userRepo.saveAll(users);

        log.debug(posts);
    }

//    @RabbitHandler
//    public void handleAnswers(@Payload Answer answer) {
//        answerRepo.save(answer);
//    }
//
//    @RabbitHandler
//    public void handleComment(@Payload Comment comment) {
//        commentRepo.save(comment);
//    }
//
//    @RabbitHandler
//    public void handlePost(@Payload Post post) {
//        postRepo.save(post);
//    }
//
//    @RabbitHandler
//    public void handleTopic(@Payload Topic topic) {
//        topicRepo.save(topic);
//    }
//
//    @RabbitHandler
//    public void handleUser(@Payload User user) {
//        userRepo.save(user);
//    }

}
