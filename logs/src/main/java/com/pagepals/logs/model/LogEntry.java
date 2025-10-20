package com.pagepals.logs.model;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "logs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LogEntry {
    @Id
    private String id;

    private String serviceName;   // ex: auth-service
    private String level;         // INFO/WARN/ERROR
    private String logger;        // com.pagepals.xxx.Classe
    private String thread;        // http-nio-8080-exec-1
    private String message;
    private String exception;     // stacktrace si présent
    private String traceId;       // pour corréler les requêtes
    private String spanId;

    private Instant timestamp;
    private String path;          // ex: /api/users
    private String method;        // GET/POST...
    private String remoteIp;      

}