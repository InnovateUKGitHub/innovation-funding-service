package org.innovateuk.ifs.sil.crm.resource.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.user.resource.EDIStatus;

import java.io.IOException;

public class EDIStatusDeserializer extends JsonDeserializer<EDIStatus> {
    @Override
    public EDIStatus deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if(EDIStatus.COMPLETE.getDisplayName().equals(node.asText()))
            return EDIStatus.COMPLETE;
        else if(EDIStatus.INPROGRESS.getDisplayName().equals(node.asText()))
            return EDIStatus.INPROGRESS;
        else
            throw new IllegalArgumentException("Invalid EDI status enum: " + node.asText());
    }
}
