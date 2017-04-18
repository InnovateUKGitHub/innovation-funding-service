package org.innovateuk.ifs.project.projectdetails.domain;

import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ProjectDetailsState;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.Process;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ProjectDetailsProcess extends Process<ProjectUser, Project, ProjectDetailsState> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id", referencedColumnName = "id")
    private ProjectUser participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id", referencedColumnName = "id")
    private Project target;

    // for ORM use
    ProjectDetailsProcess() {
    }

    public ProjectDetailsProcess(ProjectUser participant, Project target, ActivityState originalState) {
        this.participant = participant;
        this.target = target;
        this.setActivityState(originalState);
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
    public ProjectDetailsState getActivityState() {
        return ProjectDetailsState.fromState(activityState.getState());
    }
}
