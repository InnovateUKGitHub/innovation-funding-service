package com.worth.ifs.workflow.domain;

/**
 * Created by nunoalexandre on 15/09/15.
 */
public enum ProcessEvent {

    ASSESSMENT_INVITATION("assessment_invitation"),
    ANOTHER_ONE("n/a");


    private final String type;

    ProcessEvent(String value) {
        this.type = value;
    }
}
