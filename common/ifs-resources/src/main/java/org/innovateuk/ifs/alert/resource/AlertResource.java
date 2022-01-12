package org.innovateuk.ifs.alert.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;

/**
 * Represents an alert which is displayed to users.
 */
public class AlertResource {

    private Long id;
    private String message;
    private AlertType type;
    private ZonedDateTime validFromDate;
    private ZonedDateTime validToDate;

    public AlertResource() {
    }

    public AlertResource(final Long id, final String message, final AlertType type, final ZonedDateTime validFromDate, final ZonedDateTime validToDate) {
        this.id = id;
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

    public void setMessage(final String message) {
        this.message = message;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(final AlertType type) {
        this.type = type;
    }

    public ZonedDateTime getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(final ZonedDateTime validFromDate) {
        this.validFromDate = validFromDate;
    }

    public ZonedDateTime getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(final ZonedDateTime validToDate) {
        this.validToDate = validToDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AlertResource that = (AlertResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(message, that.message)
                .append(type, that.type)
                .append(validFromDate, that.validFromDate)
                .append(validToDate, that.validToDate)
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
