package com.taskbuddy.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    public static String serialize(Object target) {
        try {
            return OBJECT_MAPPER.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to serialize : " + target.toString(), e);
        }
    }

    public static <R> R deserialize(String target, Class<R> returnClass) {
        try {
            return OBJECT_MAPPER.readValue(target, returnClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to deserialize : " + target, e);
        }
    }
}
