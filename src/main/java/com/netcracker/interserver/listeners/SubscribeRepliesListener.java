package com.netcracker.interserver.listeners;

import com.netcracker.interserver.RabbitConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Log4j
@RequiredArgsConstructor
@RabbitListener(queues = RabbitConfiguration.QUEUE_SUBSCRIBE_UNSUBSCRIBE_REQUESTS)
public class SubscribeRepliesListener {
}
