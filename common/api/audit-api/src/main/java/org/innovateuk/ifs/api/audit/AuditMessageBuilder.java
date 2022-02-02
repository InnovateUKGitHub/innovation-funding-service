package org.innovateuk.ifs.api.audit;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AuditMessageBuilder {

    private final UUID uuid;
    private final AuditType auditType;
    private final LocalDateTime created;
    private String userId;
    private String traceId;
    private String spanId;
    private String payload;

    private AuditMessageBuilder(UUID uuid, AuditType auditType, LocalDateTime created) {
        this.uuid = uuid;
        this.auditType = auditType;
        this.created = created;
    }

    public static AuditMessageBuilder builder(AuditType auditType) {
        AuditMessageBuilder auditMessageBuilder = new AuditMessageBuilder(UUID.randomUUID(), auditType, LocalDateTime.now());
        return auditMessageBuilder;
    }

    public static AuditMessageBuilder builder(UUID uuid, AuditType auditType) {
        AuditMessageBuilder auditMessageBuilder = new AuditMessageBuilder(uuid, auditType, LocalDateTime.now());
        return auditMessageBuilder;
    }

    public AuditMessageBuilder payload(String payload) {
        this.payload = payload;
        return this;
    }

    public AuditMessageBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public AuditMessageBuilder spanTrace(String spanId, String traceId) {
        this.spanId = spanId;
        this.traceId = traceId;
        return this;
    }

    public AuditMessage build() {
        return new AuditMessage(this);
    }
}
