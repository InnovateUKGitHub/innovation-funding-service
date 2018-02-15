package org.innovateuk.ifs.interview.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;


// TODO Who is the participant? Lead assessor -- probably as they submit responses

@Entity
public class InterviewAssignment extends Process<ProcessRole, Application, InterviewAssignmentState> {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private ProcessRole participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application target;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "process", fetch = FetchType.LAZY)
    private InterviewAssignmentMessageOutcome message;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "process", fetch = FetchType.LAZY)
    private InterviewAssignmentResponseOutcome response;

    public InterviewAssignment() {
    }

    public InterviewAssignment(Application target, ProcessRole participant) {
        this.target = target;
        this.participant = participant;
    }

    @Override
    public void setParticipant(ProcessRole participant) {
        this.participant = participant;
    }

    @Override
    public ProcessRole getParticipant() {
        return participant;
    }

    @Override
    public void setTarget(Application target) {
        this.target  = target;
    }

    @Override
    public Application getTarget() {
        return target;
    }

    @Override
    public InterviewAssignmentState getActivityState() {
        return InterviewAssignmentState.fromState(activityState.getState());
    }

    public void setResponse(InterviewAssignmentResponseOutcome response) {
        this.response = response;
    }

    public InterviewAssignmentResponseOutcome getResponse() {
        return response;
    }

    public void setMessage(InterviewAssignmentMessageOutcome message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAssignment that = (InterviewAssignment) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(participant, that.participant)
                .append(target, that.target)
                .append(message, that.message)
                .append(response, that.response)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(participant)
                .append(target)
                .append(message)
                .append(response)
                .toHashCode();
    }

    public InterviewAssignmentMessageOutcome getMessage() {
        return message;
    }
}