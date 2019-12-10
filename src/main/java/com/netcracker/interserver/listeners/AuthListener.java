package com.netcracker.interserver.listeners;

import com.netcracker.interserver.messages.UserAuthenticationReply;
import com.netcracker.interserver.messages.UserAuthenticationRequest;
import com.netcracker.services.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(id = AuthListener.ID)
@RequiredArgsConstructor
@Log4j
public class AuthListener {
    public static final String ID = "Auth Listener";

    private final UserService userService;

//    @RabbitHandler
//    UserAuthenticationReply handleUserAuthRequest(@Payload UserAuthenticationRequest request, Message msg) {
//        log.debug(msg);
//
//        return userService.localAuth(request);
//    }
}

