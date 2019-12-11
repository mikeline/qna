package com.netcracker.interserver.listeners;

import com.netcracker.interserver.messages.UserAuthenticationReply;
import com.netcracker.interserver.messages.UserAuthenticationRequest;
import com.netcracker.services.service.NodeService;
import com.netcracker.services.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

//@Component
//@RabbitListener(id = AuthListener.ID)
//@RequiredArgsConstructor
//@Log4j
public class AuthListener {
//    public static final String ID = "Auth Listener";
//
//    private final NodeService nodeService;
//    private final UserService userService;
//
//    @RabbitHandler
//    UserAuthenticationReply handleUserAuthRequest(@Payload UserAuthenticationRequest request, @Header("sender") String sender, Message msg) {
//        log.debug(msg);
//        if (sender.equals(nodeService.getSelfUUID().toString())) {
//            log.debug("got my own message");
//            return null;
//        }
//        return userService.localAuth(request);
//    }
}

