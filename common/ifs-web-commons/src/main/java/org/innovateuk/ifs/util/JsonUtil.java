package org.innovateuk.ifs.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public final class JsonUtil {
	private JsonUtil() {}
	
    public static String getSerializedObject(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        String json = "";
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return json;
    }

    public static <T> T getObjectFromJson(String json, Class<T> type) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        T obj = null;
        try {
            obj = mapper.readValue(json, type);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return obj;
    }
}
