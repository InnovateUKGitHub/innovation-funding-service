package com.worth.ifs.assessment.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.workflow.domain.Process;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.OutcomeType;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static com.worth.ifs.assessment.resource.AssessmentStates.ASSESSED;
import static com.worth.ifs.assessment.resource.AssessmentStates.SUBMITTED;

@Entity
public class Assessment extends Process<ProcessRole, Application, AssessmentStates> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id", referencedColumnName = "id")
    private ProcessRole participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private Application target;

    public Assessment() {
        super();
    }

    public Assessment(ProcessRole processRole) {
        this.participant = processRole;
    }

    public Assessment(Application application, ProcessRole processRole) {
        this(processRole);
        this.target = application;
    }

    public Boolean isStarted() {
        if(getActivityState()!=null) {
            return isInState(ASSESSED);
        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean isSubmitted() {
        if(getActivityState()!=null) {
            return isInState(SUBMITTED);
        } else {
            return Boolean.FALSE;
        }
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

    @Override
    public ProcessRole getParticipant() {
        return participant;
    }

    @Override
    public void setParticipant(ProcessRole participant) {
        this.participant = participant;
    }

    @Override
    public Application getTarget() {
        return target;
    }

    @Override
    public void setTarget(Application target) {
        this.target = target;
    }

    public AssessmentStates getActivityState() {
        return AssessmentStates.fromState(activityState.getState());
    }
}
