package com.worth.ifs.alert.resource;

import com.worth.ifs.alert.domain.AlertType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Represents an alert which is displayed to users.
 */
public class AlertResource {

    private Long id;
    private String message;
    private AlertType type;
    @DateTimeFormat
    private LocalDateTime validFromDate;
    @DateTimeFormat
    private LocalDateTime validToDate;

    public AlertResource() {
    }

    public AlertResource(final Long id, final String message, final AlertType type, final LocalDateTime validFromDate, final LocalDateTime validToDate) {
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

    public LocalDateTime getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(final LocalDateTime validFromDate) {
        this.validFromDate = validFromDate;
    }

    public LocalDateTime getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(final LocalDateTime validToDate) {
        this.validToDate = validToDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlertResource that = (AlertResource) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (type != that.type) return false;
        if (validFromDate != null ? !validFromDate.equals(that.validFromDate) : that.validFromDate != null)
            return false;
        return validToDate != null ? validToDate.equals(that.validToDate) : that.validToDate == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (validFromDate != null ? validFromDate.hashCode() : 0);
        result = 31 * result + (validToDate != null ? validToDate.hashCode() : 0);
        return result;
    }
}
