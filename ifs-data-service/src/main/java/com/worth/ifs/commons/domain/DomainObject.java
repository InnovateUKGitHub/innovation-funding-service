package com.worth.ifs.commons.domain;

import com.worth.ifs.user.resource.UserResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by skistapur on 18/07/2016.
 *
 * This is used to handle the common functionalities like setting the created time, updated time, updated by and
 * also can be used for future audit handling.
 */
@MappedSuperclass
public abstract class DomainObject implements Serializable {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_timestamp", nullable = true)
    protected Date updateTimestamp;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_timestamp", nullable = true)
    protected Date createdTimestamp;

    @Column(name = "last_update_user", nullable = true)
    protected String lastUpdateUser;

    protected final static String NOT_KNOWN = "not known";

    @PrePersist
    protected void onCreate() {
        createdTimestamp = new Date();
        updateTimestamp = createdTimestamp;
        updateUser();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTimestamp = new Date();
        updateUser();
    }

    protected void updateUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            lastUpdateUser = ((UserResource) authentication.getDetails()).getEmail();
        } else {
            lastUpdateUser = NOT_KNOWN;
        }
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

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }
}
