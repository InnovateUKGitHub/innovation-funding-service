package com.worth.ifs.token.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.token.JpaConverterJson;
import com.worth.ifs.token.resource.TokenType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TokenType type;
    private String className;
    private Long classPk;
    @Column(unique=true)
    private String hash;

    @NotNull
    @CreatedDate
    @DateTimeFormat
    @Column(updatable = false)
    private LocalDateTime created;

    @LastModifiedDate
    @DateTimeFormat
    private LocalDateTime updated;

    @Convert(converter = JpaConverterJson.class)
    @Column( length = 5000 )
    JsonNode extraInfo;

    public Token(TokenType type, String className, Long classPk, String hash, LocalDateTime created, JsonNode extraInfo) {
        this.type = type;
        this.className = className;
        this.classPk = classPk;
        this.hash = hash;
        this.extraInfo = extraInfo;
        this.created = created;
    }

    public Token() {
    	// no-arg constructor
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public JsonNode getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(JsonNode extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Token token = (Token) o;

        return new EqualsBuilder()
                .append(id, token.id)
                .append(type, token.type)
                .append(className, token.className)
                .append(classPk, token.classPk)
                .append(hash, token.hash)
                .append(created, token.created)
                .append(updated, token.updated)
                .append(extraInfo, token.extraInfo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(type)
                .append(className)
                .append(classPk)
                .append(hash)
                .append(created)
                .append(updated)
                .append(extraInfo)
                .toHashCode();
    }
}
