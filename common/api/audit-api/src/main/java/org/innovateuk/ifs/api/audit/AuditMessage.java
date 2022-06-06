package org.innovateuk.ifs.api.audit;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public final class AuditMessage {

    private UUID uuid;
    private String auditType;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime created;
    private String userId;
    private String traceId;
    private String spanId;
    private String payload;

    public AuditMessage() {
    }

    public AuditMessage(AuditMessageBuilder auditMessageBuilder) {
        this.uuid = auditMessageBuilder.getUuid();
        this.auditType = auditMessageBuilder.getAuditType();
        this.created = auditMessageBuilder.getCreated();
        this.userId = auditMessageBuilder.getUserId();
        this.traceId = auditMessageBuilder.getTraceId();
        this.spanId = auditMessageBuilder.getSpanId();
        this.payload = auditMessageBuilder.getPayload();
    }

}
