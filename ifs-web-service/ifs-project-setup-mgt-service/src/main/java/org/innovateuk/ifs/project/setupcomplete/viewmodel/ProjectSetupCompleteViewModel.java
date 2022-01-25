package org.innovateuk.ifs.project.setupcomplete.viewmodel;

import lombok.Getter;
import lombok.ToString;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;

import java.time.LocalDate;

@Getter
@ToString
public class ProjectSetupCompleteViewModel {

    private final long projectId;
    private final long applicationId;
    private final long competitionId;
    private final String projectName;
    private final LocalDate targetStartDate;

    private final ProjectState state;

    public ProjectSetupCompleteViewModel(ProjectResource project) {
        this.projectId = project.getId();
        this.applicationId = project.getApplication();
        this.competitionId = project.getCompetition();
        this.projectName = project.getName();
        this.state = project.getProjectState();
        this.targetStartDate = project.getTargetStartDate();
    }

    public boolean isReadonly() {
        return !state.isActive();
    }

    public boolean isUnsuccessful() {
        return state.isUnsuccessful();
    }
}
