package org.innovateuk.ifs.application.finance.viewmodel;

/**
 * A view model for displaying project finance rows and changes made by internal project finance team
 */
public class ProjectFinanceChangesViewModel {
    private boolean isInternal;
    private String organisationName;
    private Long organisationId;
    private String projectName;
    private Long applicationId;
    private Long projectId;
    private boolean procurementCompetition;

    private ProjectFinanceChangesFinanceSummaryViewModel financeSummary;
    private ProjectFinanceChangesProjectFinancesViewModel projectFinances;
    private ProjectFinanceChangesMilestoneDifferencesViewModel milestoneDifferences;

    public ProjectFinanceChangesViewModel(boolean isInternal, String organisationName, Long organisationId,
                                          String projectName, Long applicationId, Long projectId,
                                          boolean procurementCompetition,
                                          ProjectFinanceChangesFinanceSummaryViewModel financeSummary,
                                          ProjectFinanceChangesProjectFinancesViewModel projectFinances,
                                          ProjectFinanceChangesMilestoneDifferencesViewModel milestoneDifferences) {
        this.isInternal = isInternal;
        this.organisationName = organisationName;
        this.organisationId = organisationId;
        this.projectName = projectName;
        this.applicationId = applicationId;
        this.projectId = projectId;
        this.procurementCompetition = procurementCompetition;
        this.financeSummary = financeSummary;
        this.projectFinances = projectFinances;
        this.milestoneDifferences = milestoneDifferences;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getProjectName() {
        return projectName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public boolean isInternal() {
        return isInternal;
    }

    public boolean isProcurementCompetition() {
        return procurementCompetition;
    }

    public ProjectFinanceChangesFinanceSummaryViewModel getFinanceSummary() {
        return financeSummary;
    }

    public ProjectFinanceChangesMilestoneDifferencesViewModel getMilestoneDifferences() {
        return milestoneDifferences;
    }

    public ProjectFinanceChangesProjectFinancesViewModel getProjectFinances() {
        return projectFinances;
    }

    public boolean hasChanges() {
        if (projectFinances != null && projectFinances.hasChanges()) {
            return true;
        }
        return milestoneDifferences != null && milestoneDifferences.hasChanges();

    }
}
