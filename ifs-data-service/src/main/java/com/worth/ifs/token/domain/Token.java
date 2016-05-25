package com.worth.ifs.token.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.worth.ifs.token.JpaConverterJson;
import com.worth.ifs.token.resource.TokenType;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Enumerated(EnumType.STRING)
    TokenType type;
    String className;
    Long classPk;
    @Column(unique=true)
    String hash;

    @Convert(converter = JpaConverterJson.class)
    @Column( length = 5000 )
    JsonNode extraInfo;

    public Token(TokenType type, String className, Long classPk, String hash, JsonNode extraInfo) {
        this.type = type;
        this.className = className;
        this.classPk = classPk;
        this.hash = hash;
        this.extraInfo = extraInfo;
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

    public JsonNode getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(JsonNode extraInfo) {
        this.extraInfo = extraInfo;
    }
}
