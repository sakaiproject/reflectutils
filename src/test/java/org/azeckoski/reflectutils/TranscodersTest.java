package org.azeckoski.reflectutils;

import org.azeckoski.reflectutils.transcoders.JSONTranscoder;
import org.azeckoski.reflectutils.transcoders.Transcoder;
import org.azeckoski.reflectutils.transcoders.XMLTranscoder;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TranscodersTest {

    @Test
    void jsonEncodeDecodeRoundTrip() {
        Transcoder transcoder = new JSONTranscoder();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("name", "reflectutils");
        payload.put("version", 21);

        String encoded = transcoder.encode(payload, null, null);
        Map<String, Object> decoded = transcoder.decode(encoded);

        assertEquals("reflectutils", decoded.get("name"));
        assertEquals(21, decoded.get("version"));
    }

    @Test
    void jsonEncodeWithProperties() {
        Transcoder transcoder = new JSONTranscoder(true, true, true);
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("meta", "test");

        String encoded = transcoder.encode(new TestBean("alpha"), "bean", properties);
        Map<String, Object> decoded = transcoder.decode(encoded);

        assertEquals("test", decoded.get("meta"));
        Map<String, Object> bean = (Map<String, Object>) decoded.get("bean");
        assertEquals("alpha", bean.get("value"));
    }

    @Test
    void xmlEncodeDecodeRoundTrip() {
        Transcoder transcoder = new XMLTranscoder();
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("project", "reflectutils");
        payload.put("jdk", 21);

        String encoded = transcoder.encode(payload, null, null);
        Map<String, Object> decoded = transcoder.decode(encoded);

        assertEquals("reflectutils", decoded.get("project"));
        assertEquals(21, decoded.get("jdk"));
    }

    private static class TestBean {
        private final String value;

        TestBean(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
