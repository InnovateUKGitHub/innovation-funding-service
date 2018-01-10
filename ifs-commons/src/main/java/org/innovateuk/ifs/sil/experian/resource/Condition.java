package org.innovateuk.ifs.sil.experian.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Condition {
    private String severity;
    private Integer code;
    private String description;

    public Condition() {}

    public Condition(String severity, Integer code, String description) {
        this.severity = severity;
        this.code = code;
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Condition condition = (Condition) o;

        return new EqualsBuilder()
                .append(severity, condition.severity)
                .append(code, condition.code)
                .append(description, condition.description)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(severity)
                .append(code)
                .append(description)
                .toHashCode();
    }
}
