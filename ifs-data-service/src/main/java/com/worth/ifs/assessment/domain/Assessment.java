package com.worth.ifs.assessment.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.workflow.domain.OutcomeType;
import com.worth.ifs.workflow.domain.Process;
import com.worth.ifs.workflow.domain.ProcessOutcome;

import javax.persistence.Entity;

@Entity
public class Assessment extends Process {

    public Assessment() {
        super();
    }

    public Assessment(ProcessRole processRole) {
        super(processRole);
    }

    public Boolean hasAssessmentStarted() {
        return getProcessStatus().equals(AssessmentStates.ASSESSED.getState());
    }

    @JsonIgnore
    public Boolean isSubmitted() {
        return getProcessStatus().equals(AssessmentStates.SUBMITTED.getState());
    }

    public ProcessOutcome getLastOutcome() {
        if(this.processOutcomes != null) {
            return this.processOutcomes.stream().findFirst().orElse(null);
        }
        return null;
    }

    public ProcessOutcome getLastOutcome(OutcomeType outcomeType) {
        if(this.processOutcomes != null) {
            return processOutcomes.stream().filter(po -> outcomeType.getType().equals(po.getOutcomeType())).findFirst().orElse(null);
        }
        return null;
    }


}
