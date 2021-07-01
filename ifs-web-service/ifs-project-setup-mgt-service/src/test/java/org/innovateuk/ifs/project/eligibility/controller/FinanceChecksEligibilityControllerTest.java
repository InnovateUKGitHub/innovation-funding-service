package org.innovateuk.ifs.project.eligibility.controller;

import org.innovateuk.ifs.AbstractAsyncWaitMockMVCTest;
import org.innovateuk.ifs.application.finance.service.FinanceService;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.application.forms.academiccosts.form.AcademicCostForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.LabourRowForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.form.YourProjectCostsForm;
import org.innovateuk.ifs.application.forms.sections.yourprojectcosts.validator.YourProjectCostsFormValidator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionTypeEnum;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
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
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.ELIGIBILITY_HAS_ALREADY_BEEN_APPROVED;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceChecksEligibilityControllerTest extends AbstractAsyncWaitMockMVCTest<FinanceChecksEligibilityController> {

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

    private OrganisationResource industrialOrganisation;

    private OrganisationResource academicOrganisation;

    private OrganisationResource kbOrganisation;

    private CompetitionResource competitionResource = newCompetitionResource()
            .withFundingType(FundingType.GRANT)
            .withIncludeJesForm(true)
            .build();

    private ApplicationResource application = newApplicationResource().withId(123L).build();

    private ProjectResource project = newProjectResource()
            .withId(1L)
            .withName("Project1")
            .withApplication(application)
            .withCompetition(competitionResource.getId())
            .withProjectState(SETUP)
            .build();

    private FinanceCheckEligibilityResource eligibilityOverview = newFinanceCheckEligibilityResource().build();

    private GrantOfferLetterStateResource grantOfferLetterStateResource = GrantOfferLetterStateResource.stateInformationForPartnersView(GrantOfferLetterState.PENDING, null);

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

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getByApplicationId(application.getId())).thenReturn(project);
        when(organisationRestService.getOrganisationById(industrialOrganisation.getId())).thenReturn(restSuccess(industrialOrganisation));
        when(organisationRestService.getOrganisationById(academicOrganisation.getId())).thenReturn(restSuccess(academicOrganisation));
        when(organisationRestService.getOrganisationById(kbOrganisation.getId())).thenReturn(restSuccess(kbOrganisation));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), industrialOrganisation.getId())).thenReturn(eligibilityOverview);
        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));

        when(projectFinanceRestService.getFinanceTotals(project.getId())).thenReturn(restSuccess(Collections.emptyList()));
        when(grantOfferLetterService.getGrantOfferLetterState(project.getId())).thenReturn(serviceSuccess(grantOfferLetterStateResource));
    }

    @Test
    public void testViewEligibilityLeadOrg() throws Exception {

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility, project);

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                project.getId(), industrialOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        assertViewEligibilityDetails(eligibility, result, true, industrialOrganisation.getName(), false,  false);

    }

    @Test
    public void testViewEligibilityLeadOrgH2020() throws Exception {

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility, project);

        CompetitionResource h2020Comp = newCompetitionResource().withCompetitionTypeEnum(CompetitionTypeEnum.HORIZON_2020).build();

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(competitionRestService.getCompetitionById(anyLong())).thenReturn(restSuccess(h2020Comp));
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                                               project.getId(), industrialOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        assertViewEligibilityDetails(eligibility, result, true, industrialOrganisation.getName(), false,  true);

    }

    @Test
    public void testViewEligibilityNonLeadOrg() throws Exception {

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility, project);

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(academicOrganisation);
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                project.getId(), industrialOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        assertViewEligibilityDetails(eligibility, result, false, industrialOrganisation.getName(), false,  false);

    }

    @Test
    public void testViewEligibilityLeadOrgIsAcademic() throws Exception {

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility, project);

        AcademicCostForm academicCostForm = new AcademicCostForm();
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(academicOrganisation);
        when(projectAcademicCostFormPopulator.populate(any(), eq(project.getId()), eq(academicOrganisation.getId()))).thenReturn(academicCostForm);

        ApplicationFinanceResource appFinanceResource = newApplicationFinanceResource().withFinanceFileEntry(123L).build();
        FileEntryResource jesFile = newFileEntryResource().withId(987L).withName("Jes1").build();
        when(applicationFinanceRestService.getFinanceDetails(project.getApplication(), academicOrganisation.getId())).thenReturn(restSuccess(appFinanceResource));
        when(financeService.getFinanceEntry(123L)).thenReturn(restSuccess(jesFile));
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility",
                project.getId(), academicOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("summaryModel")).
                andExpect(model().attribute("academicCostForm", academicCostForm)).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        assertViewEligibilityDetails(eligibility, result, true, academicOrganisation.getName(), true, false);
    }

    @Test
    public void testViewEligibilityLeadOrgIsAcademicEditFinances() throws Exception {

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility, project);

        AcademicCostForm academicCostForm = new AcademicCostForm();
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(academicOrganisation);
        when(projectAcademicCostFormPopulator.populate(any(), eq(project.getId()), eq(academicOrganisation.getId()))).thenReturn(academicCostForm);

        ApplicationFinanceResource appFinanceResource = newApplicationFinanceResource().withFinanceFileEntry(123L).build();
        FileEntryResource jesFile = newFileEntryResource().withId(987L).withName("Jes1").build();
        when(applicationFinanceRestService.getFinanceDetails(project.getApplication(), academicOrganisation.getId())).thenReturn(restSuccess(appFinanceResource));
        when(financeService.getFinanceEntry(123L)).thenReturn(restSuccess(jesFile));
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility?editAcademicFinances=true",
                project.getId(), academicOrganisation.getId())).
                andExpect(status().isOk()).
                andExpect(model().attributeExists("summaryModel")).
                andExpect(model().attribute("academicCostForm", academicCostForm)).
                andExpect(view().name("project/financecheck/eligibility")).
                andReturn();

        FinanceChecksEligibilityViewModel viewModel = assertViewEligibilityDetails(eligibility, result, true, academicOrganisation.getName(), true, false);
        assertTrue(viewModel.isCanEditAcademicFinances());
    }

    @Test
    public void testViewEligibilityFecKbOrg() throws Exception {

        List<FinanceRowType> expectedFinanceRowTypes = FinanceRowType.getKtpFinanceRowTypes().stream()
                .filter(financeRowType -> financeRowType.isCost()
                        && !FinanceRowType.getNonFecSpecificFinanceRowTypes().contains(financeRowType))
                .collect(Collectors.toList());

        CompetitionResource competitionResource = newCompetitionResource()
                .withFundingType(FundingType.KTP)
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

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getByApplicationId(application.getId())).thenReturn(project);
        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(kbOrganisation);
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(Collections.singletonList(projectFinance)));
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), kbOrganisation.getId())).thenReturn(eligibilityOverview);
        when(projectFinanceRestService.getFinanceTotals(project.getId())).thenReturn(restSuccess(Collections.emptyList()));
        when(grantOfferLetterService.getGrantOfferLetterState(project.getId())).thenReturn(serviceSuccess(grantOfferLetterStateResource));

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
                .withFundingType(FundingType.KTP)
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

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getByApplicationId(application.getId())).thenReturn(project);
        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(kbOrganisation);
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(Collections.singletonList(projectFinance)));
        when(financeCheckServiceMock.getFinanceCheckEligibilityDetails(project.getId(), kbOrganisation.getId())).thenReturn(eligibilityOverview);
        when(projectFinanceRestService.getFinanceTotals(project.getId())).thenReturn(restSuccess(Collections.emptyList()));
        when(grantOfferLetterService.getGrantOfferLetterState(project.getId())).thenReturn(serviceSuccess(grantOfferLetterStateResource));

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

    @Test
    public void testConfirmEligibilityWhenConfirmEligibilityNotChecked() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(financeCheckRestService.saveEligibility(projectId, organisationId, EligibilityState.APPROVED, EligibilityRagStatus.UNSET)).
                thenReturn(restSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("confirm-eligibility", "").
                        param("confirmEligibilityChecked", "false").
                        param("eligibilityRagStatus", "RED")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility"));

        verify(financeCheckRestService).saveEligibility(projectId, organisationId, EligibilityState.APPROVED, EligibilityRagStatus.UNSET);

    }

    @Test
    public void testConfirmEligibilityWhenConfirmEligibilityChecked() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(financeCheckRestService.saveEligibility(projectId, organisationId, EligibilityState.APPROVED, EligibilityRagStatus.RED)).
                thenReturn(restSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("confirm-eligibility", "").
                        param("confirmEligibilityChecked", "true").
                        param("eligibilityRagStatus", "RED")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility"));

        verify(financeCheckRestService).saveEligibility(projectId, organisationId, EligibilityState.APPROVED, EligibilityRagStatus.RED);

    }

    @Test
    public void testConfirmEligibilityWhenSaveEligibilityReturnsFailure() throws Exception {

        when(financeCheckRestService.saveEligibility(project.getId(), industrialOrganisation.getId(), EligibilityState.APPROVED, EligibilityRagStatus.RED)).
                thenReturn(restFailure(ELIGIBILITY_HAS_ALREADY_BEEN_APPROVED));
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(emptyList()));

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility, project);

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", project.getId(), industrialOrganisation.getId()).
                        param("confirm-eligibility", "").
                        param("confirmEligibilityChecked", "true").
                        param("eligibilityRagStatus", "RED")).
                andExpect(status().isOk()).
                andExpect(view().name("project/financecheck/eligibility"));

        verify(financeCheckRestService).saveEligibility(project.getId(), industrialOrganisation.getId(), EligibilityState.APPROVED, EligibilityRagStatus.RED);

    }

    @Test
    public void testConfirmEligibilitySuccess() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(financeCheckRestService.saveEligibility(projectId, organisationId, EligibilityState.APPROVED, EligibilityRagStatus.GREEN)).
                thenReturn(restSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("confirm-eligibility", "").
                        param("confirmEligibilityChecked", "true").
                        param("eligibilityRagStatus", "GREEN")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility"));

        verify(financeCheckRestService).saveEligibility(projectId, organisationId, EligibilityState.APPROVED, EligibilityRagStatus.GREEN);

    }

    @Test
    public void testResetEligibilitySuccess() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(financeCheckRestService.resetEligibility(projectId, organisationId, "something")).
                thenReturn(restSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("reset-eligibility", "").
                        param("retractionReason", "something")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check/organisation/" + organisationId + "/eligibility"));

        verify(financeCheckRestService).resetEligibility(projectId, organisationId, "something");
    }

    @Test
    public void testResetEligibilityWithoutRetractionReason() throws Exception {

        long projectId = project.getId();
        long organisationId = industrialOrganisation.getId();

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility, project);

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(emptyList()));

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("reset-eligibility", "").
                        param("retractionReason", "")).
                andExpect(status().isOk()).
                andExpect(model().attributeHasFieldErrors("resetForm", "retractionReason")).
                andExpect(view().name("project/financecheck/eligibility"));
    }

    @Test
    public void testSaveAndContinueWhenConfirmEligibilityNotChecked() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(financeCheckRestService.saveEligibility(projectId, organisationId, EligibilityState.REVIEW, EligibilityRagStatus.UNSET)).
                thenReturn(restSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("save-and-continue", "").
                        param("confirmEligibilityChecked", "false").
                        param("eligibilityRagStatus", "RED")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check"));

        verify(financeCheckRestService).saveEligibility(projectId, organisationId, EligibilityState.REVIEW, EligibilityRagStatus.UNSET);

    }

    @Test
    public void testSaveAndContinueWhenConfirmEligibilityChecked() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(financeCheckRestService.saveEligibility(projectId, organisationId, EligibilityState.REVIEW, EligibilityRagStatus.RED)).
                thenReturn(restSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("save-and-continue", "").
                        param("confirmEligibilityChecked", "true").
                        param("eligibilityRagStatus", "RED")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check"));

        verify(financeCheckRestService).saveEligibility(projectId, organisationId, EligibilityState.REVIEW, EligibilityRagStatus.RED);

    }

    @Test
    public void testSaveAndContinueWhenSaveEligibilityReturnsFailure() throws Exception {

        when(financeCheckRestService.saveEligibility(project.getId(), industrialOrganisation.getId(), EligibilityState.REVIEW, EligibilityRagStatus.RED)).
                thenReturn(restFailure(ELIGIBILITY_HAS_ALREADY_BEEN_APPROVED));
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(emptyList()));

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.APPROVED, EligibilityRagStatus.GREEN);
        setUpViewEligibilityMocking(eligibility, project);

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", project.getId(), industrialOrganisation.getId()).
                        param("save-and-continue", "").
                        param("confirmEligibilityChecked", "true").
                        param("eligibilityRagStatus", "RED")).
                andExpect(status().isOk()).
                andExpect(view().name("project/financecheck/eligibility"));

        verify(financeCheckRestService).saveEligibility(project.getId(), industrialOrganisation.getId(), EligibilityState.REVIEW, EligibilityRagStatus.RED);

    }

    @Test
    public void testSaveAndContinueSuccess() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;

        when(financeCheckRestService.saveEligibility(projectId, organisationId, EligibilityState.REVIEW, EligibilityRagStatus.GREEN)).
                thenReturn(restSuccess());

        mockMvc.perform(
                post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                        param("save-and-continue", "").
                        param("confirmEligibilityChecked", "true").
                        param("eligibilityRagStatus", "GREEN")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check"));

        verify(financeCheckRestService).saveEligibility(projectId, organisationId, EligibilityState.REVIEW, EligibilityRagStatus.GREEN);

    }

    @Test
    public void ajaxRemoveRow() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;
        Long costId = 3L;

        mockMvc.perform(post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility/remove-row/{rowId}",
                projectId, organisationId, costId))
                .andExpect(status().isOk());

        verify(yourProjectCostsSaver).removeFinanceRow(String.valueOf(costId));
    }

    @Test
    public void ajaxAddRow() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;
        String rowId = "123";
        LabourRowForm row = new LabourRowForm();
        row.setCostId(Long.valueOf(rowId));
        FinanceRowType type = FinanceRowType.LABOUR;

        doAnswer((invocation) -> {
            YourProjectCostsForm form = (YourProjectCostsForm) invocation.getArguments()[0];
            form.getLabour().getRows().put(rowId, row);
            return form.getLabour().getRows().entrySet().iterator().next();
        }).when(yourProjectCostsSaver).addRowForm(any(YourProjectCostsForm.class), eq(type));

        mockMvc.perform(post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility/add-row/{type}",
                projectId, organisationId, type))
                .andExpect(view().name("application/your-project-costs-fragments :: ajax_labour_row"))
                .andExpect(model().attribute("row", row))
                .andExpect(model().attribute("id", rowId))
                .andExpect(status().isOk());

        verify(yourProjectCostsSaver).addRowForm(any(YourProjectCostsForm.class), eq(type));
    }

    @Test
    public void testViewEligibilityEditFinances() throws Exception {

        EligibilityResource eligibility = new EligibilityResource(EligibilityState.REVIEW, EligibilityRagStatus.UNSET);
        setUpViewEligibilityMocking(eligibility, project);

        when(projectService.getLeadOrganisation(project.getId())).thenReturn(industrialOrganisation);
        when(projectFinanceRestService.getProjectFinances(project.getId())).thenReturn(restSuccess(emptyList()));

        mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility?editProjectCosts=true",
                project.getId(), industrialOrganisation.getId())).
                andExpect(model().attributeExists("model")).
                andExpect(view().name("project/financecheck/eligibility")).
                andExpect(status().isOk());
    }

    @Test
    public void testProjectFinanceFormSubmit() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;

        ProjectFinanceResource projectFinance = newProjectFinanceResource()
                .withFecEnabled(false)
                .withOrganisation(organisationId)
                .withGrantClaimPercentage(BigDecimal.valueOf(100))
                .withFinanceOrganisationDetails(asMap(
                        FinanceRowType.LABOUR, newDefaultCostCategory().build(),
                        FinanceRowType.OVERHEADS, newDefaultCostCategory().build(),
                        FinanceRowType.MATERIALS, newDefaultCostCategory().build(),
                        FinanceRowType.CAPITAL_USAGE, newDefaultCostCategory().build(),
                        FinanceRowType.SUBCONTRACTING_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.TRAVEL, newDefaultCostCategory().build(),
                        FinanceRowType.OTHER_COSTS, newDefaultCostCategory().build(),
                        FinanceRowType.YOUR_FINANCE, newDefaultCostCategory().build(),
                        FinanceRowType.FINANCE, newDefaultCostCategory().build(),
                        FinanceRowType.OTHER_FUNDING, newDefaultCostCategory().build()))
                        .build();

        CompetitionResource competition = newCompetitionResource()
                .withId(123L)
                .withFinanceRowTypes(FinanceRowType.getNonFecSpecificFinanceRowTypes())
                .build();

        when(projectFinanceRestService.getProjectFinance(projectId, organisationId)).thenReturn(restSuccess(projectFinance));
        when(competitionRestService.getCompetitionForProject(projectId)).thenReturn(restSuccess(competition));

        List<FinanceRowType> financeRowTypes = competition.getFinanceRowTypesByFinance(Optional.of(projectFinance))
                .stream()
                .filter(FinanceRowType::isAppearsInProjectCostsAccordion)
                .collect(Collectors.toList());

        financeRowTypes.forEach(type -> yourProjectCostsFormValidator.validateType(isA(YourProjectCostsForm.class), eq(type), any(ValidationHandler.class)));
        when(yourProjectCostsSaver.save(isA(YourProjectCostsForm.class), eq(projectId), eq(industrialOrganisation), eq(new ValidationMessages()))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility", projectId, organisationId).
                param("save-eligibility", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/finance-check/organisation/" + 2 +"/eligibility"));
    }

    @Test
    public void testEligibilityChanges() throws Exception {
        Long projectId = 1L;
        Long organisationId = 2L;
        ProjectFinanceChangesViewModel viewModel = mock(ProjectFinanceChangesViewModel.class);
        when(projectFinanceChangesViewModelPopulator.getProjectFinanceChangesViewModel(true, project, industrialOrganisation)).thenReturn(viewModel);

        mockMvc.perform(get("/project/{projectId}/finance-check/organisation/{organisationId}/eligibility/changes", projectId, organisationId)).
                andExpect(status().isOk()).
                andExpect(view().name("project/financecheck/eligibility-changes")).
                andExpect(model().attribute("model", viewModel)).
                andReturn();
    }

    @Override
    protected FinanceChecksEligibilityController supplyControllerUnderTest() {
        return new FinanceChecksEligibilityController();
    }
}