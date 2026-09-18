package com.umerqureshicodes.tidier.embeddings;

import com.umerqureshicodes.tidier.users.AppUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public List<SearchResultDTO> search(@RequestParam("q") String query,
                                        @RequestParam(value = "type", defaultValue = "videos") String type,
                                        @AuthenticationPrincipal AppUser user) {
        Embedding.Kind kind = "montages".equalsIgnoreCase(type) ? Embedding.Kind.MONTAGE : Embedding.Kind.VIDEO;
        return searchService.search(query, kind, user.getId(), user.getUsername());
    }
}
