package org.innovateuk.ifs.project.grantofferletter.viewmodel;

public class ProcurementGrantOfferLetterTemplateViewModel {

    private final long applicationId;
    private final String organisationName;
    private final String projectName;
    private final String competitionName;
    private final String projectManagerName;

    public ProcurementGrantOfferLetterTemplateViewModel(long applicationId, String organisationName, String projectName, String competitionName, String projectManagerName) {
        this.applicationId = applicationId;
        this.organisationName = organisationName;
        this.projectName = projectName;
        this.competitionName = competitionName;
        this.projectManagerName = projectManagerName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getProjectManagerName() {
        return projectManagerName;
    }
}