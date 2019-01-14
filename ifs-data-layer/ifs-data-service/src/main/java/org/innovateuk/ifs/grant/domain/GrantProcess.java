package org.innovateuk.ifs.grant.domain;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private long applicationId;

    private ZonedDateTime sentRequested;
    private ZonedDateTime sentSucceeded;
    private ZonedDateTime lastProcessed;
    private boolean pending;
    private String message;

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public ZonedDateTime getSentRequested() {
        return sentRequested;
    }

    public void setSentRequested(ZonedDateTime sentRequested) {
        this.sentRequested = sentRequested;
    }

    public ZonedDateTime getSentSucceeded() {
        return sentSucceeded;
    }

    public void setSentSucceeded(ZonedDateTime sentSucceeded) {
        this.sentSucceeded = sentSucceeded;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
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

    public void setMessage(String message) {
        this.message = message;
    }

    public ZonedDateTime getLastProcessed() {
        return lastProcessed;
    }

    public void setLastProcessed(ZonedDateTime lastProcessed) {
        this.lastProcessed = lastProcessed;
    }
}
