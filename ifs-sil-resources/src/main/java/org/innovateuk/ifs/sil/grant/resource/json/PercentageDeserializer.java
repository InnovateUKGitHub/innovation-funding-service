package org.innovateuk.ifs.sil.grant.resource.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.sil.grant.resource.json.GrantConstants.DATE_FORMAT;
import static org.innovateuk.ifs.sil.grant.resource.json.GrantConstants.GMT;

public class PercentageDeserializer extends JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        return BigDecimal.valueOf(Double.parseDouble(node.asText().replaceAll("%", "")));
    }
}
