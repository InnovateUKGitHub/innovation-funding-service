package org.innovateuk.ifs.grant.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.ZonedDateTime;

/**
 * Represents a request to send information about a live project to the external live project system.
 */
@Entity
public class GrantProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private final long applicationId;

    private ZonedDateTime sentRequested;
    private ZonedDateTime sentSucceeded;
    private ZonedDateTime lastProcessed;
    private boolean pending;
    private String message;

    GrantProcess() {
        this.applicationId = -1;
    }

    public GrantProcess(long applicationId) {
        this.applicationId = applicationId;
        this.pending = false;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public ZonedDateTime getSentRequested() {
        return sentRequested;
    }

    public ZonedDateTime getSentSucceeded() {
        return sentSucceeded;
    }

    public boolean isPending() {
        return pending;
    }

    /**
     * If pending is false then the grant should not be sent to downstream systems. This can be false if the project
     * has been filtered out - see GrantProcessApplicationFilter. A grant process entry is still created since
     * a subsequent configuration change and process execution could later take place that could switch this flag and
     * trigger processing.
     *
     * @return whether grant should be sent
     */
    public String getMessage() {
        return message;
    }

    public ZonedDateTime getLastProcessed() {
        return lastProcessed;
    }


    public GrantProcess requestSend(ZonedDateTime now) {
        this.pending = true;
        this.sentRequested = now;
        return this;
    }

    public GrantProcess sendSucceeded(ZonedDateTime now) {
        this.pending = false;
        this.sentSucceeded = now;
        this.message = null;
        return this;
    }

    public GrantProcess sendFailed(ZonedDateTime now, String message) {
        this.lastProcessed = now;
        this.message = message;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GrantProcess that = (GrantProcess) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(pending, that.pending)
                .append(id, that.id)
                .append(sentRequested, that.sentRequested)
                .append(sentSucceeded, that.sentSucceeded)
                .append(lastProcessed, that.lastProcessed)
                .append(message, that.message)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(applicationId)
                .append(sentRequested)
                .append(sentSucceeded)
                .append(lastProcessed)
                .append(pending)
                .append(message)
                .toHashCode();
    }
}