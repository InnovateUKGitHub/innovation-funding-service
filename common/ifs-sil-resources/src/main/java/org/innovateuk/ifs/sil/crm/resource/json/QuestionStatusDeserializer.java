package org.innovateuk.ifs.sil.crm.resource.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.innovateuk.ifs.application.resource.QuestionStatus;

import java.io.IOException;

public class QuestionStatusDeserializer extends JsonDeserializer<QuestionStatus> {


    @Override
    public QuestionStatus deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if(QuestionStatus.COMPLETE.getDisplayName().equals(node.asText()))
            return QuestionStatus.COMPLETE;
        else if(QuestionStatus.INCOMPLETE.getDisplayName().equals(node.asText()))
            return QuestionStatus.INCOMPLETE;
        else
            throw new IllegalArgumentException("Invalid question status enum: " + node.asText());
    }
}
