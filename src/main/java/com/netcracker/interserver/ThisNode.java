package com.netcracker.interserver;

import com.netcracker.interserver.listeners.ReplicationListener;
import com.netcracker.interserver.listeners.SearchListener;
import com.netcracker.interserver.listeners.SummaryReplyListener;
import com.netcracker.interserver.listeners.SummaryRequestListener;
import com.netcracker.interserver.messages.SummaryRequest;
import com.netcracker.models.Node;
import com.netcracker.services.repo.NodeRepo;
import com.netcracker.utils.NodeRole;
import exception.MultipleSelfNodesQnAException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j
public class ThisNode {
    private final NodeRepo nodeRepo;
    private final RabbitTemplate template;
    private final RabbitAdmin admin;
    private final RabbitListenerEndpointRegistry registry;

    private final TopicExchange replicationExchange;
    private final DirectExchange searchExchange;

    private Node self;
    private final Queue replicationQueue;
    private final Queue searchQueue;
    private Map<UUID, PublisherBindings> bindingsMap = new HashMap<>(); // maps PublisherUUID to bindings

    private final Queue summaryRequestQueue; // очередь в которую попадают просьбы выслать summary
    private final Queue summaryReplyQueue; // очередь в которую попадают summaries

    @Qualifier(RabbitConfiguration.EXCHANGE_REQUEST_SUMMARY)
    private final FanoutExchange summaryRequestExchange;
//    @Qualifier(RabbitConfiguration.EXCHANGE_REPLY_SUMMARY)
//    private final FanoutExchange summaryReplyExchange;

    private class PublisherBindings {
        Binding replicateBinding;
        Binding searchBinding;
    }

    @PostConstruct
    public void init() {
        log.info("post construct");
        loadSelf();
        bindRabbitQueues();
        loadPublishers();
    }

    @EventListener
    public void handleContextRefreshedEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("context refreshed");
        log.info(contextRefreshedEvent);

        configureRabbit();
        requestSummaries();
    }

    private void bindRabbitQueues() {
        template.setReplyAddress(summaryReplyQueue.getName());
//        admin.declareBinding(BindingBuilder.bind(summaryReplyQueue).to(summaryReplyExchange));
        admin.declareBinding(BindingBuilder.bind(summaryRequestQueue).to(summaryRequestExchange));
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

    private void loadPublishers() {

    }

    // some configs are defined in RabbitConfiguration.java
    private void configureRabbit() {
        log.info("configuring rabbit");
        template.setBeforePublishPostProcessors(
                message ->
                        MessageBuilder
                                .fromMessage(message)
                                .setHeader("sender", self.getNodeId().toString())
                                .build()
        );

        log.info(registry);
        log.info(registry.getListenerContainerIds());
        log.info(registry.getListenerContainer(ReplicationListener.ID));
        log.info(registry.getListenerContainer(SearchListener.ID));


        register(SummaryReplyListener.ID, summaryReplyQueue);
        register(SummaryRequestListener.ID, summaryRequestQueue);
//        register(ReplicationListener.ID, replicationQueue);
//        register(SearchListener.ID, replicationQueue);
    }

    private void register(String containerID, Queue queue) {
        ((SimpleMessageListenerContainer) registry.getListenerContainer(containerID)).addQueues(queue);
    }

    private void requestSummaries() {
        template.convertAndSend(
                RabbitConfiguration.EXCHANGE_REQUEST_SUMMARY,
                "",
                new SummaryRequest(self),
                message ->
                    MessageBuilder
                            .fromMessage(message)
                            .setReplyTo(summaryReplyQueue.getName())
                            .build()
        );
//        template.convertAndSend(RabbitConfiguration.EXCHANGE_REQUEST_SUMMARY, "", new SummaryRequest(self));
    }

    public Node getNode() {
        return self; //fixme
    }

    public List<Node> getPublishers() {
        return nodeRepo.findByNodeRolePublisher();
    }

    public void addPublisher(Node node) {
        node.setNodeRole(NodeRole.PUBLISHER);
        nodeRepo.save(node);

        PublisherBindings bindings = new PublisherBindings();
        bindings.replicateBinding = BindingBuilder
                .bind(replicationQueue)
                .to(replicationExchange)
                .with(node.getNodeId().toString());
        bindings.searchBinding = BindingBuilder
                .bind(searchQueue)
                .to(searchExchange)
                .with(node.getNodeId().toString());
        bindingsMap.put(node.getNodeId(), bindings);

        admin.declareBinding(bindings.replicateBinding);
        admin.declareBinding(bindings.searchBinding);
    }

    public void removePublisher(Node node) {
        PublisherBindings bindings = bindingsMap.get(node.getNodeId());
        if (bindings == null) {
            return;
        }

        admin.removeBinding(bindings.replicateBinding);
        admin.removeBinding(bindings.searchBinding);
        bindingsMap.remove(node.getNodeId());

        node.setNodeRole(NodeRole.NONE);
        nodeRepo.save(node);
    }
}
