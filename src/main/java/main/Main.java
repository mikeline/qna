package main;

import models.Post;
import models.Topic;
import models.User;
import services.implementations.PostService;
import services.implementations.TopicService;
import services.implementations.UserService;
import utils.PostType;
import utils.QnaRole;
import utils.SecurityUtil;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws SQLException {
        UserService userService = new UserService();
        PostService postService = new PostService();
        TopicService topicService = new TopicService();
        User user = new User("Misha Linev", "misha0206",
                SecurityUtil.encryptPassword("misha0206"), "raraavis027@gmail.com", QnaRole.administrator);
        userService.save(user);
        Post post = new Post("Want to learn Hibernate", PostType.TOPIC, user);
        postService.save(post);
        Topic topic = new Topic();
        topic.setTitle("Learning Hibernate");
        topic.setTopicPost(post);
        topicService.save(topic);

    }


}
