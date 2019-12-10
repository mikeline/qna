package com.netcracker.security;

import com.netcracker.models.User;
import com.netcracker.services.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class MyUserDetails implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User '" + username + "' not found");
        }

        return org.springframework.security.core.userdetails.User//
                .withUsername(username)//
                .password(user.getPasswordEncrypted())//
                .authorities(user.getRole().toString())//
                .accountExpired(false)//
                .accountLocked(banned(user))//
                .credentialsExpired(false)//
                .disabled(false)//
                .build();
    }

    public boolean banned(User user) {
        if(user.getUnblockTime() == null) {
            return false;
        }
        if(user.getUnblockTime().isAfter(LocalDateTime.now())) {
            return true;
        }
        return false;
    }

}