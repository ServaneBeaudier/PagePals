package com.pagepals.circle.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

/**
 * Appender personnalisé pour Logback qui envoie les logs du microservice
 * vers un service distant (logs-service) via HTTP.
 *
 * Cet appender permet de centraliser les journaux d'exécution de chaque microservice
 * en les transmettant sous format JSON au service de logs.
 * 
 * Caractéristiques :
 * - Ne perturbe jamais le fonctionnement de l’application même en cas d’échec réseau
 * - Supporte la transmission d’informations de traçage (traceId, spanId)
 * - Formate les messages et exceptions en JSON
 */
public class HttpLogAppender extends AppenderBase<ILoggingEvent> {

    /** URL du service de logs (ex. http://logs-service:8095/logs en Docker) */
    private String logsServiceUrl;

    /** Nom du service émetteur (ex. circle-service, auth-service) */
    private String serviceName;

    /** Indique si l’envoi des logs au service distant est activé */
    private boolean enabled = true;

    /** Délai maximum de connexion en millisecondes */
    private int connectTimeoutMs = 800;

    /** Délai maximum de lecture en millisecondes */
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

    /**
     * Envoie un événement de log au service distant sous forme de requête HTTP POST.
     * Cette méthode ne doit jamais interrompre l’application, même en cas d’erreur.
     *
     * @param event événement de log à traiter
     */
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

            // Ne bloque jamais le processus en cas d’erreur HTTP
            int code = conn.getResponseCode();
            if (code >= 400) {
                // On ne fait rien pour éviter une boucle infinie de logs
            }
            conn.disconnect();
        } catch (Exception ignored) {
            // Surtout ne jamais casser l'application à cause du logging
        }
    }

    /** Échappe les caractères spéciaux pour formater une chaîne JSON valide. */
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
