package com.worth.ifs.assessment.resource;

import com.worth.ifs.workflow.resource.OutcomeType;

public enum AssessmentOutcomes implements OutcomeType {
    ACCEPT("accept"),
    REJECT("reject"),
    RECOMMEND("recommend"),
    SUBMIT("submit");

    String event;

    AssessmentOutcomes(String event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return event;
    }
}
