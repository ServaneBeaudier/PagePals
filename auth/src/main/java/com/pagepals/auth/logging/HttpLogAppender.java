package com.pagepals.auth.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

public class HttpLogAppender extends AppenderBase<ILoggingEvent> {

    private String logsServiceUrl; // ex: http://logs-service:8095/logs (docker) / http://localhost:8095/logs
                                   // (local)
    private String serviceName; // ex: auth-service
    private boolean enabled = true;
    private int connectTimeoutMs = 800;
    private int readTimeoutMs = 1200;

    public void setLogsServiceUrl(String logsServiceUrl) {
        this.logsServiceUrl = logsServiceUrl;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setConnectTimeoutMs(int ms) {
        this.connectTimeoutMs = ms;
    }

    public void setReadTimeoutMs(int ms) {
        this.readTimeoutMs = ms;
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!enabled || logsServiceUrl == null || logsServiceUrl.isBlank())
            return;
        try {
            String stack = Optional.ofNullable(event.getThrowableProxy())
                    .map(p -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append(p.getClassName()).append(": ").append(p.getMessage()).append("\n");
                        for (var ste : p.getStackTraceElementProxyArray()) {
                            sb.append("    at ").append(ste.getStackTraceElement().toString()).append("\n");
                        }
                        return sb.toString();
                    })
                    .orElse(null);

            // Récupère éventuellement traceId/spanId depuis MDC
            String traceId = event.getMDCPropertyMap().get("traceId");
            String spanId = event.getMDCPropertyMap().get("spanId");
            String path = event.getMDCPropertyMap().get("http.path");
            String method = event.getMDCPropertyMap().get("http.method");
            String remote = event.getMDCPropertyMap().get("http.remote");

            String payload = """
                    {
                      "serviceName": %s,
                      "level": %s,
                      "logger": %s,
                      "thread": %s,
                      "message": %s,
                      "exception": %s,
                      "traceId": %s,
                      "spanId": %s,
                      "timestamp": %s,
                      "path": %s,
                      "method": %s,
                      "remoteIp": %s
                    }
                    """.formatted(
                    json(serviceName),
                    json(event.getLevel().toString()),
                    json(event.getLoggerName()),
                    json(event.getThreadName()),
                    json(event.getFormattedMessage()),
                    json(stack),
                    json(traceId),
                    json(spanId),
                    json(Instant.ofEpochMilli(event.getTimeStamp()).toString()),
                    json(path),
                    json(method),
                    json(remote));

            URL url = new URL(logsServiceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectTimeoutMs);
            conn.setReadTimeout(readTimeoutMs);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            // On lit juste le code, on n’échoue jamais le log appelant
            int code = conn.getResponseCode();
            if (code >= 400) {
                // On ne remonte pas d’exception pour ne pas boucler le logging
            }
            conn.disconnect();
        } catch (Exception ignored) {
            // Surtout ne jamais casser l'app à cause du logging
        }
    }

    private static String json(String s) {
        if (s == null)
            return "null";
        return "\"" + s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                + "\"";
    }

}