package com.tgerstel.quizmaster.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.Set;

public final class LogSanitizer {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password",
            "pass",
            "pwd",
            "secret",
            "token",
            "accessToken",
            "refreshToken"
    );

    private LogSanitizer() {}

    public static String sanitize(String body) {
        if (body == null || body.isBlank()) {
            return body;
        }

        // Try JSON first
        try {
            JsonNode root = MAPPER.readTree(body);
            maskNode(root);
            return MAPPER.writeValueAsString(root);
        } catch (Exception e) {
            // fallback for non-JSON
            return maskByRegex(body);
        }
    }

    private static void maskNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            Iterator<String> fields = obj.fieldNames();

            while (fields.hasNext()) {
                String field = fields.next();
                JsonNode child = obj.get(field);

                if (SENSITIVE_FIELDS.contains(field)) {
                    obj.put(field, "***");
                } else {
                    maskNode(child);
                }
            }
        } else if (node.isArray()) {
            node.forEach(LogSanitizer::maskNode);
        }
    }

    private static String maskByRegex(String input) {
        return input
                .replaceAll("(?i)(password|pass|pwd|secret|token)=([^&\\s]+)", "$1=***")
                .replaceAll("(?i)\"(password|pass|pwd|secret|token)\"\\s*:\\s*\".*?\"", "\"$1\":\"***\"");
    }
}
