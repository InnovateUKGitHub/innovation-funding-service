package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.GRANT;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.project.financechecks.controller.ProjectFinanceChecksOverviewController.PROJECT_FINANCE_CHECKS_BASE_URL;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Unit tests for finance check overview page for external users
 */
public class ProjectFinanceChecksOverviewControllerTest extends BaseControllerMockMVCTest<ProjectFinanceChecksOverviewController> {

    @Mock
    private ProjectService projectService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private FinanceCheckService financeCheckServiceMock;

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void viewOverview() throws Exception {
        CompetitionResource competition = newCompetitionResource().withFundingType(GRANT).withFinanceRowTypes(singleton(FinanceRowType.FINANCE)).build();
        ApplicationResource application = newApplicationResource().withId(123L).withCompetition(competition.getId()).build();
        ProjectResource project = newProjectResource().withId(1L).withName("Project1").withApplication(application).withCompetition(competition.getId()).build();
        OrganisationResource industrialOrganisation = newOrganisationResource()
                .withId(2L)
                .withName("Industrial Org")
                .withCompaniesHouseNumber("123456789")
                .withOrganisationTypeName(OrganisationTypeEnum.BUSINESS.name())
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();
        FinanceCheckEligibilityResource eligibilityOverview = newFinanceCheckEligibilityResource().build();

        PartnerOrganisationResource partnerOrganisationResource = new PartnerOrganisationResource();
        partnerOrganisationResource.setOrganisation(industrialOrganisation.getId());
        partnerOrganisationResource.setLeadOrganisation(false);

        when(projectService.getOrganisationIdFromUser(project.getId(), loggedInUser)).thenReturn(industrialOrganisation.getId());
        when(projectService.getById(project.getId())).thenReturn(project);
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId())).thenReturn(restSuccess(singletonList(partnerOrganisationResource)));
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), industrialOrganisation.getId())).thenReturn(eligibilityOverview);
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

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
