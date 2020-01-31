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

    public ProjectFinanceCheckSummaryViewModel(FinanceCheckSummaryResource financeCheckSummaryResource,
                                               boolean projectIsActive,
                                               boolean collaborativeProject) {
        this.financeCheckSummaryResource = financeCheckSummaryResource;
        this.projectIsActive = projectIsActive;
        this.collaborativeProject = collaborativeProject;
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

    public boolean isShowChangeFundingLevelPercentageAlert() {
        return financeCheckSummaryResource.isAllEligibilityAndViabilityInReview();
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
}