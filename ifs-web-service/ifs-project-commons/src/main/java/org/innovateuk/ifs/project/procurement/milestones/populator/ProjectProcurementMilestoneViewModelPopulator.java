package org.innovateuk.ifs.project.procurement.milestones.populator;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.grantofferletter.GrantOfferLetterService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.procurement.milestones.viewmodel.ProjectProcurementMilestoneViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectProcurementMilestoneViewModelPopulator {

    @Autowired
    private ProjectRestService projectRestService;

    @Autowired
    private ProjectFinanceRestService projectFinanceRestService;

    @Autowired
    private FinanceCheckRestService financeCheckRestService;

    @Autowired
    private GrantOfferLetterService grantOfferLetterService;

    public ProjectProcurementMilestoneViewModel populate(long projectId, long organisationId, UserResource userResource, boolean editMilestones, boolean showChangesLink) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();

        PaymentMilestoneResource paymentMilestoneResource = financeCheckRestService.getPaymentMilestoneState(projectId, organisationId).getSuccess();

        boolean readOnly = !(userResource.isInternalUser() && editMilestones);

        ProjectFinanceResource finance = projectFinanceRestService.getProjectFinance(projectId, organisationId).getSuccess();

        boolean resettableGolState = false;

        if (userResource.isInternalUser()) {
            GrantOfferLetterStateResource golState = grantOfferLetterService.getGrantOfferLetterState(projectId).getSuccess();
            resettableGolState = golState.getState() != GrantOfferLetterState.APPROVED;
        }

        return new ProjectProcurementMilestoneViewModel(project,
                finance,
                String.format("/project-setup-management/project/%d/finance-check", projectId),
                readOnly,
                paymentMilestoneResource,
                userResource.isInternalUser() ? isAllEligibilityAndViabilityApproved(projectId) : false,
                userResource.isExternalUser(),
                resettableGolState,
                showChangesLink);
    }

    private boolean isAllEligibilityAndViabilityApproved(long projectId) {
        FinanceCheckSummaryResource financeCheckSummaryResource = financeCheckRestService.getFinanceCheckSummary(projectId).getSuccess();
        return financeCheckSummaryResource.isAllEligibilityAndViabilityApproved();
    }
}
