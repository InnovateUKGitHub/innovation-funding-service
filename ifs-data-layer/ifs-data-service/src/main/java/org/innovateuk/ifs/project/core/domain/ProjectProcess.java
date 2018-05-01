package org.innovateuk.ifs.project.core.domain;

import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.workflow.domain.Process;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;

/**
 * The process for Project Setup
 */
@Entity
public class ProjectProcess extends Process<ProjectUser, Project, ProjectState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id", referencedColumnName = "id")
    private ProjectUser participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private Project target;

    @Column(name="activity_state_id")
    private ProjectState activityState;

    public ProjectProcess() {
    }

    public ProjectProcess(ProjectUser participant, Project target, ProjectState originalState) {
        this.participant = participant;
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
    public ProjectState getProcessState() {
        return activityState;
    }

    @Override
    public void setProcessState(ProjectState status) {
        this.activityState = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectProcess that = (ProjectProcess) o;

        return new EqualsBuilder()
                .append(participant, that.participant)
                .append(target, that.target)
                .append(activityState, that.activityState)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(participant)
                .append(target)
                .append(activityState)
                .toHashCode();
    }
}