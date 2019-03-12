package org.innovateuk.ifs.sil.grant.resource.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.sil.grant.resource.json.GrantConstants.DATE_FORMAT;
import static org.innovateuk.ifs.sil.grant.resource.json.GrantConstants.GMT;

public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        return LocalDate.parse(node.asText(), DATE_FORMAT);
    }
}
