package com.netcracker.controllers;

import com.netcracker.interserver.messages.UserAuthenticationReply;
import com.netcracker.models.User;
import com.netcracker.services.repo.UserRepo;
import com.netcracker.services.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.Future;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @RequestMapping(method = PUT)
    @ResponseBody
    public ResponseEntity<User> update(@RequestBody User user) {

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
                                                 @ApiParam("Password") @RequestParam String password) throws InterruptedException {
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    @ApiOperation(value = "${UserController.me}", response = User.class)
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public User whoAmI(HttpServletRequest req) {
        return userService.whoAmI(req);
    }

    @GetMapping("/refresh")
    public String refresh(HttpServletRequest req) {
        return userService.refresh(req.getRemoteUser());
    }

    @RequestMapping(value = "/ban", method = PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MODER')")
    public ResponseEntity ban(@RequestParam String id, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime unblockTime) {
        HttpStatus status = userService.banUserById(UUID.fromString(id), unblockTime);
        return new ResponseEntity(status);
    }

}
