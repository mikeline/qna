package com.netcracker.services.service;

import javax.servlet.http.HttpServletRequest;

import com.netcracker.models.User;
import com.netcracker.services.repo.UserRepo;
import com.netcracker.utils.QnaRole;
import com.netcracker.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.netcracker.security.JwtTokenProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    public User getUserById(UUID id) {
        return userRepo.getOne(id);
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

    public String signin(String username, String password) {
        try {
            List<QnaRole> roles = new ArrayList<>();
            User user = userRepo.findByUsername(username);
            roles.add(user.getRole());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            return jwtTokenProvider.createToken(username, roles);
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public String signup(User user) {
        if (!userRepo.existsByUsername(user.getUsername())) {
            user.setPasswordEncrypted(passwordEncoder.encode(user.getPasswordEncrypted()));
            userRepo.save(user);
            List<QnaRole> roles = new ArrayList<QnaRole>();
            roles.add(user.getRole());
            return jwtTokenProvider.createToken(user.getUsername(), roles);
        } else {
            throw new CustomException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public void delete(String username) {
        userRepo.deleteByUsername(username);
    }

    public User search(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new CustomException("The user doesn't exist", HttpStatus.NOT_FOUND);
        }
        return user;
    }

    public User whoAmI(HttpServletRequest req) {
        return userRepo.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
    }

    public String refresh(String username) {
        List<QnaRole> roles = new ArrayList<QnaRole>();
        roles.add(userRepo.findByUsername(username).getRole());
        return jwtTokenProvider.createToken(username, roles);
    }

}