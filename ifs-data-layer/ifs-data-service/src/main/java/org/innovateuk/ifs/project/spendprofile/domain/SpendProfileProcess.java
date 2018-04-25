package org.innovateuk.ifs.project.spendprofile.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

/**
 * The process of submitting and approving a complete Spend Profile for Projects
 */
@Entity
public class SpendProfileProcess extends Process<ProjectUser, Project, SpendProfileState> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id", referencedColumnName = "id")
    private ProjectUser participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private Project target;

    @Column(name="activity_state_id")
    private SpendProfileState activityState;

    SpendProfileProcess() {
    }

    public SpendProfileProcess(ProjectUser participant, Project target, SpendProfileState originalState) {
        this.participant = participant;
        this.target = target;
        this.setActivityState(originalState);
    }

    public SpendProfileProcess(User internalParticipant, Project target, SpendProfileState originalState) {
        this.internalParticipant = internalParticipant;
        this.target = target;
        this.setActivityState(originalState);
    }

    @Override
    public void setActivityState(SpendProfileState status) {
        this.activityState = status;
    }

    @Override
    public void setParticipant(ProjectUser participant) {
        this.participant = participant;
    }

    @Override
    public ProjectUser getParticipant() {
        return participant;
    }

    @Override
    public Project getTarget() {
        return target;
    }

    @Override
    public void setTarget(Project target) {
        this.target = target;
    }

    @Override
    public SpendProfileState getProcessState() {
        return activityState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SpendProfileProcess that = (SpendProfileProcess) o;

        return new EqualsBuilder()
                .append(participant, that.participant)
                .append(target, that.target)
                .append(activityState, that.activityState)
                .append(getProcessEvent(), that.getProcessEvent())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(participant)
                .append(target)
                .toHashCode();
    }

    @Override
    public SpendProfileState getActivityState() {
        return activityState;
    }
}