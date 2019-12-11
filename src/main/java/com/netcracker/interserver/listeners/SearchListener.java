package com.netcracker.interserver.listeners;

import com.netcracker.interserver.messages.Query;
import com.netcracker.interserver.messages.SearchResult;
import com.netcracker.search.GeneralSearch;
import com.netcracker.services.service.NodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.netcracker.interserver.RabbitConfiguration.*;

@Component
@Log4j
@RequiredArgsConstructor
@RabbitListener(queues = {QUEUE_SEARCH, QUEUE_SEARCH_RESULT, QUEUE_SEARCH_QUERY})
public class SearchListener {
//    public static final String ID = "SearchListener";

    private final GeneralSearch generalSearch;
    private final RabbitTemplate template;
    private final NodeService nodeService;


    @RabbitHandler
    public void handleSearchQuery(@Payload String query, Message msg) {
        log.debug(msg);
        template.convertAndSend(EXCHANGE_SEARCH, "query", new Query(query, nodeService.getSelfUUID().toString()));
    }

    @RabbitHandler
    public void handleSearch(@Payload Query query, Message msg) {
        log.debug(msg);
        List<Object> result = generalSearch.search(query.getQuery());
        template.convertAndSend(EXCHANGE_SEARCH, query.getSenderKey(), new SearchResult(result));
    }

    @RabbitHandler
    public void handleSearchResult(@Payload SearchResult result, Message msg) {
        log.debug(msg);
        log.info(result); //todo : something meaningful
    }

}
