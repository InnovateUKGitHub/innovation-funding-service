package com.worth.ifs.workflow.domain;

import javax.persistence.*;

/**
 * Represents a possible state that a Process can be in, given the type of Process
 */
@Entity
public class ActivityState {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    private State state;

    public Long getId() {
        return id;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public State getState() {
        return state;
    }
}
