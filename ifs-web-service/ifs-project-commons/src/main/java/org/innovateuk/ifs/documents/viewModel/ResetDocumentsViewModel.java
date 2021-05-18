package org.innovateuk.ifs.documents.viewModel;

public class ResetDocumentsViewModel {

    private final long projectId;
    private final long competitionId;
    private final String projectName;
    private final long applicationId;
    private final boolean projectManager;
    private final boolean projectIsActive;

    public ResetDocumentsViewModel(long projectId, long competitionId, String projectName, long applicationId, boolean projectManager, boolean projectIsActive) {
        this.projectId = projectId;
        this.competitionId = competitionId;
        this.projectName = projectName;
        this.applicationId = applicationId;
        this.projectManager = projectManager;
        this.projectIsActive = projectIsActive;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public boolean isProjectManager() {
        return projectManager;
    }

    public boolean isProjectIsActive() {
        return projectIsActive;
    }
}
