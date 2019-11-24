package com.netcracker.services.service;

import com.netcracker.exception.MultipleSelfNodesQnAException;
import com.netcracker.interserver.InterserverCommunication;
import com.netcracker.models.Node;
import com.netcracker.services.repo.NodeRepo;
import com.netcracker.utils.NodeRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Log4j
@RequiredArgsConstructor
public class NodeService {
    private final NodeRepo nodeRepo;

    @Value("${mynodename}")
    private String mynodename;
    @Autowired
    private InterserverCommunication interserverCommunication; // breaks dependency cycle; todo: move UUID into a separate bean?

    @Getter
    private Node self;

    @PostConstruct
    private void init() {
        loadSelf();
    }

    private void loadSelf() {
        List<Node> selfNodes = nodeRepo.findByNodeRoleSelf();
        if (selfNodes.size() > 1){
            throw new MultipleSelfNodesQnAException();
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


//    public Node save(Node node) {
//        if (node.getNodeId().equals(getSelfUUID())) {
//            return null;
//        }
//
//        if (node.getNodeRole() == null) {
//            node.setNodeRole(NodeRole.NONE);
//        }
//
//        return nodeRepo.save(node);
//    }

    public Node saveReplication(Node node) {
        if (node.getOwnerId().equals(getSelfUUID())) {
            return null;
        }

        return nodeRepo.save(node);
    }

    public List<Node> saveAllReplication(Iterable<Node> nodes) {
        List<Node> savedNodes = StreamSupport
                .stream(nodes.spliterator(), false)
                .filter(node -> !node.getOwnerId().equals(getSelfUUID())) // what to do if someone tries to send me nodes with my ownerid ???
                .collect(Collectors.toList());

        return nodeRepo.saveAll(savedNodes);
    }



    private Node saveNodeWithRole(Node node, NodeRole role) {
        Optional<Node> savedNode = nodeRepo.findById(node.getNodeId());
        if (savedNode.isPresent() && savedNode.get().getNodeRole() != NodeRole.NONE) {
            return null;
        }

        node.setNodeRole(role);
        return nodeRepo.save(node);
    }

    // returns true if node was removed
    private boolean removeNodeWithRole(UUID nodeId, NodeRole role) {
        if (getSelfUUID().equals(nodeId)) {
            return false;
        }

        Optional<Node> savedNode = nodeRepo.findById(nodeId);
        if (savedNode.isEmpty() || savedNode.get().getNodeRole() != role) {
            return false;
        }

        nodeRepo.delete(savedNode.get());
        return true;
    }

    public boolean savePublisher(Node publisher) {
        Node savedNode = saveNodeWithRole(publisher, NodeRole.PUBLISHER);
        if (savedNode != null) {
            interserverCommunication.createPublisherBinding(savedNode.getNodeId());
            return true;
        }

        return false;
    }

    //todo: search for new publishers
    public void deletePublisher(UUID publisherId) {
        if (removeNodeWithRole(publisherId, NodeRole.PUBLISHER)) {
            interserverCommunication.deletePublisherBinding(publisherId);
        }
    }

    //todo: stop listening to subscribe messages
    public boolean saveSubscriber(Node subscriber) {
        Node savedNode = saveNodeWithRole(subscriber, NodeRole.SUBSCRIBER);
        return savedNode != null;
    }

    public void deleteSubscriber(UUID subscriber) {
        removeNodeWithRole(subscriber, NodeRole.SUBSCRIBER);
    }


    public UUID getSelfUUID() {
        return self.getNodeId();
    }

    public List<Node> getSubscribers() {
        return nodeRepo.findByNodeRoleSubscribers();
    }

    public List<Node> getPublishers() {
        return nodeRepo.findByNodeRolePublisher();
    }


}

