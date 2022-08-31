package org.innovateuk.ifs.project.eligibility.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.validator.YourProjectCostsFormValidator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.eligibility.form.FinanceChecksEligibilityForm;
import org.innovateuk.ifs.financecheck.eligibility.viewmodel.FinanceChecksEligibilityViewModel;
import org.innovateuk.ifs.grantofferletter.GrantOfferLetterService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.eligibility.populator.FinanceChecksEligibilityProjectCostsFormPopulator;
import org.innovateuk.ifs.project.eligibility.populator.ProjectAcademicCostFormPopulator;
import org.innovateuk.ifs.project.eligibility.populator.ProjectFinanceChangesViewModelPopulator;
import org.innovateuk.ifs.project.eligibility.saver.FinanceChecksEligibilityProjectCostsSaver;
import org.innovateuk.ifs.project.eligibility.viewmodel.FinanceChecksProjectCostsViewModel;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
import org.innovateuk.ifs.project.finance.resource.EligibilityResource;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentItemResourceBuilder.newPublicContentItemResource;
import static org.innovateuk.ifs.publiccontent.builder.PublicContentResourceBuilder.newPublicContentResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(Parameterized.class)
public class KtpFinanceChecksEligibilityControllerTest extends AbstractAsyncWaitMockMVCTest<FinanceChecksEligibilityController> {

    private final FundingType fundingType;

    private OrganisationResource industrialOrganisation;

    private OrganisationResource academicOrganisation;

    private OrganisationResource kbOrganisation;

    private ApplicationResource application = newApplicationResource().withId(123L).build();

    private ProjectResource project = newProjectResource()
            .withId(1L)
            .withName("Project1")
            .withApplication(application)
            .withProjectState(SETUP)
            .build();

    private FinanceCheckEligibilityResource eligibilityOverview = newFinanceCheckEligibilityResource().build();

    private GrantOfferLetterStateResource grantOfferLetterStateResource = GrantOfferLetterStateResource.stateInformationForPartnersView(GrantOfferLetterState.PENDING, null);

    @Mock
    private ProjectService projectService;

    @Mock
    private FinanceCheckService financeCheckServiceMock;

    @Mock
    private FinanceCheckRestService financeCheckRestService;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private FinanceService financeService;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private FinanceChecksEligibilityProjectCostsFormPopulator formPopulator;

    @Mock
    private YourProjectCostsFormValidator yourProjectCostsFormValidator;

    @Mock
    private FinanceChecksEligibilityProjectCostsSaver yourProjectCostsSaver;

    @Mock
    private ProjectFinanceChangesViewModelPopulator projectFinanceChangesViewModelPopulator;

    @Mock
    private ProjectAcademicCostFormPopulator projectAcademicCostFormPopulator;

    @Mock
    private GrantOfferLetterService grantOfferLetterService;

    @Mock
    private PublicContentItemRestService publicContentItemRestService;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    public KtpFinanceChecksEligibilityControllerTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Override
    protected FinanceChecksEligibilityController supplyControllerUnderTest() {
        return new FinanceChecksEligibilityController();
    }

    @Before
    public void setupData() {

        industrialOrganisation = newOrganisationResource()
                .withId(2L)
                .withName("Industrial Org")
                .withCompaniesHouseNumber("123456789")
                .withOrganisationTypeName(OrganisationTypeEnum.BUSINESS.name())
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                .build();

        academicOrganisation = newOrganisationResource()
                .withId(1L)
                .withName("Academic Org")
                .withOrganisationTypeName(OrganisationTypeEnum.RESEARCH.name())
                .withOrganisationType(OrganisationTypeEnum.RESEARCH.getId())
                .build();

        kbOrganisation = newOrganisationResource()
                .withId(11L)
                .withName("Kb Org")
                .withOrganisationTypeName(OrganisationTypeEnum.KNOWLEDGE_BASE.name())
                .withOrganisationType(OrganisationTypeEnum.KNOWLEDGE_BASE.getId())
                .build();

        when(organisationRestService.getOrganisationById(industrialOrganisation.getId())).thenReturn(restSuccess(industrialOrganisation));
        when(organisationRestService.getOrganisationById(academicOrganisation.getId())).thenReturn(restSuccess(academicOrganisation));
        when(organisationRestService.getOrganisationById(kbOrganisation.getId())).thenReturn(restSuccess(kbOrganisation));
    }

    @Test
    public void testViewEligibilityFecKbOrg() throws Exception {

        List<FinanceRowType> expectedFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .filter(financeRowType -> financeRowType.isCost()
                        && !FinanceRowType.getNonFecSpecificFinanceRowTypes().contains(financeRowType))
                .collect(Collectors.toList());

        CompetitionResource competitionResource = newCompetitionResource()
                .withFundingType(fundingType)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .build();

        ProjectResource project = newProjectResource()
                .withId(10L)
                .withName("Project1")
                .withApplication(application)
                .withCompetition(competitionResource.getId())
                .withProjectState(SETUP)
                .build();

        ProjectFinanceResource projectFinance = newProjectFinanceResource()
                .withFecEnabled(true)
                .withOrganisation(kbOrganisation.getId())
                .withGrantClaimPercentage(BigDecimal.valueOf(100))
                .withFinanceOrganisationDetails(asMap(
                        FinanceRowType.OTHER_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.ASSOCIATE_SALARY_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.CONSUMABLES, newDefaultCostCategory().build(),
                        FinanceRowType.ASSOCIATE_SUPPORT, newDefaultCostCategory().build(),
                        FinanceRowType.KNOWLEDGE_BASE, newDefaultCostCategory().build(),
                        FinanceRowType.ESTATE_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.KTP_TRAVEL, newDefaultCostCategory().build()))
                .build();

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility, project);

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItemResource = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getByApplicationId(application.getId())).thenReturn(project);
        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(kbOrganisation);
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(Collections.singletonList(projectFinance)));
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), kbOrganisation.getId())).thenReturn(eligibilityOverview);
        when(projectFinanceRestService.getFinanceTotals(project.getId())).thenReturn(restSuccess(Collections.emptyList()));
        when(grantOfferLetterService.getGrantOfferLetterState(project.getId())).thenReturn(serviceSuccess(grantOfferLetterStateResource));
        when(publicContentItemRestService.getItemByCompetitionId(project.getCompetition())).thenReturn(restSuccess(publicContentItemResource));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                        project.getId(), kbOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        assertViewEligibilityDetails(eligibility, result, true, kbOrganisation.getName(), false,  false);

        Map<String, Object> model = result.getModelAndView().getModel();

        FinanceChecksProjectCostsViewModel projectCostViewModel = (FinanceChecksProjectCostsViewModel) model.get("model");

        assertNotNull(projectCostViewModel);

        assertThat(projectCostViewModel.getOrderedAccordionFinanceRowTypes(), containsInAnyOrder(expectedFinanceRowTypes.toArray()));
    }

    @Test
    public void testViewEligibilityNonFecKbOrg() throws Exception {

        List<FinanceRowType> expectedFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .filter(financeRowType -> financeRowType.isCost()
                        && !FinanceRowType.getFecSpecificFinanceRowTypes().contains(financeRowType))
                .collect(Collectors.toList());

        CompetitionResource competitionResource = newCompetitionResource()
                .withFundingType(fundingType)
                .withFinanceRowTypes(FinanceRowType.getKtpFinanceRowTypes())
                .build();

        ProjectResource project = newProjectResource()
                .withId(10L)
                .withName("Project1")
                .withApplication(application)
                .withCompetition(competitionResource.getId())
                .withProjectState(SETUP)
                .build();

        ProjectFinanceResource projectFinance = newProjectFinanceResource()
                .withFecEnabled(false)
                .withOrganisation(kbOrganisation.getId())
                .withGrantClaimPercentage(BigDecimal.valueOf(100))
                .withFinanceOrganisationDetails(asMap(
                        FinanceRowType.OTHER_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.ASSOCIATE_SALARY_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.CONSUMABLES, newDefaultCostCategory().build(),
                        FinanceRowType.ASSOCIATE_SUPPORT, newDefaultCostCategory().build(),
                        FinanceRowType.KNOWLEDGE_BASE, newDefaultCostCategory().build(),
                        FinanceRowType.ESTATE_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.KTP_TRAVEL, newDefaultCostCategory().build(),
                        FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT, newDefaultCostCategory().build(),
                        FinanceRowType.INDIRECT_COSTS, newDefaultCostCategory().build()))
                .build();

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility, project);

        PublicContentResource publicContentResource = newPublicContentResource().build();
        PublicContentItemResource publicContentItemResource = newPublicContentItemResource().withPublicContentResource(publicContentResource).build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getByApplicationId(application.getId())).thenReturn(project);
        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(kbOrganisation);
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(Collections.singletonList(projectFinance)));
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), kbOrganisation.getId())).thenReturn(eligibilityOverview);
        when(projectFinanceRestService.getFinanceTotals(project.getId())).thenReturn(restSuccess(Collections.emptyList()));
        when(grantOfferLetterService.getGrantOfferLetterState(project.getId())).thenReturn(serviceSuccess(grantOfferLetterStateResource));
        when(publicContentItemRestService.getItemByCompetitionId(project.getCompetition())).thenReturn(restSuccess(publicContentItemResource));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                        project.getId(), kbOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        assertViewEligibilityDetails(eligibility, result, true, kbOrganisation.getName(), false,  false);

        Map<String, Object> model = result.getModelAndView().getModel();

        FinanceChecksProjectCostsViewModel projectCostViewModel = (FinanceChecksProjectCostsViewModel) model.get("model");

        assertNotNull(projectCostViewModel);

        assertThat(projectCostViewModel.getOrderedAccordionFinanceRowTypes(), containsInAnyOrder(expectedFinanceRowTypes.toArray()));
    }

    private void setUpViewEligibilityMocking(EligibilityResource eligibility, ProjectResource project) {

        eligibility.setEligibilityApprovalDate(LocalDate.now());
        eligibility.setEligibilityApprovalUserFirstName("Lee");
        eligibility.setEligibilityApprovalUserLastName("Bowman");

        when(financeCheckRestService.getEligibility(project.getId(), industrialOrganisation.getId())).thenReturn(restSuccess(eligibility));
        when(financeCheckRestService.getEligibility(project.getId(), academicOrganisation.getId())).thenReturn(restSuccess(eligibility));
        when(financeCheckRestService.getEligibility(project.getId(), kbOrganisation.getId())).thenReturn(restSuccess(eligibility));
        when(projectFinanceChangesViewModelPopulator.getProjectFinanceChangesViewModel(anyBoolean(), any(), any())).thenReturn(new ProjectFinanceChangesViewModel());
    }

    private FinanceChecksEligibilityViewModel assertViewEligibilityDetails(EligibilityResource eligibility, MvcResult result, boolean expectedIsLeadPartnerOrganisation, String organisationName, boolean expectedIsUsingJesFinances, boolean isH2020) {

        Map<String, Object> model = result.getModelAndView().getModel();

        FinanceChecksEligibilityViewModel viewModel = (FinanceChecksEligibilityViewModel) model.get("summaryModel");

        assertEquals(expectedIsLeadPartnerOrganisation, viewModel.isLeadPartnerOrganisation());
        assertEquals(viewModel.isH2020(), isH2020);
        assertTrue(viewModel.getOrganisationName().equals(organisationName));
        assertTrue(viewModel.getProjectName().equals(project.getName()));

        assertTrue(viewModel.isApproved());
        assertEquals(eligibility.getEligibilityRagStatus(), viewModel.getEligibilityRagStatus());
        assertEquals(eligibility.getEligibilityApprovalDate(), viewModel.getApprovalDate());
        assertEquals(eligibility.getEligibilityApprovalUserFirstName(), viewModel.getApproverFirstName());
        assertEquals(eligibility.getEligibilityApprovalUserLastName(), viewModel.getApproverLastName());

        FinanceChecksEligibilityForm form = (FinanceChecksEligibilityForm) model.get("eligibilityForm");
        assertTrue(form.isConfirmEligibilityChecked());
        assertEquals(eligibility.getEligibilityRagStatus(), form.getEligibilityRagStatus());

        assertFalse(viewModel.isExternalView());
        assertEquals(expectedIsUsingJesFinances, viewModel.isUsingJesFinances());
        return viewModel;
    }
}
