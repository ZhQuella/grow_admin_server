package dev.gad.utils.support.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

/**
 * @author LIANGQI
 * @version 1.0
 * @Desc
 */
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtil() {

    }

    @SneakyThrows(JsonProcessingException.class)
    public static String toJson(Object obj) {
        return objectMapper.writeValueAsString(obj);
    }

    @SneakyThrows(JsonProcessingException.class)
    public static byte[] toJsonByte(Object obj) {
        return objectMapper.writeValueAsBytes(obj);
    }

    @SneakyThrows({JsonProcessingException.class})
    public static <T> T toBean(String json, TypeReference<T> _ref) {
        return objectMapper.readValue(json, _ref);
    }

    @SneakyThrows({JsonProcessingException.class})
    public static <T> T toBean(String json, Class<T> _ref) {
        return objectMapper.readValue(json, _ref);
    }

}
