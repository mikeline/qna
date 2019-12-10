package com.netcracker.services.service;

import javax.servlet.http.HttpServletRequest;

import com.netcracker.interserver.InterserverCommunication;
import com.netcracker.interserver.messages.UserAuthenticationReply;
import com.netcracker.interserver.messages.UserAuthenticationRequest;
import com.netcracker.models.User;
import com.netcracker.services.repo.UserRepo;
import com.netcracker.utils.QnaRole;
import com.netcracker.exception.CustomHttpException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.netcracker.security.JwtTokenProvider;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Future;

import static org.springframework.http.HttpStatus.*;


@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    private final InterserverCommunication interserverCommunication;

    public User getUserById(UUID id) {
        return userRepo.getOne(id);
    }

    public User findUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User createUser(User user) {
        return userRepo.save(user);
    }

    public User updateUser(User user) {
        return userRepo.save(user);
    }

    public void deleteUserById(UUID id) {
        userRepo.deleteById(id);
    }


    public String signin(String username, String password) throws InterruptedException {
        List<QnaRole> roles = new ArrayList<>();
        User user = userRepo.findByUsername(username);
        roles.add(user.getRole());
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password, roles));

        if(auth.isAuthenticated())
        {
            return jwtTokenProvider.createToken(user.getUsername(), roles);
        }
        else
        {
            throw new CustomHttpException("Invalid username/password", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String signup(User user) {
        if (!userRepo.existsByUsername(user.getUsername())) {
            user.setPasswordEncrypted(passwordEncoder.encode(user.getPasswordEncrypted()));
            userRepo.save(user);
            List<QnaRole> roles = new ArrayList<>();
            roles.add(user.getRole());
            return jwtTokenProvider.createToken(user.getUsername(), roles);
        } else {
            throw new CustomHttpException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public void delete(String username) {
        userRepo.deleteByUsername(username);
    }

    public User search(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new CustomHttpException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return user;
    }

    public String getUsernameFromToken(HttpServletRequest req) {
        return jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req));
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(HttpServletRequest req) {
        return jwtTokenProvider
                .getAuthentication(jwtTokenProvider.resolveToken(req))
                .getAuthorities();
    }

    public User whoAmI(HttpServletRequest req) {
        String username = jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req));
        return userRepo.findByUsername(username);
    }

    public String refresh(String username) {
        List<QnaRole> roles = new ArrayList<>();
        roles.add(userRepo.findByUsername(username).getRole());
        return jwtTokenProvider.createToken(username, roles);
    }

    public HttpStatus banUserById(UUID id, LocalDateTime unblockTime) {

        Optional<User> optionalUser = userRepo.findById(id);
        User user = optionalUser.isPresent() ? optionalUser.get() : new User();

        user.setUnblockTime(unblockTime);

        userRepo.save(user);

        return NO_CONTENT;
    }

}