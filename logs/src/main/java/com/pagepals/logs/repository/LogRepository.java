package com.pagepals.logs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pagepals.logs.model.LogEntry;

public interface LogRepository extends MongoRepository<LogEntry, String>{

}
