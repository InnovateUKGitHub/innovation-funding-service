package org.innovateuk.ifs.project.financechecks.viewmodel;

public class ProjectFinanceChecksReadOnlyViewModel {

    private final Long projectId;
    private final String projectName;
    private final long applicationId;

    public ProjectFinanceChecksReadOnlyViewModel(Long projectId, String projectName, long applicationId) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.applicationId = applicationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getApplicationId() {
        return applicationId;
    }
}
