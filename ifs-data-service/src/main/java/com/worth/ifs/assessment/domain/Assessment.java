package com.worth.ifs.assessment.domain;

import com.worth.ifs.workflow.domain.Process;

import javax.persistence.Entity;

@Entity
public class Assessment extends Process {

    public Assessment() {
        super();
    }

    public Boolean hasAssessmentStarted() {
        return getProcessStatus().equals(AssessmentStates.ASSESSED);
    }
}
