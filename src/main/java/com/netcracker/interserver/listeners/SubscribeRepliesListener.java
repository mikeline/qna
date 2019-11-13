package com.netcracker.interserver.listeners;

import com.netcracker.interserver.RabbitConfiguration;
import com.netcracker.interserver.ThisNode;
import com.netcracker.interserver.messages.SubscribeResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Log4j
@RequiredArgsConstructor
@RabbitListener(queues = RabbitConfiguration.QUEUE_SUBSCRIBE_UNSUBSCRIBE_REQUESTS)
public class SubscribeRepliesListener {
    private final ThisNode thisNode;

    @RabbitHandler
    public void subscribeUnsubscribeReplyHandler(@Payload SubscribeResponseMessage payload, @Header("sender") String senderUUID, Message msg) {
        log.info(msg);
        if (payload.isAccepted()) {
            thisNode.addProducer()
        }
    }

    @RabbitHandler(isDefault = true)
    public void defaultHandler(@Payload Object payload, Message msg) {
        log.info("Couldn't listen");
        log.info(payload);
        log.info(msg);
    }
}
