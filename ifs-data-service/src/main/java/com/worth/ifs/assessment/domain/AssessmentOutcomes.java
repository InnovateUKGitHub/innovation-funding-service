package com.worth.ifs.assessment.domain;

import com.worth.ifs.workflow.domain.OutcomeType;

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
