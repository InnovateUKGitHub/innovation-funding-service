package org.innovateuk.ifs.project.financechecks.viewmodel;

import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleNoneMatch;

/**
 * View model backing the internal Finance Team members view of the Finance Check summary page
 */
public class ProjectFinanceCheckSummaryViewModel {

    private FinanceCheckSummaryResource financeCheckSummaryResource;
    private boolean projectIsActive;

    public ProjectFinanceCheckSummaryViewModel(FinanceCheckSummaryResource financeCheckSummaryResource,
                                               boolean projectIsActive) {
        this.financeCheckSummaryResource = financeCheckSummaryResource;
        this.projectIsActive = projectIsActive;
    }

    private boolean isGenerateSpendProfileReady() {
        return financeCheckSummaryResource.isFinanceChecksAllApproved() &&
                simpleNoneMatch(financeCheckSummaryResource.getPartnerStatusResources(),
                        partnerStatusResource -> !partnerStatusResource.isFinanceContactProvided());
    }

    public boolean isShowEnabledGenerateSpendProfilesButton() {
        return !financeCheckSummaryResource.isSpendProfilesGenerated() &&
                isGenerateSpendProfileReady();
    }

    public boolean isShowDisabledGenerateSpendProfilesButton() {
        return !financeCheckSummaryResource.isSpendProfilesGenerated() &&
                !isGenerateSpendProfileReady();
    }

    public boolean isShowSpendProfilesGeneratedMessage() {
        return financeCheckSummaryResource.isSpendProfilesGenerated();
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
}
