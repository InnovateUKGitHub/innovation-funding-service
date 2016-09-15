package com.worth.ifs.project.constant;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Enum used to represent states for project activities.  Used in resource objects for showing team status.
 */
public enum  ProjectActivityStates {
    NOT_REQUIRED,
    NOT_STARTED,
    ACTION_REQUIRED,
    PENDING,
    COMPLETE;

    @Override
    public String toString() {
        return new ToStringBuilder(this.name())
                .toString();
    }
}
