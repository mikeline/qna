package com.netcracker.interserver.listeners;

import com.netcracker.interserver.ThisNode;
import com.netcracker.interserver.messages.SubscribeRequest;
import com.netcracker.interserver.messages.SubscribeReply;
import com.netcracker.interserver.messages.Summary;
import com.netcracker.interserver.messages.SummaryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@Log4j
@RequiredArgsConstructor
@RabbitListener(id = NodeRoleListener.ID)
public class NodeRoleListener {
    public static final String ID = "NodeRoleListener";

    private final ThisNode thisNode;
    private final RabbitTemplate template;


    @RabbitHandler
    public void handleSummaryRequest(@Payload SummaryRequest request, @Header("sender") String senderUUID, Message msg) {
        log.debug("got a summary request");
        log.debug(msg);
        if (senderUUID.equals(String.valueOf(thisNode.getNode().getNodeId()))) {
            log.debug("got my own message");
            return;
//            return null; // returning null means no message will be sent
        }

        if (thisNode.getSubscribers().size() >= 3) {
//            return null;
            return;
        }

        sendReply(new Summary(thisNode.getNode()), msg);
//        template.convertAndSend("",  msg.getHeaders().get("reply_to").toString(), new Summary(thisNode.getNode()));
//        return new Summary(thisNode.getNode());
    }

    @RabbitHandler
    public void handleSummaryReply(@Payload Summary summary, Message msg) {
        log.debug(msg);

        // todo: better logic
        if (thisNode.getPublishers().size() >= 3) {
            return;
//            return null;
        }

        sendReply(new SubscribeRequest(thisNode.getSelf()), msg);
//        return new SubscribeRequest(thisNode.getSelf());
    }

    private void sendReply(Object o, Message msg) {
        log.debug(msg.getHeaders());
        template.convertAndSend("", msg.getHeaders().get("amqp_replyTo").toString(), o); //fixme: ААА коcтыль!!!! АААА
    }

    @RabbitHandler
    public void handleSubscribe(@Payload SubscribeRequest subscribe, Message msg) {
        log.debug(msg);
        if (thisNode.getSubscribers().size() >= 3) {
            return;
//            return new SubscribeReply(false, null);
        }

        boolean subscribed = thisNode.addSubscriber(subscribe.getNode());
        if (subscribed) {
            sendReply(new SubscribeReply(true, thisNode.getNode()), msg);
//            return new SubscribeReply(true, thisNode.getNode());
        } else {
            sendReply(new SubscribeReply(false, null), msg);
//            return new SubscribeReply(false, null);
        }
    }

    @RabbitHandler
    public void handleSubscribeReply(@Payload SubscribeReply reply, Message msg) {
        log.debug(msg);
        if (reply.isSubscribed()) {
            thisNode.addPublisher(reply.getNode());
        }
    }

    @RabbitHandler(isDefault = true)
    public void defaultHandler(@Payload Object obj, Message msg) {
        log.info(msg);
    }
}
