package org.innovateuk.ifs.project.procurement.milestones.propulator;

import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.grantofferletter.GrantOfferLetterService;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.procurement.milestones.populator.ProjectProcurementMilestoneViewModelPopulator;
import org.innovateuk.ifs.project.procurement.milestones.viewmodel.ProjectProcurementMilestoneViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectProcurementMilestoneViewModelPopulatorTest {

    @InjectMocks
    private ProjectProcurementMilestoneViewModelPopulator populator;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private FinanceCheckRestService financeCheckRestService;

    @Mock
    private GrantOfferLetterService grantOfferLetterService;

    private long projectId = 2L;
    private long organisationId = 3L;

    @Test
    public void populateForMo() {
        ProjectResource projectResource = newProjectResource()
                .withApplication(newApplicationResource().build())
                .build();
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(projectResource));

        PaymentMilestoneResource paymentMilestoneResource = new PaymentMilestoneResource();
        when(financeCheckRestService.getPaymentMilestoneState(projectId, organisationId)).thenReturn(restSuccess(paymentMilestoneResource));

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource()
                .withOrganisation(organisationId)
                .build();
        when(projectFinanceRestService.getProjectFinance(projectId, organisationId)).thenReturn(restSuccess(projectFinanceResource));

        UserResource userResource = newUserResource().withRoleGlobal(Role.MONITORING_OFFICER).build();

        ProjectProcurementMilestoneViewModel viewModel = populator.populate(projectId, organisationId, userResource, false, false);

        assertTrue(viewModel.isReadOnly());
        assertTrue(viewModel.isExternalUser());
        assertEquals("/project/2/finance-check/read-only", viewModel.getExternalUserLinkUrl());
        assertTrue(viewModel.isMonitoringOfficer());
    }
}
