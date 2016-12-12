package org.innovateuk.ifs.assessment.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.Process;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;
import org.innovateuk.ifs.workflow.resource.OutcomeType;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
public class Assessment extends Process<ProcessRole, Application, AssessmentStates> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id", referencedColumnName = "id")
    private ProcessRole participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private Application target;

    @OneToMany(mappedBy = "assessment")
    private List<AssessorFormInputResponse> responses;

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

    public List<AssessorFormInputResponse> getResponses() {
        return responses;
    }

    public void setResponses(List<AssessorFormInputResponse> responses) {
        this.responses = responses;
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
