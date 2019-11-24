package com.netcracker.interserver;

import com.netcracker.interserver.listeners.NodeRoleListener;
import com.netcracker.interserver.listeners.ReplicationListener;
import com.netcracker.interserver.messages.Replicate;
import com.netcracker.interserver.messages.SummaryRequest;
import com.netcracker.models.Node;
import com.netcracker.services.repo.PostRepo;
import com.netcracker.services.service.NodeService;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.*;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.netcracker.interserver.RabbitConfiguration.EXCHANGE_REQUEST_SUMMARY;
import static com.netcracker.interserver.RabbitConfiguration.EXCHANGE_USER_AUTHENTICATION;

//import exception.MultipleSelfNodesQnAException;

/*
* InterserverCommunication class is responsible for configuring RabbitMQ at runtime
* Static configuration is done in RabbitConfiguration.java
* */

@Component
@Log4j
public class InterserverCommunication {
    private final NodeService nodeService;
    private final PostRepo postRepo;

    private final RabbitTemplate template;
    private final RabbitAdmin admin;
    private final RabbitListenerEndpointRegistry registry;

    private final TopicExchange replicationExchange;
    private final DirectExchange searchExchange;

    private final FanoutExchange summaryRequestExchange;
    private final FanoutExchange userAuthenticationExchange;

    private final Queue replicationQueue;
    private final Queue searchQueue;
    private final Queue nodeRoleQueue;
    private final Queue userAuthenticationQueue;

    private final String mynodename;

    //    private Map<UUID, PublisherBindings> bindingsMap = new HashMap<>(); // maps PublisherUUID to bindings
    private Map<UUID, Binding> publisherBindings = new HashMap<>();

    public InterserverCommunication(NodeService nodeService,
                                    PostRepo postRepo,
                                    RabbitTemplate template,
                                    RabbitAdmin admin,
                                    RabbitListenerEndpointRegistry registry,
                                    TopicExchange replicationExchange,
                                    DirectExchange searchExchange,
                                    @Qualifier(EXCHANGE_REQUEST_SUMMARY) FanoutExchange summaryRequestExchange,
                                    @Qualifier(EXCHANGE_USER_AUTHENTICATION) FanoutExchange userAuthenticationExchange,
                                    Queue replicationQueue,
                                    Queue searchQueue,
                                    Queue nodeRoleQueue,
                                    Queue userAuthenticationQueue,
                                    @Value("${mynodename}") String mynodename) {
        this.nodeService = nodeService;
        this.postRepo = postRepo;
        this.template = template;
        this.admin = admin;
        this.registry = registry;
        this.replicationExchange = replicationExchange;
        this.searchExchange = searchExchange;
        this.summaryRequestExchange = summaryRequestExchange;
        this.userAuthenticationExchange = userAuthenticationExchange;
        this.replicationQueue = replicationQueue;
        this.searchQueue = searchQueue;
        this.nodeRoleQueue = nodeRoleQueue;
        this.userAuthenticationQueue = userAuthenticationQueue;
        this.mynodename = mynodename;
    }


//    @Qualifier(RabbitConfiguration.EXCHANGE_REPLY_SUMMARY)
//    private final FanoutExchange summaryReplyExchange;

    @PostConstruct
    public void init() {
        log.debug("post construct");
        bindRabbitQueues();
    }

    @EventListener
    public void handleContextRefreshedEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.debug("context refreshed");
        log.debug(contextRefreshedEvent);

        configureRabbit();
        requestSummaries();
    }

    private void bindRabbitQueues() {
        admin.declareBinding(BindingBuilder.bind(nodeRoleQueue).to(summaryRequestExchange));
        admin.declareBinding(BindingBuilder.bind(userAuthenticationQueue).to(userAuthenticationExchange));
    }

    // some configs are defined in RabbitConfiguration.java
    private void configureRabbit() {
        log.debug("configuring rabbit");
        template.addBeforePublishPostProcessors(
                message -> {
                    message.getMessageProperties().setReplyTo(nodeRoleQueue.getName());
                    message.getMessageProperties().getHeaders().put("sender", nodeService.getSelfUUID());
                    return message;
                }
        );

        log.debug(registry);

        register(NodeRoleListener.ID, nodeRoleQueue);
        register(ReplicationListener.ID, replicationQueue);
    }

    private void register(String containerID, Queue queue) {
        ((SimpleMessageListenerContainer) registry.getListenerContainer(containerID)).addQueues(queue);
    }

    public void requestSummaries() {
        template.convertAndSend(EXCHANGE_REQUEST_SUMMARY,"", new SummaryRequest(nodeService.getSelf()));
    }

    public void createPublisherBinding(UUID publisherId) {
        if (publisherBindings.containsKey(publisherId)) {
            return;
        }

        Binding newPublisherBinding =
                BindingBuilder
                        .bind(replicationQueue)
                        .to(replicationExchange)
                        .with(publisherId.toString());
        publisherBindings.put(publisherId, newPublisherBinding);
        admin.declareBinding(newPublisherBinding);
    }

    public void deletePublisherBinding(UUID publisherId) {
        Binding binding = publisherBindings.get(publisherId);
        if (binding == null) {
            return;
        }

        publisherBindings.remove(publisherId);
        admin.removeBinding(binding);
    }

    private void sendCurrentDB(Node node) {
        Replicate replicate = new Replicate(postRepo.findAll());

//        template.convertAndSend(EXCHANGE_PUBLISH_REPLICATION, self.getNodeId().toString(), replicate);
    }


}
