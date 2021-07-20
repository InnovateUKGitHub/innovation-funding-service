package org.innovateuk.ifs.project.financechecks.viewmodel;

import java.util.List;

public class ProjectFinanceChecksReadOnlyViewModel {

    private final Long projectId;
    private final String projectName;
    private final boolean paymentMilestonesLink;
    private final List<ProjectOrganisationRowViewModel> projectOrganisationRows;

    public ProjectFinanceChecksReadOnlyViewModel(Long projectId, String projectName, boolean paymentMilestonesLink, List<ProjectOrganisationRowViewModel> projectOrganisationRows) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.paymentMilestonesLink = paymentMilestonesLink;
        this.projectOrganisationRows = projectOrganisationRows;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public boolean isPaymentMilestonesLink() {
        return paymentMilestonesLink;
    }

    public List<ProjectOrganisationRowViewModel> getProjectOrganisationRows() {
        return projectOrganisationRows;
    }
}
