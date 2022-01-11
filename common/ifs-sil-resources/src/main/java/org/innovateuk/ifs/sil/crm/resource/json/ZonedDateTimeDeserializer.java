package org.innovateuk.ifs.sil.crm.resource.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.sil.common.json.Constants.UTC;
import static org.innovateuk.ifs.sil.common.json.Constants.LOANS_DATETIME_FORMAT;

public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        return ZonedDateTime.parse(node.asText(), LOANS_DATETIME_FORMAT.withZone(UTC));
    }
}
