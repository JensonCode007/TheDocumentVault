package org.example.document_pro_v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.document_pro_v1.dto.SearchResponse;
import org.example.document_pro_v1.jwtSecurity.UserPrincipal;
import org.example.document_pro_v1.service.SearchService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search-query")
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<SearchResponse> searchQuery(@RequestParam(name = "query") String query,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader)
    {
        log.info("Search Query received : {}", query);
        return new ResponseEntity<>(searchService.querySearch(query, authHeader), HttpStatus.OK);

    }

    @PostMapping("/rag-search")
    public String ragSearch(@AuthenticationPrincipal UserPrincipal userPrincipal, String query){
        return searchService.ragSearch(userPrincipal, query);
    }
}
