package org.innovateuk.ifs.alert.domain;

import org.innovateuk.ifs.alert.resource.AlertType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

import static javax.persistence.EnumType.STRING;

/**
 * Represents an alert which is displayed to users.
 */
@Entity
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String message;

    @NotNull
    @Enumerated(STRING)
    private AlertType type;

    @NotNull
    @DateTimeFormat
    private ZonedDateTime validFromDate;

    @NotNull
    @DateTimeFormat
    private ZonedDateTime validToDate;

    public Alert() {
    }

    public Alert(final String message, final AlertType type, final ZonedDateTime validFromDate, final ZonedDateTime validToDate) {
        this.message = message;
        this.type = type;
        this.validFromDate = validFromDate;
        this.validToDate = validToDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public ZonedDateTime getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(ZonedDateTime validFromDate) {
        this.validFromDate = validFromDate;
    }

    public ZonedDateTime getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(ZonedDateTime validToDate) {
        this.validToDate = validToDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Alert alert = (Alert) o;

        return new EqualsBuilder()
                .append(id, alert.id)
                .append(message, alert.message)
                .append(type, alert.type)
                .append(validFromDate, alert.validFromDate)
                .append(validToDate, alert.validToDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(message)
                .append(type)
                .append(validFromDate)
                .append(validToDate)
                .toHashCode();
    }
}
