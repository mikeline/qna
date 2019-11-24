package com.netcracker.interserver.listeners;

import com.netcracker.interserver.InterserverCommunication;
import com.netcracker.interserver.messages.SubscribeReply;
import com.netcracker.interserver.messages.SubscribeRequest;
import com.netcracker.interserver.messages.SubscribeRequest.SubStatus;
import com.netcracker.interserver.messages.Summary;
import com.netcracker.interserver.messages.SummaryRequest;
import com.netcracker.services.service.NodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Log4j
@RequiredArgsConstructor
@RabbitListener(id = NodeRoleListener.ID)
public class NodeRoleListener {
    public static final String ID = "NodeRoleListener";

    private final NodeService nodeService;
    private final InterserverCommunication interserverCommunication;
    private final RabbitTemplate template;


    @RabbitHandler
    public void handleSummaryRequest(@Payload SummaryRequest request, @Header("sender") String senderUUID, Message msg) {
        log.debug("got a summary request");
        log.debug(msg);
        if (senderUUID.equals(String.valueOf(nodeService.getSelfUUID()))) {
            log.debug("got my own message");
            return;
        }

        if (nodeService.getSubscribers().size() >= 3) {
            return;
        }

        sendReply(new Summary(nodeService.getSelf()), msg);
    }

    @RabbitHandler
    public void handleSummaryReply(@Payload Summary summary, Message msg) {
        log.debug(msg);

        if (nodeService.getPublishers().size() >= 3) {
            return;
        }

        sendReply(new SubscribeRequest(nodeService.getSelf(), SubStatus.SUBSCRIBE), msg);
    }

    @RabbitHandler
    public void handleSubscribe(@Payload SubscribeRequest subscribe, Message msg) {
        log.debug(msg);
        if (subscribe.getStatus() == SubStatus.UNSUBSCRIBE) {
            nodeService.deleteSubscriber(subscribe.getNode().getNodeId());
        }
        if (nodeService.getSubscribers().size() >= 3) {
            return;
        }

        boolean subscribed = nodeService.saveSubscriber(subscribe.getNode());
        sendReply(new SubscribeReply (subscribed, subscribed ? nodeService.getSelf() : null), msg);
    }

    @RabbitHandler
    public void handleSubscribeReply(@Payload SubscribeReply reply, Message msg) {
        log.debug(msg);
        if (reply.isSubscribed()) {
            nodeService.savePublisher(reply.getNode());
        }
    }

    @RabbitHandler(isDefault = true)
    public void defaultHandler(@Payload Object obj, Message msg) {
        log.info(msg);
    }


    private void sendReply(Object o, Message msg) {
        log.debug(msg.getHeaders());
        template.convertAndSend("", msg.getHeaders().get("amqp_replyTo").toString(), o); //fixme
    }
}
