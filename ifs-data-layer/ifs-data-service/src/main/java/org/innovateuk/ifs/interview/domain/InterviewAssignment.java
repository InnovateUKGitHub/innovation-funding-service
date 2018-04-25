package org.innovateuk.ifs.interview.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

import static org.innovateuk.ifs.user.resource.Role.INTERVIEW_LEAD_APPLICANT;

/**
 * An invitation for an application to participate on an interview panel.
 */
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

    @Column(name="activity_state_id")
    private InterviewAssignmentState activityState;

    public InterviewAssignment() {
    }

    public InterviewAssignment(Application application, ProcessRole participant) {
        if (application == null) throw new NullPointerException("target cannot be null");
        if (participant == null) throw new NullPointerException("participant cannot be null");

        if (participant.getRole() != INTERVIEW_LEAD_APPLICANT)
            throw new IllegalArgumentException("participant must be INTERVIEW_LEAD_APPLICANT");
        if (!participant.getApplicationId().equals(application.getId()))
            throw new IllegalArgumentException("participant application must match the application");
        if (!participant.getOrganisationId().equals(application.getLeadOrganisationId()))
            throw new IllegalArgumentException("participant organisation must match the application's lead organisation");
        if (!participant.getUser().getId().equals(application.getLeadApplicant().getId()))
            throw new IllegalArgumentException("participant user must match the application's lead user");

        this.target = application;
        this.participant = participant;
        setActivityState(InterviewAssignmentState.CREATED);
    }

    @Override
    public void setActivityState(InterviewAssignmentState status) {
        this.activityState = status;
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
    public InterviewAssignmentState getProcessState() {
        return activityState;
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
    public InterviewAssignmentState getActivityState() {
        return activityState;
    }

    public InterviewAssignmentMessageOutcome getMessage() {
        return message;
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
                .append(activityState, that.activityState)
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
                .append(activityState)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("participant", participant)
                .append("target", target)
                .append("message", message)
                .append("response", response)
                .append("activityState", activityState)
                .toString();
    }
}