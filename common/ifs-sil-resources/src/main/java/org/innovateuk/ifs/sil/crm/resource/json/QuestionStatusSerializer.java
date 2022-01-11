package org.innovateuk.ifs.sil.crm.resource.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.innovateuk.ifs.application.resource.QuestionStatus;

import java.io.IOException;
import java.time.ZonedDateTime;

public class QuestionStatusSerializer extends JsonSerializer<QuestionStatus> {
    @Override
    public void serialize(QuestionStatus value, JsonGenerator generator, SerializerProvider serializers)
            throws IOException {
        generator.writeString(value.getDisplayName());
    }
}
