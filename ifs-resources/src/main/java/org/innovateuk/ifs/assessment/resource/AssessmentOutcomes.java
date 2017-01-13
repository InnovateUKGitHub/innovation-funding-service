package org.innovateuk.ifs.assessment.resource;

import org.innovateuk.ifs.workflow.resource.OutcomeType;

public enum AssessmentOutcomes implements OutcomeType {
    NOTIFY("notify"),
    ACCEPT("accept"),
    REJECT("reject"),
    WITHDRAW("withdraw"),
    FEEDBACK("feedback"),
    FUNDING_DECISION("funding-decision"),
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
