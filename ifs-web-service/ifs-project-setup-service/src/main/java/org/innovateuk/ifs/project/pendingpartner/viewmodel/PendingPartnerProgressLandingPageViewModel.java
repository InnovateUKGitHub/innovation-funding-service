package org.innovateuk.ifs.project.pendingpartner.viewmodel;

import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.Optional;

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
    private final boolean isReadyToJoinProject;
    private final boolean showSubsidyBasis;
    private final boolean subsidyBasisComplete;
    private final Optional<Long> subsidyBasisQuestionId;

    public PendingPartnerProgressLandingPageViewModel(ProjectResource project, long organisationId, PendingPartnerProgressResource progress, boolean showYourOrganisation, boolean showSubsidyBasis, Optional<Long> subsidyBasisQuestionId) {
        this.projectId = project.getId();
        this.organisationId = organisationId;
        this.applicationId = project.getApplication();
        this.projectName = project.getName();
        this.yourOrganisationComplete = progress.isYourOrganisationComplete();
        this.yourFundingComplete = progress.isYourFundingComplete();
        this.termsAndConditionsComplete = progress.isTermsAndConditionsComplete();
        this.showYourOrganisation = showYourOrganisation;
        this.completed = progress.isCompleted();
        this.isReadyToJoinProject = progress.isReadyToJoinProject();
        this.showSubsidyBasis = showSubsidyBasis;
        this.subsidyBasisComplete = progress.isSubsidyBasisComplete();
        this.subsidyBasisQuestionId = subsidyBasisQuestionId;
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
        return isReadyToJoinProject;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isShowSubsidyBasis() {
        return showSubsidyBasis;
    }

    public boolean isSubsidyBasisComplete() {
        return subsidyBasisComplete;
    }

    public Long getSubsidyBasisQuestionId() {
        return subsidyBasisQuestionId.orElse(null);
    }
}
