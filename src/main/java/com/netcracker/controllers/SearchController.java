package com.netcracker.controllers;

import com.netcracker.interserver.messages.SearchRequest;
import com.netcracker.models.SearchResult;
import com.netcracker.search.GeneralSearch;
import com.netcracker.services.repo.SearchResultRepo;
import com.netcracker.services.service.NodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

import static com.netcracker.interserver.RabbitConfiguration.EXCHANGE_SEARCH;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RestController
@RequestMapping("/search")
public class SearchController {

//    private final GeneralSearch generalSearch;
    private final SearchResultRepo searchResultRepo;
    private final RabbitTemplate template;
    private final NodeService nodeService;

    @GetMapping(value = "/{text}")
    @ResponseBody
    public ResponseEntity<UUID> search(@PathVariable String text) {
        UUID resultId = UUID.randomUUID();
        SearchResult searchResult = new SearchResult();
        searchResult.setId(resultId);
        searchResultRepo.save(searchResult);
        template.convertAndSend(EXCHANGE_SEARCH, "query", new SearchRequest(text, nodeService.getSelfUUID(), searchResult.getId()));
        return new ResponseEntity<>(resultId, OK);
    }

    @RequestMapping("/result/{id}")
    @ResponseBody
    public ResponseEntity<String> searchById(@PathVariable UUID id) {
        String result = searchResultRepo.findById(id).orElse(new SearchResult(null, "")).getResult();
        return new ResponseEntity<String>(result, OK);
    }

}
