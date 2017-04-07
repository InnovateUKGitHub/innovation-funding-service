package org.innovateuk.ifs.token.resource;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.ZonedDateTime;

public class TokenResource {
    private Long id;
    private String className;
    private Long classPk;
    private String hash;
    private ZonedDateTime created;
    private ZonedDateTime updated;
    private JsonNode extraInfo;
    private TokenType type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getClassPk() {
        return classPk;
    }

    public void setClassPk(Long classPk) {
        this.classPk = classPk;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public JsonNode getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(JsonNode extraInfo) {
        this.extraInfo = extraInfo;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(ZonedDateTime updated) {
        this.updated = updated;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }
}
