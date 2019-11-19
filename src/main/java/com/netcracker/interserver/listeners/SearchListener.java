package com.netcracker.interserver.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Log4j
@RequiredArgsConstructor
@RabbitListener(id = SearchListener.ID)
public class SearchListener {
    public static final String ID = "SearchListener";

    @RabbitHandler(isDefault = true)
    public void handleSearch(@Payload Object payload, Message msg) {
        log.info(msg);
    }
}
