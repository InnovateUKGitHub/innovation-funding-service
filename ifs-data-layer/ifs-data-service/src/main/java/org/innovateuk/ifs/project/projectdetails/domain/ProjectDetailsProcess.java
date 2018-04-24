package org.innovateuk.ifs.project.projectdetails.domain;

import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.*;

@Entity
public class ProjectDetailsProcess extends Process<ProjectUser, Project, ProjectDetailsState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id", referencedColumnName = "id")
    private ProjectUser participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private Project target;

    @Column(name="activity_state_id")
    private ProjectDetailsState activityState;

    ProjectDetailsProcess() {
    }

    public ProjectDetailsProcess(ProjectUser participant, Project target, ProjectDetailsState originalState) {
        this.participant = participant;
        this.target = target;
        this.setActivityState(originalState);
    }

    @Override
    public void setActivityState(ProjectDetailsState status) {
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
    public ProjectDetailsState getProcessState() {
        return activityState;
    }

    @Override
    public ProjectDetailsState getActivityState() {
        return activityState;
    }
}