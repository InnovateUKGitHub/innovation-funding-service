package org.innovateuk.ifs.project.financechecks.viewmodel;

import java.util.List;

public class ProjectFinanceChecksReadOnlyViewModel {

    private final Long projectId;
    private final String projectName;
    private final List<ProjectOrganisationRowViewModel> projectOrganisationRows;

    public ProjectFinanceChecksReadOnlyViewModel(Long projectId, String projectName,List<ProjectOrganisationRowViewModel> projectOrganisationRows) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectOrganisationRows = projectOrganisationRows;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public List<ProjectOrganisationRowViewModel> getProjectOrganisationRows() {
        return projectOrganisationRows;
    }
}
