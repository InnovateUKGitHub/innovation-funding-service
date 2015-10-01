package com.worth.ifs.assessment.domain;

import com.worth.ifs.workflow.domain.Events;

public enum AssessmentEvents implements Events {
    ACCEPT("accept"),
    REJECT("reject"),
    RECOMMEND("recommend"),
    SUBMIT("submit");

    String event;

    AssessmentEvents(String event) {
        this.event = event;
    }


    @Override
    public String getEvent() {
        return event;
    }
}
