package com.worth.ifs.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class JpaConverterJson implements AttributeConverter<JsonNode, String> {
	private static final Log LOG = LogFactory.getLog(JpaConverterJson.class);
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonNode meta) {
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (JsonProcessingException e) {
            LOG.error(e);
            return null;
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        try {

            return objectMapper.readTree(dbData);
        } catch (IOException e) {
            LOG.error(e);
            return null;
        }
    }

}