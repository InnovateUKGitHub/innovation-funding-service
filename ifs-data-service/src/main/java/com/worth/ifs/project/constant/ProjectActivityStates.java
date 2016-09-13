package com.worth.ifs.project.constant;

import org.apache.commons.lang3.builder.ToStringBuilder;

public enum  ProjectActivityStates {
    NOT_REQUIRED,
    NOT_STARTED,
    ACTION_REQUIRED,
    PENDING,
    COMPLETE;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .toString();
    }
}
