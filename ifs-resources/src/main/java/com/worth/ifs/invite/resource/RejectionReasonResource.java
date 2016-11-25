package com.worth.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A reason for rejecting an invitation to be a CompetitionParticipant.
 */
public class RejectionReasonResource {

    private Long id;
    private String reason;
    private Boolean active;
    private Integer priority;

    public RejectionReasonResource() {
    }

    public RejectionReasonResource(String reason, Boolean active, Integer priority) {
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

        RejectionReasonResource that = (RejectionReasonResource) o;

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