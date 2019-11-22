package com.netcracker.interserver;

import com.netcracker.interserver.listeners.NodeRoleListener;
import com.netcracker.interserver.listeners.ReplicationListener;
import com.netcracker.interserver.listeners.SearchListener;
import com.netcracker.interserver.messages.Replicate;
import com.netcracker.interserver.messages.SummaryRequest;
import com.netcracker.models.Node;
import com.netcracker.services.repo.NodeRepo;
import com.netcracker.services.repo.PostRepo;
import com.netcracker.utils.NodeRole;
import com.sun.istack.NotNull;
//import exception.MultipleSelfNodesQnAException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.netcracker.interserver.RabbitConfiguration.EXCHANGE_REQUEST_SUMMARY;

@Component
@RequiredArgsConstructor
@Log4j
public class ThisNode {
    private final NodeRepo nodeRepo;
    private final PostRepo postRepo;

    private final RabbitTemplate template;
    private final RabbitAdmin admin;
    private final RabbitListenerEndpointRegistry registry;

    private final TopicExchange replicationExchange;
    private final DirectExchange searchExchange;

    private final Queue replicationQueue;
    private final Queue searchQueue;
    private final Queue nodeRoleQueue;

    //    private Map<UUID, PublisherBindings> bindingsMap = new HashMap<>(); // maps PublisherUUID to bindings
    @Getter
    private Node self;
    private Map<UUID, Binding> publisherBindings = new HashMap<>();


    @Qualifier(EXCHANGE_REQUEST_SUMMARY) //fixme qualifiers do not work with lombok wtf?
    private final FanoutExchange summaryRequestExchange;
//    @Qualifier(RabbitConfiguration.EXCHANGE_REPLY_SUMMARY)
//    private final FanoutExchange summaryReplyExchange;
//
//    private class PublisherBindings {
//        Binding replicateBinding;
//        Binding searchBinding;
//    }

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
//        template.setReplyAddress(nodeRoleQueue.getName());
//        admin.declareBinding(BindingBuilder.bind(summaryReplyQueue).to(summaryReplyExchange));
        admin.declareBinding(BindingBuilder.bind(nodeRoleQueue).to(summaryRequestExchange));
    }

    @Value("${mynodename}")
    private String mynodename;

    private void loadSelf() {
        List<Node> selfNodes = nodeRepo.findByNodeRoleSelf();
        if (selfNodes.size() > 1){
            throw new RuntimeException("Mulitple nodes are declared as 'SELF' in the database");//MultipleSelfNodesQnAException();
        }

        if (selfNodes.size() == 0) {
            log.info("Generating new self node");
            self = new Node();
            self.setName(mynodename); // fixme
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
        log.debug("configuring rabbit");
        template.addBeforePublishPostProcessors(
                message -> {
                    message.getMessageProperties().setReplyTo(nodeRoleQueue.getName());
                    message.getMessageProperties().getHeaders().put("sender", self.getNodeId().toString());
                    return message;
                }
        );

        log.debug(registry);

        register(NodeRoleListener.ID, nodeRoleQueue);

//        register(SummaryReplyListener.ID, summaryReplyQueue);
//        register(SummaryRequestListener.ID, summaryRequestQueue);
        register(ReplicationListener.ID, replicationQueue);
//        register(SearchListener.ID, replicationQueue);
    }

    private void register(String containerID, Queue queue) {
        ((SimpleMessageListenerContainer) registry.getListenerContainer(containerID)).addQueues(queue);
    }

    private void requestSummaries() {
        template.convertAndSend(EXCHANGE_REQUEST_SUMMARY,"", new SummaryRequest(self));
    }

    public Node getNode() {
        return self; //fixme
    }

    public List<Node> getPublishers() {
        return nodeRepo.findByNodeRolePublisher();
    }

    public boolean addPublisher(@NotNull Node node) {
        boolean saved = saveNodeWithRole(node, NodeRole.PUBLISHER);
        if (saved) {
            Binding newPublisherBinding =
                    BindingBuilder
                            .bind(replicationQueue)
                            .to(replicationExchange)
                            .with(node.getNodeId().toString());
            publisherBindings.put(node.getNodeId(), newPublisherBinding);
            admin.declareBinding(newPublisherBinding);
        }
        return saved;
    }

    private void sendCurrentDB(Node node) {
        Replicate replicate = new Replicate(postRepo.findAll());

//        template.convertAndSend(EXCHANGE_PUBLISH_REPLICATION, self.getNodeId().toString(), replicate);
    }

    public void removePublisher(@NotNull Node node) {
        if (node.getNodeId().equals(self.getNodeId())) {
            log.debug("please stop!");
            return;
        }

        Binding publisher = publisherBindings.get(node.getNodeId());
        if (publisher == null ) {
            return;
        }

        publisherBindings.remove(node.getNodeId());
        admin.removeBinding(publisher);
    }

    public List<Node> getSubscribers() {
        return nodeRepo.findByNodeRoleSubscribers();
    }


    public boolean addSubscriber(Node node) {
        return saveNodeWithRole(node, NodeRole.SUBSCRIBER);
    }

    private boolean saveNodeWithRole(Node node, NodeRole role) {
        Optional<Node> savedNode = nodeRepo.findById(node.getNodeId());
        if (savedNode.isPresent() && savedNode.get().getNodeRole() != NodeRole.NONE) {
            return false;
        }

        node.setNodeRole(role);
        nodeRepo.save(node);

        return true;
    }
}
