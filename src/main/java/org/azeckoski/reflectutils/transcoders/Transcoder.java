package org.azeckoski.reflectutils.transcoders;

import java.util.Map;

/**
 * Simple contract for encoding and decoding objects to formats such as JSON or XML.
 */
public interface Transcoder {

    String DATA_KEY = "data";

    String getHandledFormat();

    String encode(Object object, String name, Map<String, Object> properties);

    default String encode(Object object, String name, Map<String, Object> properties, int maxDepth) {
        return encode(object, name, properties);
    }

    Map<String, Object> decode(String string);
}
