package org.innovateuk.ifs.project.pendingpartner.viewmodel;

import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.time.ZonedDateTime;

public class PendingPartnerProgressLandingPageViewModel {

    private final long projectId;
    private final long organisationId;
    private final long applicationId;
    private final String projectName;
    private final boolean yourOrganisationComplete;
    private final boolean yourFundingComplete;
    private final boolean termsAndConditionsComplete;
    private final boolean showYourOrganisation;
    private final boolean completed;

    public PendingPartnerProgressLandingPageViewModel(ProjectResource project, long organisationId, PendingPartnerProgressResource progress, boolean showYourOrganisation) {
        this.projectId = project.getId();
        this.organisationId = organisationId;
        this.applicationId = project.getApplication();
        this.projectName = project.getName();
        this.yourOrganisationComplete = progress.isYourOrganisationComplete();
        this.yourFundingComplete = progress.isYourFundingComplete();
        this.termsAndConditionsComplete = progress.isTermsAndConditionsComplete();
        this.showYourOrganisation = showYourOrganisation;
        this.completed = progress.isCompleted();
    }

    public long getProjectId() {
        return projectId;
    }

    public long getOrganisationId() {
        return organisationId;
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

    public boolean isReadyToJoinProject() {
        return isYourFundingComplete() && isTermsAndConditionsComplete() && (!isShowYourOrganisation() || isYourOrganisationComplete()) && !isCompleted();
    }

    public boolean isCompleted() {
        return completed;
    }
}
