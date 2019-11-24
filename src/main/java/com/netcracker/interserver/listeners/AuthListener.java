package com.netcracker.interserver.listeners;

import com.netcracker.interserver.messages.UserAuthenticationReply;
import com.netcracker.interserver.messages.UserAuthenticationRequest;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(id = AuthListener.ID)
@Log4j
public class AuthListener {
    public static final String ID = "Auth Listener";

    @RabbitHandler
    UserAuthenticationReply handleUserAuthRequest(@Payload UserAuthenticationRequest request, Message msg) {
        log.debug(msg);

        return new UserAuthenticationReply();
    }
}

