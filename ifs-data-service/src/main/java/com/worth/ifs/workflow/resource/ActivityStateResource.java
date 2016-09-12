package com.worth.ifs.workflow.resource;

import com.worth.ifs.workflow.domain.ActivityType;
import com.worth.ifs.workflow.domain.State;

/**
 * Represents an ActivityState for use outside of the entity model
 */
public class ActivityStateResource {

    private ActivityType activityType;
    private State state;

    public ActivityStateResource() {
    }

    public ActivityStateResource(ActivityType activityType, State state) {
        this.activityType = activityType;
        this.state = state;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
