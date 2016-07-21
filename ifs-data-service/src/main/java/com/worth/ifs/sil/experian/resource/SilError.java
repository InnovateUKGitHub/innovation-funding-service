package com.worth.ifs.sil.experian.resource;

import com.fasterxml.jackson.annotation.JsonRootName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonRootName("")
public class SilError {
    private String code;
    private String message;
    private String fields;

    public SilError() {}

    public SilError(String code, String message, String fields) {
        this.code = code;
        this.message = message;
        this.fields = fields;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SilError error = (SilError) o;

        return new EqualsBuilder()
                .append(code, error.code)
                .append(message, error.message)
                .append(fields, error.fields)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(code)
                .append(message)
                .append(fields)
                .toHashCode();
    }
}
