package com.netcracker.utils;


import com.netcracker.interserver.InterserverCommunication;
import com.netcracker.interserver.messages.Replicable;
import com.netcracker.services.repo.NodeRepo;
import com.netcracker.services.service.NodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import static com.netcracker.interserver.messages.ReplicationOperation.DELETE;
import static com.netcracker.interserver.messages.ReplicationOperation.UPDATE;

@Component
@Log4j
//@RequiredArgsConstructor
public class ReplicatedEntityListener {
    private static InterserverCommunication communication;
    private static NodeService nodeService;

    @Autowired
    private void setInterserverCommunication(InterserverCommunication communication) {
        ReplicatedEntityListener.communication = communication;
    }

    @Autowired
    private void setNodeService(NodeService nodeService) {
        ReplicatedEntityListener.nodeService = nodeService;
    }

    @PostPersist
    @PostUpdate
    public void postPersistOrUpdate(Replicable replicable) {
        log.info("got event persist or update");
        log.info(replicable);
        if (nodeService.getSelfUUID().equals(replicable.getOwnerId())) {
            communication.replicate(replicable, UPDATE);
        }
    }
    
    @PostRemove
    public void postRemove(Replicable replicable) {
        log.info("got event remove");
        log.info(replicable);
        if (nodeService.getSelfUUID().equals(replicable.getOwnerId())) {
            communication.replicate(replicable, DELETE);
        }
    }

}
