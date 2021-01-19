package org.innovateuk.ifs.sil.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;

import static org.innovateuk.ifs.sil.common.json.Constants.DATE_FORMAT;

public class LocalDateSerializer extends JsonSerializer<LocalDate> {
    @Override
    public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider serializers)
            throws IOException {
        generator.writeString(value.format(DATE_FORMAT));
    }
}
