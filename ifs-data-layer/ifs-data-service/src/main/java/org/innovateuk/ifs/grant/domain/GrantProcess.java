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
        this.pending = true;
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
        this.pending = false;
        this.sentRequested = now;
        return this;
    }

    public GrantProcess sendSucceeded(ZonedDateTime now) {
        this.pending = true;
        this.sentSucceeded = now;
        this.message = null;
        return this;
    }

    public GrantProcess sendFailed(ZonedDateTime now, String message) {
        this.lastProcessed = now;
        this.message = message;
        return this;
    }
}