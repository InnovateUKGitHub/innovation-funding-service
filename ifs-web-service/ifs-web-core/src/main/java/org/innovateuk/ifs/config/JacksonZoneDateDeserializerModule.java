package org.innovateuk.ifs.config;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import org.innovateuk.ifs.util.TimeZoneUtil;

import java.io.IOException;
import java.time.ZonedDateTime;

public class JacksonZoneDateDeserializerModule extends SimpleModule {

    public JacksonZoneDateDeserializerModule() {
        addDeserializer(ZonedDateTime.class, new JsonDeserializer<ZonedDateTime>() {
            @Override
            public ZonedDateTime deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
                ZonedDateTime standardResult = InstantDeserializer.ZONED_DATE_TIME.deserialize(p, context);
                return standardResult.withZoneSameInstant(TimeZoneUtil.UK_TIME_ZONE);
            }
        });
    }
}