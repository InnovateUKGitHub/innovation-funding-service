package org.innovateuk.ifs.grant.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
public class GrantStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private long applicationId;

    private ZonedDateTime sentRequested;
    private ZonedDateTime sentSucceeded;

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
}
