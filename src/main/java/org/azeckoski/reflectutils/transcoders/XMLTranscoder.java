package org.azeckoski.reflectutils.transcoders;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Jackson-backed implementation for XML encoding/decoding.
 */
public class XMLTranscoder implements Transcoder {

    private final XmlMapper mapper;

    public XMLTranscoder() {
        this(false, true);
    }

    public XMLTranscoder(boolean humanOutput, boolean includeNulls) {
        this.mapper = new XmlMapper();
        if (humanOutput) {
            this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        if (!includeNulls) {
            this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
    }

    @Override
    public String getHandledFormat() {
        return "xml";
    }

    @Override
    public String encode(Object object, String name, Map<String, Object> properties) {
        Objects.requireNonNull(object, "object cannot be null");
        try {
            Object payload = buildPayload(object, name, properties);
            return mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to encode object to XML", e);
        }
    }

    private Object buildPayload(Object object, String name, Map<String, Object> properties) {
        boolean hasName = name != null && !name.isEmpty();
        boolean hasProperties = properties != null && !properties.isEmpty();

        if (!hasName && !hasProperties) {
            return object;
        }

        Map<String, Object> envelope = new LinkedHashMap<>();
        if (hasProperties) {
            envelope.putAll(properties);
        }
        String key = hasName ? name : DATA_KEY;
        envelope.put(key, object);
        return envelope;
    }

    @Override
    public Map<String, Object> decode(String string) {
        Objects.requireNonNull(string, "string cannot be null");
        try {
            JsonNode node = mapper.readTree(string);
            if (node == null || node.isNull()) {
                return new LinkedHashMap<>();
            }
            if (node.isObject()) {
                Map<String, Object> result = mapper.convertValue(node, new TypeReference<Map<String, Object>>() {});
                return sanitize(result);
            }
            Map<String, Object> result = new LinkedHashMap<>();
            result.put(DATA_KEY, mapper.convertValue(node, Object.class));
            return sanitize(result);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to decode XML", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> sanitize(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            result.put(entry.getKey(), coerceValue(entry.getValue()));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Object coerceValue(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> coerced = new LinkedHashMap<>();
            map.forEach((k, v) -> coerced.put(String.valueOf(k), coerceValue(v)));
            return coerced;
        }
        if (value instanceof Iterable<?> iterable) {
            java.util.List<Object> list = new java.util.ArrayList<>();
            for (Object item : iterable) {
                list.add(coerceValue(item));
            }
            return list;
        }
        if (value instanceof String s) {
            if (s.matches("[-+]?\\d+")) {
                try {
                    if (s.length() < 10) {
                        return Integer.valueOf(s);
                    }
                    return Long.valueOf(s);
                } catch (NumberFormatException ignored) {
                    // fall back to double below
                }
            }
            if (s.matches("[-+]?\\d*\\.\\d+")) {
                try {
                    return Double.valueOf(s);
                } catch (NumberFormatException ignored) {
                    return s;
                }
            }
            if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
                return Boolean.valueOf(s);
            }
        }
        return value;
    }
}
