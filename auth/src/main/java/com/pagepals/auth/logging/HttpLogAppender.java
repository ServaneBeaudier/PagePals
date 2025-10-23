package com.pagepals.auth.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Optional;

/**
 * Appender Logback personnalisé envoyant les événements de log au microservice de logs via HTTP.
 * 
 * Permet de centraliser les journaux applicatifs des différents microservices (ici auth-service)
 * dans un service distant dédié (logs-service). Chaque événement de log est sérialisé en JSON
 * et transmis par une requête POST.
 *
 * Cette approche offre une meilleure traçabilité inter-services, notamment via les identifiants
 * de corrélation (traceId, spanId) stockés dans le MDC.
 *
 */
public class HttpLogAppender extends AppenderBase<ILoggingEvent> {

    /** URL du microservice de logs recevant les événements (ex : http://logs-service:8095/logs). */
    private String logsServiceUrl;

    /** Nom du service émetteur (ex : auth-service). */
    private String serviceName;

    /** Active ou désactive l’envoi distant des logs. */
    private boolean enabled = true;

    /** Délai maximal de connexion HTTP en millisecondes. */
    private int connectTimeoutMs = 800;

    /** Délai maximal de lecture HTTP en millisecondes. */
    private int readTimeoutMs = 1200;

    /** Définit l’URL du service de logs distant. */
    public void setLogsServiceUrl(String logsServiceUrl) {
        this.logsServiceUrl = logsServiceUrl;
    }

    /** Définit le nom du service émettant les logs. */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /** Active ou désactive l’appender. */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** Configure le délai maximal de connexion HTTP. */
    public void setConnectTimeoutMs(int ms) {
        this.connectTimeoutMs = ms;
    }

    /** Configure le délai maximal de lecture HTTP. */
    public void setReadTimeoutMs(int ms) {
        this.readTimeoutMs = ms;
    }

    /**
     * Envoie un événement de log au service distant.
     * Sérialise les métadonnées (niveau, logger, message, exception, contexte MDC)
     * au format JSON et les transmet par POST.
     *
     * En cas d’erreur réseau ou serveur, aucune exception n’est propagée afin de
     * ne jamais interrompre le processus de logging local.
     *
     * @param event l’événement Logback à transmettre
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

            // On ignore les erreurs HTTP pour ne pas boucler sur le logger
            int code = conn.getResponseCode();
            if (code >= 400) {
                // Pas d'action : on ne propage jamais l'erreur
            }
            conn.disconnect();
        } catch (Exception ignored) {
            // Ne jamais interrompre le logging applicatif
        }
    }

    /**
     * Transforme une chaîne en version JSON échappée.
     * Remplace les caractères spéciaux pour éviter les erreurs de format.
     *
     * @param s chaîne d'entrée
     * @return version échappée de la chaîne pour JSON, ou "null" si la valeur est nulle
     */
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
