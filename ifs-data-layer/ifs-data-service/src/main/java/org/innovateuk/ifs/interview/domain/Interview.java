package org.innovateuk.ifs.interview.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.interview.resource.InterviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

/**
 * An invitation for an assessor to interview an application's applicants on an interview panel.
 */
@Entity
public class Interview extends Process<ProcessRole, Application, InterviewState> {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "participant_id", referencedColumnName = "id")
    private ProcessRole participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "id")
    private Application target;

    @Column(name="activity_state_id")
    private InterviewState activityState;

    public Interview() {
        super();
    }

    public Interview(Application application, InterviewParticipant interviewParticipant) {
        this.participant = new ProcessRole(interviewParticipant.getUser(), application.getId(), Role.INTERVIEW_ASSESSOR);
        this.target = application;
    }

    @Deprecated
    public Interview(Application application, ProcessRole processRole) {
        if (!application.getId().equals(processRole.getApplicationId())) {
            throw new IllegalArgumentException("application.id must equal processRole.id");
        }
        this.participant = processRole;
        this.target = application;
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

    @Override
    public InterviewState getProcessState() {
        return activityState;
    }

    @Override
    public void setProcessState(InterviewState status) {
        this.activityState = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Interview interview = (Interview) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(participant, interview.participant)
                .append(target, interview.target)
                .append(activityState, interview.activityState)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(participant)
                .append(target)
                .append(activityState)
                .toHashCode();
    }
}