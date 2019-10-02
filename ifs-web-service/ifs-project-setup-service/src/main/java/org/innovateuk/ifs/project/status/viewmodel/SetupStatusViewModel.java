package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.project.projectdetails.viewmodel.BasicProjectDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;

import java.util.List;

/**
 * A view model that backs the Project Status page
 */
public class SetupStatusViewModel implements BasicProjectDetailsViewModel {

    private final long projectId;
    private final String projectName;
    private final boolean monitoringOfficer;
    private final ProjectState projectState;
    private final long applicationId;
    private final String competitionName;
    private final long competitionId;
    private final boolean loanCompetition;
    private final List<SetupStatusStageViewModel> stages;

    public SetupStatusViewModel(ProjectResource project, boolean monitoringOfficer, List<SetupStatusStageViewModel> stages, boolean loanCompetition) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.monitoringOfficer = monitoringOfficer;
        this.projectState = project.getProjectState();
        this.applicationId = project.getApplication();
        this.competitionName = project.getCompetitionName();
        this.competitionId = project.getCompetition();
        this.stages = stages;
        this.loanCompetition = loanCompetition;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public ProjectState getProjectState() {
        return projectState;
    }

    public boolean isMonitoringOfficer() {
        return monitoringOfficer;
    }

    public boolean isLoanCompetition() {
        return loanCompetition;
    }

    public List<SetupStatusStageViewModel> getStages() {
        return stages;
    }

    public boolean shouldShowStatus(SetupStatusStageViewModel stage) {
        return isMonitoringOfficer() || !stage.getAccess().isNotAccessible();
    }
}
