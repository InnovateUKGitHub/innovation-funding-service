package com.worth.ifs.commons.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by skistapur on 18/07/2016.
 */
public abstract class DomainObject implements Serializable {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_timestamp", nullable = true)
    protected Date updateTimestamp;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_timestamp", nullable = true)
    protected Date createdTimestamp;

    @PrePersist
    protected void onCreate() {
        createdTimestamp = new Date();
        updateTimestamp = createdTimestamp;
    }

    @PreUpdate
    protected void onUpdate() {
        updateTimestamp = new Date();
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
