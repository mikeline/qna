package com.netcracker.services.service;

import com.netcracker.models.*;
import com.netcracker.models.Post;
import com.netcracker.models.User;
import com.netcracker.search.GeneralSearch;
import com.netcracker.services.repo.PostRepo;
import com.netcracker.services.repo.UserPostVoteRepo;
import com.netcracker.services.repo.UserRepo;
import com.netcracker.utils.QnaRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RequiredArgsConstructor
@Service
@Log4j
public class PostService {

    private final UserService userService;
    private final NodeService nodeService;

    private final PostRepo postRepo;
    private final UserPostVoteRepo userPostVoteRepo;
    private final UserRepo userRepo;

    private final GeneralSearch generalSearch;

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

        if (post.getOwnerId() == null) {
            post.setOwnerId(nodeService.getSelfUUID());
        }

        return postRepo.save(post);
    }


    public Post updatePost(Post post, HttpServletRequest req) {

        Collection<? extends GrantedAuthority> userRoles = userService.getAuthoritiesFromToken(req);

        if (post.getOwnerId() == null) {
            post.setOwnerId(nodeService.getSelfUUID());
        }

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

    @Transactional
    public void updateRating(UUID userId, UUID postId, int vote) {
        Optional<Post> opPost = postRepo.findById(postId);
        Optional<User> opUser = userRepo.findById(userId);

        if (opPost.isEmpty() || opUser.isEmpty()) {
            return;
        }

        Post post = opPost.get();
        User user = opUser.get();
//        log.info(post);
//        log.info(user);

        UserPostVoteId ratingId = new UserPostVoteId(userId, postId);
        UserPostVote rating = userPostVoteRepo.findById(ratingId).orElse(new UserPostVote(user, post, 0));
        post.setRating(post.getRating() - rating.getVote() + vote);

        if (vote == 0) {
            userPostVoteRepo.deleteById(ratingId); // apparently this throws an exception if given id does not exist in the DB
        } else {
            rating.setVote(vote);
            userPostVoteRepo.save(rating);
        }

        postRepo.save(post);
    }
}
