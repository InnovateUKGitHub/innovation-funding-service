package org.innovateuk.ifs.api.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AuditMessageTest {

    private static final AuditMessage auditMessage =
        AuditMessageBuilder.builder(UUID.randomUUID(), AuditType.MISC)
            .payload("foo")
            .userId("user")
            .spanTrace(UUID.randomUUID().toString(), UUID.randomUUID().toString())
            .build();
    private static final AuditMessage auditMessagePartial =
        AuditMessageBuilder.builder(AuditType.MISC)
            .build();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void equalsTest() {
        EqualsVerifier.simple().forClass(AuditMessage.class).verify();
    }

    @Test
    public void serializeTest() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(auditMessage);
        assertThat(json, notNullValue());
        AuditMessage jsonAuditMessage = objectMapper.readValue(json, AuditMessage.class);
        assertThat(auditMessage, equalTo(jsonAuditMessage));
    }

    @Test
    public void serializeTestPartialBuilder() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(auditMessagePartial);
        assertThat(json, notNullValue());
        AuditMessage jsonAuditMessage = objectMapper.readValue(json, AuditMessage.class);
        assertThat(auditMessagePartial, equalTo(jsonAuditMessage));
    }

    @Test
    public void builderTest() {
        assertThat(auditMessage.getUuid(), notNullValue());
        assertThat(auditMessage.toString(), containsString("foo"));
        assertThat(auditMessage.getAuditType(), equalTo(AuditType.MISC));
        assertThat(auditMessage.getPayload(), equalTo("foo"));
        assertThat(auditMessage.getUserId(), equalTo("user"));
        assertThat(auditMessage.getSpanId(), notNullValue());
        assertThat(auditMessage.getCreated(), notNullValue());
        assertThat(auditMessage.getTraceId(), notNullValue());
    }

    @Test
    public void builderTestPartialBuilder() {
        assertThat(auditMessagePartial.getUuid(), notNullValue());
        assertThat(auditMessagePartial.getAuditType(), equalTo(AuditType.MISC));
        assertThat(auditMessagePartial.getCreated(), notNullValue());
    }

}