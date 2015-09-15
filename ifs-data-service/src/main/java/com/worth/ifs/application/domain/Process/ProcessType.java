package com.worth.ifs.application.domain.Process;

/**
 * Created by nunoalexandre on 15/09/15.
 */
public enum ProcessType {

    ASSESSMENT_INVITATION("assessment_invitation"),
    ANOTHER_ONE("n/a");


    private final String type;

    ProcessType(String value) {
        this.type = value;
    }
}
