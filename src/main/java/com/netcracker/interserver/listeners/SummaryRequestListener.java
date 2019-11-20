package com.netcracker.interserver.listeners;

import com.netcracker.interserver.RabbitConfiguration;
import com.netcracker.interserver.ThisNode;
import com.netcracker.interserver.messages.Summary;
import com.netcracker.interserver.messages.SummaryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@RabbitListener(queues = RabbitConfiguration.QUEUE_REQUEST_SUMMARY)
public class SummaryRequestListener {

    private final RabbitTemplate template;
    private final ThisNode thisNode;

//    @RabbitHandler
//    public void summaryRequestHandler(@Payload SummaryRequest request, Message msg) {
//        log.info(msg);
//        template.convertAndSend(RabbitConfiguration.EXCHANGE_REPLY_SUMMARY, "", new Summary(thisNode.getNode()));
//    }

    @RabbitHandler(isDefault = true)
    public void defaultHandler(@Payload Object payload, Message msg) {
        log.info("", msg);
    }
}
