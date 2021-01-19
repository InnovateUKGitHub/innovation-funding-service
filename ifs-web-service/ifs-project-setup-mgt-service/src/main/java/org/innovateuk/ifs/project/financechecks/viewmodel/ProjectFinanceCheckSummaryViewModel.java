package org.innovateuk.ifs.project.financechecks.viewmodel;

import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleNoneMatch;

/**
 * View model backing the internal Finance Team members view of the Finance Check summary page
 */
public class ProjectFinanceCheckSummaryViewModel {

    private FinanceCheckSummaryResource financeCheckSummaryResource;
    private boolean projectIsActive;
    private boolean collaborativeProject;
    private boolean hasOrganisationSizeChanged;
    private boolean userHasExternalFinanceRole;
    private final boolean hasSpendProfileStage;

    public ProjectFinanceCheckSummaryViewModel(FinanceCheckSummaryResource financeCheckSummaryResource,
                                               boolean projectIsActive,
                                               boolean collaborativeProject,
                                               boolean hasOrganisationSizeChanged,
                                               boolean userHasExternalFinanceRole,
                                               boolean hasSpendProfileStage) {
        this.financeCheckSummaryResource = financeCheckSummaryResource;
        this.projectIsActive = projectIsActive;
        this.collaborativeProject = collaborativeProject;
        this.hasOrganisationSizeChanged = hasOrganisationSizeChanged;
        this.userHasExternalFinanceRole = userHasExternalFinanceRole;
        this.hasSpendProfileStage = hasSpendProfileStage;
    }

    private boolean isGenerateSpendProfileReady() {
        return financeCheckSummaryResource.isFinanceChecksAllApproved() &&
                simpleNoneMatch(financeCheckSummaryResource.getPartnerStatusResources(),
                        partnerStatusResource -> !partnerStatusResource.isFinanceContactProvided());
    }

    public boolean isShowEnabledGenerateSpendProfilesButton() {
        return !financeCheckSummaryResource.isSpendProfilesGenerated() &&
                isGenerateSpendProfileReady() &&
                projectIsActive;
    }

    public boolean isShowDisabledGenerateSpendProfilesButton() {
        return !financeCheckSummaryResource.isSpendProfilesGenerated() &&
                !isGenerateSpendProfileReady();
    }

    public boolean isShowSpendProfilesGeneratedMessage() {
        return financeCheckSummaryResource.isSpendProfilesGenerated();
    }

    public boolean isHasOrganisationSizeChanged() {
        return hasOrganisationSizeChanged;
    }

    public boolean isShowChangeFundingLevelPercentageAlert() {
        return financeCheckSummaryResource.isAllEligibilityAndViabilityInReview() && isHasOrganisationSizeChanged();
    }

    public FinanceCheckSummaryResource getFinanceCheckSummaryResource() {
        return financeCheckSummaryResource;
    }

    public void setFinanceCheckSummaryResource(FinanceCheckSummaryResource financeCheckSummaryResource) {
        this.financeCheckSummaryResource = financeCheckSummaryResource;
    }

    public boolean isProjectIsActive() {
        return projectIsActive;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isUserHasExternalFinanceRole() {
        return userHasExternalFinanceRole;
    }

    public boolean canEditProjectDuration()  {
        return !userHasExternalFinanceRole && !financeCheckSummaryResource.isSpendProfilesGenerated();
    }

    public boolean isHasSpendProfileStage() {
        return hasSpendProfileStage;
    }

    public boolean displayMilestoneColumn() {
        return this.financeCheckSummaryResource.getPartnerStatusResources().stream()
                .filter(partner -> partner.getPaymentMilestoneState() != null)
                .findAny()
                .isPresent();
    }

}