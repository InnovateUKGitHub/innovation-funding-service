package org.innovateuk.ifs.commons.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;
import java.io.IOException;

/**
 * @deprecated very few times has storing json in mysql tables been a good idea
 *
 * Catching the exception and returning null has never been a good idea
 *
 */
@Slf4j
@Deprecated
public class JpaJsonConverter implements AttributeConverter<JsonNode, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(JsonNode meta) {
        try {
            return objectMapper.writeValueAsString(meta);
        } catch (IOException e) {
            log.error("writeValueAsString", e);
            return null;
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readTree(dbData);
        } catch (IOException e) {
            log.error("readTree", e);
            return null;
        }
    }

}
