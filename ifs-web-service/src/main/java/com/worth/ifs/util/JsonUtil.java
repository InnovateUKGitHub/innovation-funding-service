package com.worth.ifs.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public final class JsonUtil {
	private JsonUtil() {}
	
    private static final Log log = LogFactory.getLog(JsonUtil.class);
    public static String getSerializedObject(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e);
        }
        return json;
    }

    public static <T> T getObjectFromJson(String json, Class<T> type) {
        ObjectMapper mapper = new ObjectMapper();
        T obj = null;
        try {
            obj = mapper.readValue(json, type);
        } catch (IOException e) {
            log.error(e);
        }
        return obj;
    }
}