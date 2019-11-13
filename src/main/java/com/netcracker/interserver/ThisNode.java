package com.netcracker.interserver;

import com.netcracker.interserver.messages.CheckStatusMessage;
import com.netcracker.interserver.messages.SubscribeRequestMessage;
import com.netcracker.models.Node;
import com.netcracker.services.repo.NodeRepo;
import com.netcracker.utils.NodeRole;
import exception.MultipleSelfNodesQnAException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.parameters.P;
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
        configureRabbit();

        //todo: delete nodes if they are too old/ take too long to answer

        List<Node> producers = nodeRepo.findByNodeRoleProducer();
        updateNodeRole(producers);
        if (producers.size() < 3) { // todo: wait for updateProducers to reply
            findNewProducers();
        }

        List<Node> subscribers = nodeRepo.findByNodeRoleSubscriber();
        updateNodeRole(subscribers);
    }

    private void configureRabbit() {
        template.setBeforePublishPostProcessors(
                message ->
                        MessageBuilder
                                .fromMessage(message)
                                .setHeader("sender", getUUID())
                                .build()
        );
        admin.declareBinding(
                BindingBuilder
                        .bind(queueSubscribeUnsubscribeReplies)
                        .to(exchangeDirectSubscribeUnsubscribeReplies)
                        .with(getUUID().toString())
        );
    }

    private void loadSelf() {
        List<Node> selfNodes = nodeRepo.findByNodeRoleSelf();
        if (selfNodes.size() > 1){
            throw new MultipleSelfNodesQnAException();
        }

        if (selfNodes.size() == 0) {
            log.info("Generating new self node");
            self = new Node();
            self.setName("my name"); // fixme
            self.setAuthorityToken("my token");
            self.setNodeRole(NodeRole.SELF);

            self = nodeRepo.save(self);
        } else {
            self = selfNodes.get(0);
        }
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


    // sets a node as a subscriber
    public void addSubscriber(Node subscriberNode) {
        subscriberNode.setNodeRole(NodeRole.PRODUCER);
        nodeRepo.save(subscriberNode);
    }

    public void removeSubscriber(String senderID) {
        UUID uuid;
        try {
            uuid = UUID.fromString(senderID);
        } catch (IllegalArgumentException e) {
            log.info(e);
            return;
        }

        Optional<Node> subscriberNode = nodeRepo.findById(uuid);
        subscriberNode.ifPresent(node -> {
            node.setNodeRole(NodeRole.NONE);
            nodeRepo.save(node);
        });
    }

    public List<Node> getProducers() {
        return nodeRepo.findByNodeRoleProducer();
    }

    public void addProducer(Node producerNode) {
        producerNode.setNodeRole(NodeRole.PRODUCER);
        nodeRepo.save(producerNode);
    }
}
