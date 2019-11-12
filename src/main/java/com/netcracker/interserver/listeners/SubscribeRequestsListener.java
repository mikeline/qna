package com.netcracker.interserver.listeners;

import com.netcracker.interserver.RabbitConfiguration;
import com.netcracker.interserver.ThisNode;
import com.netcracker.interserver.messages.SubscribeRequestMessage;
import com.netcracker.interserver.messages.SubscribeResponseMessage;
import com.netcracker.interserver.messages.UnsubscribeRequestMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Log4j
@RequiredArgsConstructor
@RabbitListener(queues = RabbitConfiguration.QUEUE_SUBSCRIBE_UNSUBSCRIBE_REQUESTS)
public class SubscribeRequestsListener {

    private final ThisNode thisNode;
    private final RabbitTemplate rabbitTemplate;

    @RabbitHandler
    public void subscriberRequestHandler(@Payload SubscribeRequestMessage message, @Header("sender") String senderUUID, Message<?> msg) {
        log.info(msg);
        if (thisNode.getUUID().toString().equals(senderUUID) || thisNode.getSubscribers().size() >= 3) {
            return;
        }

        UUID senderID;
        try{
            senderID = UUID.fromString(senderUUID);
        } catch (IllegalArgumentException e) {
            log.info(e);
            return;
        }

        thisNode.addSubscriber(senderID);

        // send directly to subscriber
        rabbitTemplate.convertAndSend(
                RabbitConfiguration.EXCHANGE_DIRECT_SUBSCRIBE_UNSUBSCRIBE_REPLIES,
                senderUUID,
                new SubscribeResponseMessage(true)
        );
    }

    @RabbitHandler
    public void unsubscribeRequestHandler(@Payload UnsubscribeRequestMessage message, @Header("sender") String senderUUID, Message<?> msg) {
        log.info(msg);
        UUID senderID;
        try{
            senderID = UUID.fromString(senderUUID);
        } catch (IllegalArgumentException e) {
            log.info(e);
            return;
        }

        thisNode.removeSubscriber(senderID);
    }


    @RabbitHandler(isDefault = true)
    public void defaultHandler(Message<?> message) {
        log.info("Wrong message");
        log.info(message);
    }
}
