package org.innovateuk.ifs.sil.crm.resource.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.innovateuk.ifs.user.resource.EDIStatus;

import java.io.IOException;

public class EDIStatusSerializer extends JsonSerializer<EDIStatus> {
    @Override
    public void serialize(EDIStatus value, JsonGenerator generator, SerializerProvider serializers)
            throws IOException {
        generator.writeString(value.getDisplayName());
    }
}
