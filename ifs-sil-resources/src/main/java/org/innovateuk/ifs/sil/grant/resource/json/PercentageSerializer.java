package org.innovateuk.ifs.sil.grant.resource.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class PercentageSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal value, JsonGenerator generator, SerializerProvider serializers)
            throws IOException {
        generator.writeString(value.stripTrailingZeros().toString() + "%");
    }
}
