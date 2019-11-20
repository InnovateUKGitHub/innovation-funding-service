package org.innovateuk.ifs.project.pendingpartner.viewmodel;

import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

public class PendingPartnerProgressLandingPageViewModel {

    private final long projectId;
    private final long applicationId;
    private final String projectName;
    private final boolean yourOrganisationComplete;
    private final boolean yourFundingComplete;
    private final boolean termsAndConditionsComplete;
    private final boolean showYourOrganisation;
    private final long organisationId;

    public PendingPartnerProgressLandingPageViewModel(ProjectResource project,
                                                      PendingPartnerProgressResource progress,
                                                      boolean showYourOrganisation,
                                                      long organisationId) {
        this.projectId = project.getId();
        this.applicationId = project.getApplication();
        this.projectName = project.getName();
        this.yourOrganisationComplete = progress.isYourOrganisationComplete();
        this.yourFundingComplete = progress.isYourFundingComplete();
        this.termsAndConditionsComplete = progress.isTermsAndConditionsComplete();
        this.showYourOrganisation = showYourOrganisation;
        this.organisationId = organisationId;
    }

    public long getProjectId() {
        return projectId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getProjectName() {
        return projectName;
    }

    public boolean isYourOrganisationComplete() {
        return yourOrganisationComplete;
    }

    public boolean isYourFundingComplete() {
        return yourFundingComplete;
    }

    public boolean isTermsAndConditionsComplete() {
        return termsAndConditionsComplete;
    }

    public boolean isShowYourOrganisation() {
        return showYourOrganisation;
    }

    public long getOrganisationId() {
        return organisationId;
    }
}
