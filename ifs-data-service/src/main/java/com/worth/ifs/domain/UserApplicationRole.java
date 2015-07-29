package com.worth.ifs.domain;

import javax.persistence.*;

/**
 * Created by wouter on 29/07/15.
 */
@Entity
public class UserApplicationRole {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long userId;
    private long applicationId;
    private long roleId;

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getRoleId() {
        return roleId;
    }

}
