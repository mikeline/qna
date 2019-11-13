package com.netcracker.interserver.listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(id = SearchListener.ID)
public class SearchListener {
    public static final String ID = "SearchListener";
}
