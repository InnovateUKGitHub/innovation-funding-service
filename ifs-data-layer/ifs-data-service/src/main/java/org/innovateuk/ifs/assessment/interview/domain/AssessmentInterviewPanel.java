package org.innovateuk.ifs.assessment.interview.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

@Entity
public class AssessmentInterviewPanel extends Process<ProcessRole, Application, AssessmentInterviewPanelState> {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private ProcessRole participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application target;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "process", fetch = FetchType.LAZY)
    private AssessmentInterviewPanelMessageOutcome message;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "process", fetch = FetchType.LAZY)
    private AssessmentInterviewPanelResponseOutcome response;

    public AssessmentInterviewPanel() {
    }

    public AssessmentInterviewPanel(Application application, ProcessRole participant, ActivityState createdState) {
        if (application == null) throw new NullPointerException("target cannot be null");
        if (participant == null) throw new NullPointerException("participant cannot be null");
        if (createdState == null) throw new NullPointerException("createdState cannot be null");

        if (createdState.getState() != AssessmentInterviewPanelState.CREATED.getBackingState())
            throw new IllegalArgumentException("createdState must be CREATED");
        if (!participant.getRole().isOfType(UserRoleType.INTERVIEW_LEAD_APPLICANT))
            throw new IllegalArgumentException("participant must be INTERVIEW_LEAD_APPLICANT");
        if (!participant.getApplicationId().equals(application.getId()))
            throw new IllegalArgumentException("participant application must match the application");
        if (!participant.getOrganisationId().equals(application.getLeadOrganisationId()))
            throw new IllegalArgumentException("participant organisation must match the application's lead organisation");
        if (!participant.getUser().getId().equals(application.getLeadApplicant().getId()))
            throw new IllegalArgumentException("participant user must match the application's lead user");

        this.target = application;
        this.participant = participant;
        setActivityState(createdState);
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
    public AssessmentInterviewPanelState getActivityState() {
        return AssessmentInterviewPanelState.fromState(activityState.getState());
    }

    public void setResponse(AssessmentInterviewPanelResponseOutcome response) {
        this.response = response;
    }

    public AssessmentInterviewPanelResponseOutcome getResponse() {
        return response;
    }

    public void setMessage(AssessmentInterviewPanelMessageOutcome message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentInterviewPanel that = (AssessmentInterviewPanel) o;

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

    public AssessmentInterviewPanelMessageOutcome getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("participant", participant)
                .append("target", target)
                .append("message", message)
                .append("response", response)
                .append("activityState", activityState)
                .append("processOutcomes", processOutcomes)
                .append("internalParticipant", internalParticipant)
                .toString();
    }
}