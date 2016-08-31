package com.worth.ifs.invite.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A reason for rejecting an invitation to be a {@link CompetitionParticipant}.
 */
@Entity
public class RejectionReason implements Comparable<RejectionReason> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String reason;

    @NotNull
    private boolean active;

    @NotNull
    private int priority;

    public RejectionReason() {
    }

    public RejectionReason(String reason, boolean active, int priority) {
        if (reason == null) throw new NullPointerException("reason cannot be null");
        if (reason.isEmpty()) throw new IllegalArgumentException("reason cannot be empty");
        this.reason = reason;
        this.active = active;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public String getReason() {
        return reason;
    }

    public boolean isActive() {
        return active;
    }

    public int getPriority() {
        return priority;
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

    @Override
    public int compareTo(RejectionReason o) {
        return Integer.compare(this.priority, o.priority);
    }
}