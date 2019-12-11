package com.netcracker.controllers;

import com.netcracker.search.GeneralSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RestController
@RequestMapping("/search")
public class SearchController {

    private final GeneralSearch generalSearch;

    @GetMapping(value = "/{text}")
    @ResponseBody
    public ResponseEntity<Collection<Object>> search(@PathVariable String text) {

        Collection<Object> posts = generalSearch.search(text); // fixme: insert your function that will use GeneralSearch

        return new ResponseEntity<>(posts, OK);
    }

}
