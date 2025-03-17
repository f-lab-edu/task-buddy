package com.taskbuddy.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    /**
     * 객체를 JSON 문자열로 직렬화한다.
     *
     * @param target 직렬화할 객체
     * @return JSON 문자열
     * @throws RuntimeException JSON 변환 중 오류가 발생한 경우 예외를 던집니다.
     */
    public static String serialize(Object target) {
        try {
            return OBJECT_MAPPER.writeValueAsString(target);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to serialize : " + target.toString(), e);
        }
    }

    /**
     * JSON 문자열을 주어진 타입의 객체로 역직렬화한다.
     *
     * @param target JSON 문자열
     * @param returnClass 변환할 객체 타입
     * @param <R> 변환할 객체의 타입
     * @return 역직렬화된 객체
     * @throws RuntimeException JSON 변환 중 오류가 발생한 경우 예외를 던진다.
     */
    public static <R> R deserialize(String target, Class<R> returnClass) {
        try {
            return OBJECT_MAPPER.readValue(target, returnClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("failed to deserialize : " + target, e);
        }
    }
}
