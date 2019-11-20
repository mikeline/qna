package com.netcracker.interserver.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(id = AuthListener.ID)
@Slf4j
public class AuthListener {
    public static final String ID = "Auth Listener";
}
