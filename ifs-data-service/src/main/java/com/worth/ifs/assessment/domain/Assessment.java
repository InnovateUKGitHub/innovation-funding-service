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
import java.util.Optional;

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

    public Assessment(Application application, ProcessRole processRole) {
        this.participant = processRole;
        this.target = application;
    }

    public Optional<ProcessOutcome> getLastOutcome() {
        return Optional.ofNullable(this.processOutcomes).flatMap(outcomes -> outcomes.stream().reduce((outcome1, outcome2) -> outcome2));
    }

    public Optional<ProcessOutcome> getLastOutcome(OutcomeType outcomeType) {
        return Optional.ofNullable(this.processOutcomes).flatMap(outcomes -> outcomes.stream().filter(outcome -> outcomeType.getType().equals(outcome.getOutcomeType())).reduce((outcome1, outcome2) -> outcome2));
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
