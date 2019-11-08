package com.netcracker.controllers;

import com.netcracker.models.User;
import com.netcracker.services.repo.UserRepo;
import com.netcracker.services.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.UUID;

@RequiredArgsConstructor
@SuppressWarnings("unused")
@RestController
@RequestMapping("/user")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @RequestMapping(value = "/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<User> get(@PathVariable("id") String id) {
        LOG.info("get request is received [id={}]", id);

        User res = userService.getUserById(UUID.fromString(id));

        return res == null ? new ResponseEntity<>(NOT_FOUND) : new ResponseEntity<>(res, OK);
    }

    @RequestMapping(value = "/", method = GET)
    @ResponseBody
    public ResponseEntity<Collection<User>> getAll() {
        LOG.info("getAll request is received.");

        return new ResponseEntity<>(userService.getAllUsers(), OK);
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public ResponseEntity<User> create(@RequestBody User user) {
        LOG.info("create request is received [book={}]", user);

        User res = userService.createUser(user);

        return new ResponseEntity<>(res, CREATED);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") String id) {
        LOG.info("delete request is received [id={}]", id);

        userService.deleteUserById(UUID.fromString(id));

        return new ResponseEntity(NO_CONTENT);
    }

    @RequestMapping(method = PUT)
    @ResponseBody
    public ResponseEntity<User> update(@RequestBody User user) {
        LOG.info("update request is received");

        User res = userService.updateUser(user);

        return new ResponseEntity<>(res, OK);
    }

    @PostMapping("/login")
    @ApiOperation(value = "${UserController.signin}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Invalid username/password supplied")})
    public String login(//
                        @ApiParam("Username") @RequestParam String username, //
                        @ApiParam("Password") @RequestParam String password) {
        return userService.signin(username, password);
    }

    @PostMapping("/signup")
    @ApiOperation(value = "${UserController.signup}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 422, message = "Username is already in use"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public String signup(@ApiParam("Signup User") @RequestBody User user) {
        return userService.signup(user);
    }

    @DeleteMapping(value = "/{username}")
    @PreAuthorize("hasRole('administrator')")
    @ApiOperation(value = "${UserController.delete}")
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public String deleteByUsername(@ApiParam("Username") @PathVariable String username) {
        userService.delete(username);
        return username;
    }

    @GetMapping(value = "/{username}")
    @PreAuthorize("hasRole('administrator')")
    @ApiOperation(value = "${UserController.search}", response = User.class)
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 404, message = "The user doesn't exist"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public User search(@ApiParam("Username") @PathVariable String username) {
        return userService.search(username);
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasRole('administrator') or hasRole('moderator') or hasRole('ordinary')")
    @ApiOperation(value = "${UserController.me}", response = User.class)
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public User whoAmI(HttpServletRequest req) {
        return userService.whoAmI(req);
    }

    @GetMapping("/refresh")
    @PreAuthorize("hasRole('administrator') or hasRole('moderator') or hasRole('ordinary')")
    public String refresh(HttpServletRequest req) {
        return userService.refresh(req.getRemoteUser());
    }

}
