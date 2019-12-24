package com.netcracker.interserver.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netcracker.interserver.messages.Query;
import com.netcracker.interserver.messages.Replicable;
import com.netcracker.interserver.messages.SearchRequest;
import com.netcracker.models.SearchResult;
import com.netcracker.search.GeneralSearch;
import com.netcracker.services.repo.SearchResultRepo;
import com.netcracker.services.service.NodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.netcracker.interserver.RabbitConfiguration.*;

@Component
@Log4j
@RequiredArgsConstructor
@RabbitListener(queues = {QUEUE_SEARCH, QUEUE_SEARCH_RESULT, QUEUE_SEARCH_QUERY})
public class SearchListener {
    private final NodeService nodeService;
    private final RabbitTemplate template;
    private final GeneralSearch generalSearch;
    private final ObjectMapper mapper;
    private final SearchResultRepo searchResultRepo;

    @RabbitHandler
    public void handleSearchQuery(@Payload SearchRequest searchRequest, Message msg) {
        log.debug(msg);
        Query query = new Query();
        query.setQuery(searchRequest.getQuery());
        query.setSendTo(searchRequest.getSendTo());
        query.setId(searchRequest.getId());
        query.setFilterId(nodeService.getSelfUUID());
        template.convertAndSend(QUEUE_SEARCH, query);
    }

    @RabbitHandler
    @Transactional
    public void handleSearch(@Payload Query query, Message msg) throws Exception {
        log.debug(msg);

        String result = generalSearch.search(UUID.fromString(query.getQuery()), "")
                .stream()
                .filter(obj -> obj instanceof Replicable)
                .map(obj -> (Replicable) obj)
                .filter(rep -> query.getFilterId().equals(rep.getOwnerId())) // todo: move this filter into the search
                .map(rep -> {
                    String res = "";
                    try {
                        res = mapper.writeValueAsString(rep);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    return res;
                })
                .collect(Collectors.joining(" "));

        template.convertAndSend(EXCHANGE_SEARCH, query.getSendTo().toString(), new SearchResult(null, query.getId(), result, LocalDateTime.now(), LocalDateTime.now())); //fixme
    }

    @RabbitHandler
    public void handleSearchResult(@Payload SearchResult searchResult, Message msg) {
        log.debug(msg);
        SearchResult persistedResult = searchResultRepo.findById(searchResult.getId()).orElse(new SearchResult(null, searchResult.getId(), "", LocalDateTime.now(), LocalDateTime.now()));


        persistedResult.setResult(persistedResult.getResult() + ' ' + searchResult.getResult());
        searchResultRepo.save(persistedResult);
    }

}
