package com.worth.ifs.workflow.domain;

import com.worth.ifs.workflow.resource.State;

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

    @Enumerated(EnumType.STRING)
    private State state;

    ActivityState() {
    }

    public ActivityState(ActivityType activityType, State state) {
        this.activityType = activityType;
        this.state = state;
    }

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
