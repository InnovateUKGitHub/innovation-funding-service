package org.innovateuk.ifs.project.grantofferletter.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

/**
 * The process of submitting and approving Grant Offer Letter for Projects
 */
@Entity
public class GOLProcess extends Process<ProjectUser, Project, GrantOfferLetterState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id", referencedColumnName = "id")
    private ProjectUser participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private Project target;

    @Column(name="activity_state_id")
    private GrantOfferLetterState activityState;

    GOLProcess() {
    }

    public GOLProcess(ProjectUser participant, Project target, GrantOfferLetterState originalState) {
        this.participant = participant;
        this.target = target;
        this.setProcessState(originalState);
    }

    public GOLProcess(User internalParticipant, Project target, GrantOfferLetterState originalState) {
        this.internalParticipant = internalParticipant;
        this.target = target;
        this.setProcessState(originalState);
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
    public GrantOfferLetterState getProcessState() {
        return activityState;
    }

    @Override
    public void setProcessState(GrantOfferLetterState status) {
        this.activityState = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GOLProcess that = (GOLProcess) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(participant, that.participant)
                .append(target, that.target)
                .append(activityState, that.activityState)
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