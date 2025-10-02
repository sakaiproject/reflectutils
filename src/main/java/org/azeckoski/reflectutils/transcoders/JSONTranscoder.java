package org.azeckoski.reflectutils.transcoders;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Jackson-backed implementation for JSON encoding/decoding.
 */
public class JSONTranscoder implements Transcoder {

    private final ObjectMapper mapper;
    private final boolean includeClassField;

    public JSONTranscoder() {
        this(false, true, false);
    }

    public JSONTranscoder(boolean humanOutput, boolean includeNulls, boolean includeClassField) {
        this.includeClassField = includeClassField;
        this.mapper = new ObjectMapper();
        if (humanOutput) {
            this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        if (!includeNulls) {
            this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
    }

    @Override
    public String getHandledFormat() {
        return "json";
    }

    @Override
    public String encode(Object object, String name, Map<String, Object> properties) {
        Objects.requireNonNull(object, "object cannot be null");
        try {
            Object payload = buildPayload(object, name, properties);
            return mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to encode object to JSON", e);
        }
    }

    private Object buildPayload(Object object, String name, Map<String, Object> properties) {
        boolean hasName = name != null && !name.isEmpty();
        boolean hasProperties = properties != null && !properties.isEmpty();

        if (!hasName && !hasProperties && !includeClassField) {
            return object;
        }

        Map<String, Object> envelope = new LinkedHashMap<>();
        if (hasProperties) {
            envelope.putAll(properties);
        }
        if (includeClassField && !(object instanceof Map)) {
            envelope.put("class", object.getClass().getName());
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
                return mapper.convertValue(node, new TypeReference<Map<String, Object>>() {});
            }
            Map<String, Object> result = new LinkedHashMap<>();
            result.put(DATA_KEY, mapper.convertValue(node, Object.class));
            return result;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to decode JSON", e);
        }
    }
}
