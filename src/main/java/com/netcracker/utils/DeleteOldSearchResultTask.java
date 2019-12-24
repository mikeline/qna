package com.netcracker.utils;

import com.netcracker.services.repo.SearchResultRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@Log4j
@RequiredArgsConstructor
public class DeleteOldSearchResultTask {
    private static final long DELAY = 3600 * 24 * 1000;

    private final SearchResultRepo searchResultRepo;

    @Scheduled(fixedDelay = DELAY)
    public void deleteOldSearchResult() {
        final LocalDateTime beginDate = LocalDateTime.now().minusSeconds(DELAY / 1000);
        searchResultRepo.deleteOldRows(beginDate);
    }
}
