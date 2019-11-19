package com.netcracker.interserver.listeners;

import com.netcracker.interserver.RabbitConfiguration;
import com.netcracker.interserver.ThisNode;
import com.netcracker.interserver.messages.Summary;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Log4j
@RequiredArgsConstructor
@RabbitListener(id = SummaryReplyListener.ID)
public class SummaryReplyListener {
    public static final String ID = "SummaryReplyListener";

    private final ThisNode thisNode;

    @RabbitHandler
    public void summaryReplyHandler(@Payload Summary summary, Message msg) {
        log.info("got a reply!");
        log.info(msg);
        log.info(summary);

//        // todo: better logic
//        if (thisNode.getPublishers().size() >= 3) {
//            return;
//        }
//
//        thisNode.addPublisher(summary.getNode());
    }

    @RabbitHandler(isDefault = true)
    public void defaultHandler(@Payload Object obj, Message msg) {
        log.info(msg);
    }
}
