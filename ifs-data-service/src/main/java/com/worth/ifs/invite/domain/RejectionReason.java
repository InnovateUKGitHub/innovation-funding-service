package com.worth.ifs.invite.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * A reason for rejecting an invitation to be a {@link CompetitionParticipant}.
 */
@Entity
public class RejectionReason {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String reason;

    @NotNull
    private Boolean active;

    @NotNull
    private Integer priority;

    public RejectionReason() {
    }

    public RejectionReason(Long id, String reason, Boolean active, Integer priority) {
        this.id = id;
        this.reason = reason;
        this.active = active;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RejectionReason that = (RejectionReason) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(reason, that.reason)
                .append(active, that.active)
                .append(priority, that.priority)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(reason)
                .append(active)
                .append(priority)
                .toHashCode();
    }
}