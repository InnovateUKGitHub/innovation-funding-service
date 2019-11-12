package org.innovateuk.ifs.config.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.cache.support.NullValue;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

public class ServiceResultWrappingSerializer extends GenericJackson2JsonRedisSerializer {

    public ServiceResultWrappingSerializer() {
        super(newObjectMapper());
    }

    private static ObjectMapper newObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new SimpleModule().addSerializer(new NullValueSerializer(null)));
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return objectMapper;
    }

    @Override
    public byte[] serialize(@Nullable Object source) throws SerializationException {
        ServiceResult<?> sourceResult = (ServiceResult) source;
        return super.serialize(sourceResult.getSuccess());
    }

    @Override
    public <T> T deserialize(@Nullable byte[] source, Class<T> type) throws SerializationException {
        return (T) serviceSuccess(super.deserialize(source, type));
    }

    private static class NullValueSerializer extends StdSerializer<NullValue> {

        private static final long serialVersionUID = 1999052150548658808L;
        private final String classIdentifier;

        /**
         * @param classIdentifier can be {@literal null} and will be defaulted to {@code @class}.
         */
        NullValueSerializer(@Nullable String classIdentifier) {

            super(NullValue.class);
            this.classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
        }

        /*
         * (non-Javadoc)
         * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object, com.fasterxml.jackson.core.JsonGenerator, com.fasterxml.jackson.databind.SerializerProvider)
         */
        @Override
        public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {

            jgen.writeStartObject();
            jgen.writeStringField(classIdentifier, NullValue.class.getName());
            jgen.writeEndObject();
        }
    }
}
