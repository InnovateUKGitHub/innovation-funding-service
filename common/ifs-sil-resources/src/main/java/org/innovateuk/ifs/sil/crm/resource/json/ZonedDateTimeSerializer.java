package org.innovateuk.ifs.sil.crm.resource.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.sil.common.json.Constants.LOANS_DATETIME_FORMAT;

public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {
    @Override
    public void serialize(ZonedDateTime value, JsonGenerator generator, SerializerProvider serializers)
            throws IOException {
        generator.writeString(value.toInstant().toString());
    }
}
