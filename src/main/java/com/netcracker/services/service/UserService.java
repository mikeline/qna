package com.netcracker.services.service;

import com.netcracker.exception.CustomHttpException;
import com.netcracker.interserver.InterserverCommunication;
import com.netcracker.interserver.messages.UserAuthenticationReply;
import com.netcracker.interserver.messages.UserAuthenticationRequest;
import com.netcracker.models.User;
import com.netcracker.security.JwtTokenProvider;
import com.netcracker.services.repo.UserRepo;
import com.netcracker.utils.QnaRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
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
import org.springframework.util.concurrent.ListenableFuture;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;


@RequiredArgsConstructor
@Service
@Log4j
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

    public boolean doAuthAndGetRoles(String username, String password, List<QnaRole> roles) throws AuthenticationException {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            return false;
        }

        roles.add(user.getRole());
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password, roles));

        return auth.isAuthenticated();
    }

    public UserAuthenticationReply localAuth(UserAuthenticationRequest req) {
        List<QnaRole> roles = new ArrayList<>();
        if(doAuthAndGetRoles(req.getUsername(), req.getPassword(), roles))
        {
            return new UserAuthenticationReply(jwtTokenProvider.createToken(req.getUsername(), roles));
        }
        else
        {
            return null;
        }
    }

    @Async
    public Future<UserAuthenticationReply> signin(String username, String password) throws InterruptedException {
        log.debug("kjasdfkjl;dafs");
        List<QnaRole> roles = new ArrayList<>();
        if(doAuthAndGetRoles(username, password, roles))
        {
            log.debug("true");
            return new AsyncResult<>(new UserAuthenticationReply(jwtTokenProvider.createToken(username, roles)));
        }
        else
        {
            log.debug("false");
            ListenableFuture<UserAuthenticationReply> reply = interserverCommunication.sendUserAuthenticationRequest(new UserAuthenticationRequest(username, password));
            return reply;
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

}