package com.pagepals.logs.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.pagepals.logs.model.LogEntry;
import com.pagepals.logs.repository.LogRepository;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogRepository logRepository;

    public LogController(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @PostMapping
    public LogEntry saveLog(@RequestBody LogEntry log) {
        log.setTimestamp(Instant.now());
        return logRepository.save(log);
    }

    @GetMapping
    public List<LogEntry> getLogs() {
        return logRepository.findAll();
    }
}
