package com.netcracker.interserver;

import com.netcracker.interserver.messages.SummaryRequest;
import com.netcracker.models.Node;
import com.netcracker.services.repo.NodeRepo;
import com.netcracker.utils.NodeRole;
import com.netcracker.exception.MultipleSelfNodesQnAException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThisNode {
    private final NodeRepo nodeRepo;
    private final RabbitTemplate template;
    private final RabbitAdmin admin;
    private final ApplicationContext context;
    private final RabbitListenerEndpointRegistry registry;

    private final TopicExchange replicationExchange;
    private final DirectExchange searchExchange;

    private Node self;
    private Queue replicationQueue;
    private Queue searchQueue;
    private Map<UUID, PublisherBindings> bindingsMap = new HashMap<>(); // maps PublisherUUID to bindings

    private class PublisherBindings {
        Binding replicateBinding;
        Binding searchBinding;
    }

    @PostConstruct
    public void init() {
        loadSelf();
        log.info("self:  " + self);
        loadPublishers();
        configureRabbit();
        requestSummaries();
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
//        template.setBeforePublishPostProcessors(
//                message ->
//                        MessageBuilder
//                                .fromMessage(message)
//                                .setHeader("sender", self.getNodeId().toString())
//                                .build()
//        );

        replicationQueue = admin.declareQueue();
        searchQueue = admin.declareQueue();
        log.info("registry", registry);
//        ((AbstractMessageListenerContainer) registry.getListenerContainer(ReplicationListener.ID)).addQueues(replicationQueue);
//        ((AbstractMessageListenerContainer) registry.getListenerContainer(SearchListener.ID)).addQueues(replicationQueue);
    }

    private void requestSummaries() {
        template.convertAndSend(RabbitConfiguration.EXCHANGE_REQUEST_SUMMARY, "", new SummaryRequest());
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
