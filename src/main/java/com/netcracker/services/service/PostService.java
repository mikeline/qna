package com.netcracker.services.service;

import com.netcracker.models.Post;
import com.netcracker.models.Topic;
import com.netcracker.models.User;
import com.netcracker.search.TopicSearch;
import com.netcracker.services.repo.PostRepo;
import com.netcracker.utils.QnaRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequiredArgsConstructor
@Service
public class PostService {

    private final UserService userService;

    private final PostRepo postRepo;

    private final TopicSearch topicSearch;

    public List<Post> getAllPosts() {
        return postRepo.findAll();
    }

    public Post getPostById(UUID id) {
        return postRepo.getOne(id);
    }

    public Post createPost(Post post, HttpServletRequest req) {

        String usernameFromToken = userService.getUsernameFromToken(req);

        User creator = userService.findUserByUsername(usernameFromToken);

        post.setUser(creator);

        return postRepo.save(post);
    }


    public Post updatePost(Post post, HttpServletRequest req) {

        Collection<? extends GrantedAuthority> userRoles = userService.getAuthoritiesFromToken(req);

        if(userRoles.contains(QnaRole.ROLE_MODER) || userRoles.contains(QnaRole.ROLE_ADMIN)) {
            Post res = postRepo.save(post);
            return res;
        }

        String usernameFromToken = userService.getUsernameFromToken(req);
        String authorUsername = post.getUser().getUsername();

        if(usernameFromToken.equals(authorUsername)) {
            Post res = postRepo.save(post);
            return res;
        }

        return null;
    }

    public HttpStatus deletePostById(UUID id, HttpServletRequest req) {

        Collection<? extends GrantedAuthority> userRoles = userService.getAuthoritiesFromToken(req);

        if(userRoles.contains(QnaRole.ROLE_MODER) || userRoles.contains(QnaRole.ROLE_ADMIN)) {
            postRepo.deleteById(id);
            return NO_CONTENT;
        }

        String usernameFromToken = userService.getUsernameFromToken(req);
        String authorUsername = postRepo.getOne(id).getUser().getUsername();

        if(usernameFromToken.equals(authorUsername)) {
            postRepo.deleteById(id);
            return NO_CONTENT;
        }

        return FORBIDDEN;
    }

}
