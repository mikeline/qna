package com.netcracker.interserver;

import com.netcracker.interserver.messages.CheckStatusMessage;
import com.netcracker.interserver.messages.SubscribeRequestMessage;
import com.netcracker.models.Node;
import com.netcracker.services.repo.NodeRepo;
import exception.MultipleSelfNodesQnAException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j
public class ThisNode {
    private final NodeRepo nodeRepo;
    private final RabbitTemplate template;
    private final RabbitAdmin admin;
    private final ApplicationContext context;

    @Qualifier(RabbitConfiguration.QUEUE_SUBSCRIBE_UNSUBSCRIBE_REPLIES)
    private final Queue queueSubscribeUnsubscribeReplies;

    @Qualifier(RabbitConfiguration.EXCHANGE_DIRECT_SUBSCRIBE_UNSUBSCRIBE_REPLIES)
    private final DirectExchange exchangeDirectSubscribeUnsubscribeReplies;

    private Node self;


    @PostConstruct
    public void init() {
        loadSelf();
        makeBindings();

        //todo: delete nodes if they are too old/ take too long to answer

        List<Node> producers = nodeRepo.findByNodeRoleProducer();
        updateNodeRole(producers);
        if (producers.size() < 3) { // todo: wait for updateProducers to reply
            findNewProducers();
        }

        List<Node> subscribers = nodeRepo.findByNodeRoleSubscriber();
        updateNodeRole(subscribers);
    }

    private void loadSelf() {
        List<Node> selfNodes = nodeRepo.findByNodeRoleSelf();
        if (selfNodes.size() > 1){
            throw new MultipleSelfNodesQnAException();
        }

        if (selfNodes.size() == 0) {
            log.info("Generating new self node");

        }

        self = selfNodes.get(0);
    }

    private void makeBindings() {
        admin.declareBinding(
                BindingBuilder
                        .bind(queueSubscribeUnsubscribeReplies)
                        .to(exchangeDirectSubscribeUnsubscribeReplies)
                        .with(getUUID().toString())
        );
    }

    private void updateNodeRole(List<Node> producers) {
        for(Node producer : producers) {
            template.convertAndSend(
                    RabbitConfiguration.EXCHANGE_DIRECT_SUBSCRIBE_UNSUBSCRIBE_REQUESTS,
                    producer.getNodeId().toString(),
                    new CheckStatusMessage()
            );
        }
    }

    public void findNewProducers() {
        template.convertAndSend(
                RabbitConfiguration.EXCHANGE_FANOUT_SUBSCRIBE_UNSUBSCRIBE_REQUESTS,
                "",
                new SubscribeRequestMessage(),
                message ->
                    MessageBuilder
                            .fromMessage(message)
                            .setHeader("sender", getUUID())
                            .build()

        );
    }

    public UUID getUUID() {
        return self.getNodeId();
    }

    public Set<String> getSubscribers() {
        return nodeRepo.findByNodeRoleSubscriber()
                .stream()
                .map(Node::getNodeId).map(UUID::toString)
                .collect(Collectors.toSet());
    }

    public void addSubscriber(UUID subscriberUUID) {
        
    }

    public void removeSubscriber(UUID senderID) {
    }
}
