package com.worth.ifs.alert.domain;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
    private LocalDateTime validFromDate;

    @NotNull
    @DateTimeFormat
    private LocalDateTime validToDate;

    public Alert() {
    }

    public Alert(final String message, final AlertType type, final LocalDateTime validFromDate, final LocalDateTime validToDate) {
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

    public LocalDateTime getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(LocalDateTime validFromDate) {
        this.validFromDate = validFromDate;
    }

    public LocalDateTime getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(LocalDateTime validToDate) {
        this.validToDate = validToDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alert alert = (Alert) o;

        if (id != null ? !id.equals(alert.id) : alert.id != null) return false;
        if (message != null ? !message.equals(alert.message) : alert.message != null) return false;
        if (type != alert.type) return false;
        if (validFromDate != null ? !validFromDate.equals(alert.validFromDate) : alert.validFromDate != null)
            return false;
        return validToDate != null ? validToDate.equals(alert.validToDate) : alert.validToDate == null;

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
