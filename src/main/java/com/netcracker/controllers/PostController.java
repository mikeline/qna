package com.netcracker.controllers;


import com.netcracker.models.Post;
import com.netcracker.services.repo.PostRepo;
import com.netcracker.services.service.PostService;
import com.netcracker.services.service.UserService;
import com.netcracker.utils.QnaRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    private final PostRepo postRepo;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODER') or hasRole('ROLE_CLIENT')")
    @RequestMapping(value = "/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<Post> get(@PathVariable("id") String id) {

        Post res = postService.getPostById(UUID.fromString(id));

        return res == null ? new ResponseEntity<>(NOT_FOUND) : new ResponseEntity<>(res, OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODER') or hasRole('ROLE_CLIENT')")
    @RequestMapping(value = "/", method = GET)
    @ResponseBody
    public ResponseEntity<Collection<Post>> getAll() {

        return new ResponseEntity<>(postService.getAllPosts(), OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODER') or hasRole('ROLE_CLIENT')")
    @RequestMapping(method = POST)
    @ResponseBody
    public ResponseEntity<Post> create(@RequestBody Post post, HttpServletRequest req) {

        Post res = postService.createPost(post, req);

        return new ResponseEntity<>(res, CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODER') or hasRole('ROLE_CLIENT')")
    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") String id, HttpServletRequest req) {

        HttpStatus status = postService.deletePostById(UUID.fromString(id), req);

        return new ResponseEntity<>(status);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODER') or hasRole('ROLE_CLIENT')")
    @RequestMapping(method = PUT)
    @ResponseBody
    public ResponseEntity<Post> update(@RequestBody Post post, HttpServletRequest req) {

        Post res = postService.updatePost(post, req);

        return res == null ? new ResponseEntity<>(FORBIDDEN) : new ResponseEntity<>(res, CREATED);
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODER') or hasRole('ROLE_CLIENT')")
//    @RequestMapping(value = "/{id}/upvote", method = PATCH)
//    @ResponseBody
//    public ResponseEntity upvote() {
//        return vote(1);
//    }
//
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODER') or hasRole('ROLE_CLIENT')")
//    @RequestMapping(value = "/{id}/downvote", method = PATCH)
//    @ResponseBody
//    public ResponseEntity downvote(Authentication auth) {
//        auth.getName();
//         new ResponseEntity<>(OK);
//        return vote(-1);
//
//    }



}
