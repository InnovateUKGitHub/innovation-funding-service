package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.transactional.FormInputResponseService;
import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.finance.transactional.FinanceRowCostsService;
import org.innovateuk.ifs.finance.transactional.FinanceService;
import org.innovateuk.ifs.finance.transactional.GrantClaimMaximumService;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Month;
import java.time.YearMonth;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.controller.OrganisationFinanceController.*;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.form.resource.FormInputType.FINANCIAL_OVERVIEW_ROW;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrganisationFinanceControllerTest extends BaseControllerMockMVCTest<OrganisationFinanceController> {

    @Mock
    private CompetitionService competitionService;
    @Mock
    private QuestionService questionService;
    @Mock
    private FormInputService formInputService;
    @Mock
    private FormInputResponseService formInputResponseService;
    @Mock
    private ApplicationService applicationService;
    @Mock
    private FinanceService financeService;
    @Mock
    private FinanceRowCostsService financeRowCostsService;
    @Mock
    private OrganisationService organisationService;
    @Mock
    private AuthenticationHelper authenticationHelper;
    @Mock
    private GrantClaimMaximumService grantClaimMaximumService;
    @Mock
    private SectionService sectionService;
    @Mock
    private UsersRolesService usersRolesService;
    @Mock
    private SectionStatusService sectionStatusService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
//        Mockito.reset(competitionService, questionService, formInputService, formInputResponseService, applicationService, financeService, financeRowCostsService);
    }

    @Override
    protected OrganisationFinanceController supplyControllerUnderTest() {
        return new OrganisationFinanceController(
                competitionService,
                questionService,
                formInputService,
                formInputResponseService,
                applicationService,
                financeService,
                financeRowCostsService,
                organisationService,
                authenticationHelper,
                grantClaimMaximumService,
                sectionService,
                usersRolesService,
                sectionStatusService
        );
    }

    @Test
    public void getOrganisationWithGrowthTable() throws Exception {
        long competitionId = 5;
        boolean stateAidAgreed = true;
        YearMonth financialYearEnd = YearMonth.of(2019, Month.JANUARY);
        OrganisationSize organisationSize = OrganisationSize.MEDIUM;

        long annualTurnover = 123;
        long annualProfits = 234;
        long annualExports = 456;
        long researchAndDevelopment = 789;
        long financialHeadCount = 11;

        Application application = newApplication().build();
        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionId)
                .withStateAidAgreed(stateAidAgreed)
                .build();
        Organisation organisation = newOrganisation().build();
        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withOrganisationSize(organisationSize)
                .build();
        Question financeOverviewQuestion = newQuestion().build();

        setupFormInputResponse(competitionId, application, organisation, FormInputType.FINANCIAL_YEAR_END, "01-2019");

        when(applicationService.getApplicationById(application.getId())).thenReturn(serviceSuccess(applicationResource));
        when(financeService.findApplicationFinanceByApplicationIdAndOrganisation(application.getId(), organisation.getId())).thenReturn(serviceSuccess(applicationFinanceResource));

        when(questionService.getQuestionByCompetitionIdAndFormInputType(competitionId,  FINANCIAL_OVERVIEW_ROW)).thenReturn(serviceSuccess(financeOverviewQuestion));

        setupFinanceOverviewResponseWithDescription(financeOverviewQuestion, application, organisation, String.valueOf(annualTurnover), ANNUAL_TURNOVER_FORM_INPUT_DESCRIPTION);
        setupFinanceOverviewResponseWithDescription(financeOverviewQuestion, application, organisation, String.valueOf(annualProfits), ANNUAL_PROFITS_FORM_INPUT_DESCRIPTION);
        setupFinanceOverviewResponseWithDescription(financeOverviewQuestion, application, organisation, String.valueOf(annualExports), ANNUAL_EXPORT_FORM_INPUT_DESCRIPTION);
        setupFinanceOverviewResponseWithDescription(financeOverviewQuestion, application, organisation, String.valueOf(researchAndDevelopment), OrganisationFinanceController.RESEARCH_AND_DEVELOPMENT_FORM_INPUT_DESCRIPTION);

        setupFormInputResponse(competitionId, application, organisation, FormInputType.FINANCIAL_STAFF_COUNT, String.valueOf(financialHeadCount));

        OrganisationFinancesWithGrowthTableResource expectedOrganisationFinances = new OrganisationFinancesWithGrowthTableResource();
        expectedOrganisationFinances.setOrganisationSize(organisationSize);
        expectedOrganisationFinances.setStateAidAgreed(stateAidAgreed);
        expectedOrganisationFinances.setFinancialYearEnd(financialYearEnd);

        expectedOrganisationFinances.setAnnualTurnoverAtLastFinancialYear(annualTurnover);
        expectedOrganisationFinances.setAnnualProfitsAtLastFinancialYear(annualProfits);
        expectedOrganisationFinances.setAnnualExportAtLastFinancialYear(annualExports);
        expectedOrganisationFinances.setResearchAndDevelopmentSpendAtLastFinancialYear(researchAndDevelopment);
        expectedOrganisationFinances.setHeadCountAtLastFinancialYear(financialHeadCount);

        mockMvc.perform(get("/application/{applicationId}/organisation/{organisationId}/finance/with-growth-table", application.getId(), organisation.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedOrganisationFinances)));
    }

    @Test
    public void getOrganisationWithoutGrowthTable() throws Exception {
        long competitionId = 5;
        OrganisationSize organisationSize = OrganisationSize.LARGE;
        long turnover = 123;
        long headcount = 13;
        boolean stateAidAgreed = true;
        Application application = newApplication().build();
        Organisation organisation = newOrganisation().build();
        ApplicationResource applicationResource = newApplicationResource()
                .withCompetition(competitionId)
                .withStateAidAgreed(stateAidAgreed)
                .build();
        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withOrganisationSize(organisationSize)
                .build();
        long annualTurnover = 123;

        when(applicationService.getApplicationById(application.getId())).thenReturn(serviceSuccess(applicationResource));
        when(financeService.findApplicationFinanceByApplicationIdAndOrganisation(application.getId(), organisation.getId())).thenReturn(serviceSuccess(applicationFinanceResource));
        setupFormInputResponse(competitionId, application, organisation, FormInputType.ORGANISATION_TURNOVER, String.valueOf(annualTurnover));
        setupFormInputResponse(competitionId, application, organisation, FormInputType.STAFF_COUNT, String.valueOf(headcount));

        OrganisationFinancesWithoutGrowthTableResource expectedOrganisationFinances = new OrganisationFinancesWithoutGrowthTableResource(organisationSize, turnover, headcount, stateAidAgreed);

        mockMvc.perform(get("/application/{applicationId}/organisation/{organisationId}/finance/without-growth-table", application.getId(), organisation.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expectedOrganisationFinances)));
    }

    @Test
    public void updateOrganisationWithGrowthTable() throws Exception {

        boolean stateAid = true;
        Competition competition = newCompetition().withStateAid(stateAid).build();
        CompetitionResource competitionResource = newCompetitionResource().build();
        Application application = newApplication().build();
        ApplicationResource applicationResource = newApplicationResource().withCompetition(competition.getId()).build();
        Organisation organisation = newOrganisation().build();
        OrganisationFinancesWithGrowthTableResource organisationFinancesWithGrowthTableResource = new OrganisationFinancesWithGrowthTableResource();
//        organisationFinancesWithGrowthTableResource.setOrganisationSize(OrganisationSize.MEDIUM); // new values
        User loggedInUser = newUser().build();
        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource().build();
//        applicationFinanceResource.setOrganisationSize(OrganisationSize.SMALL); // existing values

        when(applicationService.getApplicationById(application.getId())).thenReturn(serviceSuccess(applicationResource));
        when(authenticationHelper.getCurrentlyLoggedInUser()).thenReturn(serviceSuccess(loggedInUser));
        when(applicationService.getCompetitionByApplicationId(application.getId())).thenReturn(serviceSuccess(competitionResource));
        when(financeService.findApplicationFinanceByApplicationIdAndOrganisation(application.getId(), organisation.getId()))
                .thenReturn(serviceSuccess(applicationFinanceResource));


        foo(competition.getId(), FormInputType.FINANCIAL_YEAR_END, ANNUAL_TURNOVER_FORM_INPUT_DESCRIPTION);


        Question financialOverviewRowQuestion = newQuestion().build();
        when(questionService.getQuestionByCompetitionIdAndFormInputType(competition.getId(), FINANCIAL_OVERVIEW_ROW)).thenReturn(serviceSuccess(financialOverviewRowQuestion));
        List<FormInputResource> financialOverviewFormInputResponses = newFormInputResource()
                .withType(FINANCIAL_OVERVIEW_ROW)
                .withDescription(ANNUAL_TURNOVER_FORM_INPUT_DESCRIPTION, ANNUAL_PROFITS_FORM_INPUT_DESCRIPTION,
                        ANNUAL_EXPORT_FORM_INPUT_DESCRIPTION, RESEARCH_AND_DEVELOPMENT_FORM_INPUT_DESCRIPTION)
                .build(4);
        when(formInputService.findByQuestionId(financialOverviewRowQuestion.getId())).thenReturn(serviceSuccess(financialOverviewFormInputResponses));


        bar(competition.getId(), FormInputType.FINANCIAL_STAFF_COUNT);

        mockMvc.perform(post("/application/{applicationId}/organisation/{organisationId}/finance/with-growth-table", application.getId(), organisation.getId())
                .contentType(APPLICATION_JSON)
                .content(toJson(organisationFinancesWithGrowthTableResource)))
                .andExpect(status().isOk());

        // need to verify that the updates are called
//        verify(financeRowCostsService).updateApplicationFinance(eq(applicationFinanceResource.getId()), any(ApplicationFinanceResource.class));
    }

    private void foo(long competitionId, FormInputType formInputType, String description) {
        Question question = newQuestion().build();
        when(questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType)).thenReturn(serviceSuccess(question));
        List<FormInputResource> formInputResponses = newFormInputResource().withType(formInputType).withDescription(description).build(1);
        when(formInputService.findByQuestionId(question.getId())).thenReturn(serviceSuccess(formInputResponses));
    }

    private void bar(long competitionId, FormInputType formInputType) {
        Question question = newQuestion().build();
        when(questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType)).thenReturn(serviceSuccess(question));
        List<FormInputResource> formInputResponses = newFormInputResource().withType(formInputType).build(1);
        when(formInputService.findByQuestionId(question.getId())).thenReturn(serviceSuccess(formInputResponses));
    }


    @Test
    public void updateOrganisationWithoutGrowthTable() {
    }

    @Test
    public void isShowStateAidAgreement() {
    }

    @Test
    public void resetFundingAndMarkAsIncomplete() {
    }


    private void setupFormInputResponse(long competitionId, Application application, Organisation organisation, FormInputType formInputType, String value) {
        Question question = newQuestion().build();
        FormInputResponseResource formInputResponse = newFormInputResponseResource().withValue(value).build();

        when(questionService.getQuestionByCompetitionIdAndFormInputType(competitionId, formInputType)).thenReturn(serviceSuccess(question));
        when(formInputResponseService.findResponseByApplicationIdQuestionIdOrganisationIdAndFormInputType(
                application.getId(), question.getId(), organisation.getId(), formInputType)
        ).thenReturn(serviceSuccess(formInputResponse));
    }

    private void setupFinanceOverviewResponseWithDescription(Question financeOverviewQuestion, Application application, Organisation organisation, String value, String description) {
        FormInputResponseResource formInputResponse = newFormInputResponseResource().withValue(value).build();

        when(formInputResponseService.findResponseByApplicationIdQuestionIdOrganisationIdFormInputTypeAndDescription(
                application.getId(), financeOverviewQuestion.getId(), organisation.getId(), FINANCIAL_OVERVIEW_ROW, description)
        ).thenReturn(serviceSuccess(formInputResponse));
    }


}