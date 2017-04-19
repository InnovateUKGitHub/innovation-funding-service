package org.innovateuk.ifs.project.financechecks;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.financechecks.controller.ProjectFinanceChecksOverviewController;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Test;

import java.util.Collections;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.project.financechecks.controller.ProjectFinanceChecksOverviewController.PROJECT_FINANCE_CHECKS_BASE_URL;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Unit tests for finance check overview page for external users
 */
public class ProjectFinanceChecksOverviewControllerTest extends BaseControllerMockMVCTest<ProjectFinanceChecksOverviewController> {

    @Test
    public void testOverviewPageWorks() throws Exception {
        ApplicationResource application = newApplicationResource().withId(123L).build();
        ProjectResource project = newProjectResource().withId(1L).withName("Project1").withApplication(application).build();
        OrganisationResource industrialOrganisation = newOrganisationResource()
                .withId(2L)
                .withName("Industrial Org")
                .withCompanyHouseNumber("123456789")
                .withOrganisationTypeName(OrganisationTypeEnum.BUSINESS.name())
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();
        FinanceCheckEligibilityResource eligibilityOverview = newFinanceCheckEligibilityResource().build();

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(industrialOrganisation.getId());
        partnerOrganisationResource.setLeadOrganisation(false);

        when(organisationService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(industrialOrganisation.getId());
        when(projectService.getById(project.getId())).thenReturn(project);
        when(partnerOrganisationServiceMock.getPartnerOrganisations(project.getId())).thenReturn(serviceSuccess(Collections.singletonList(partnerOrganisationResource)));
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), industrialOrganisation.getId())).thenReturn(eligibilityOverview);
        mockMvc.perform(get(PROJECT_FINANCE_CHECKS_BASE_URL, project.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("project/finance-checks-overview"))
                .andReturn();
    }

    @Override
    protected ProjectFinanceChecksOverviewController supplyControllerUnderTest() {
        return new ProjectFinanceChecksOverviewController();
    }
}
