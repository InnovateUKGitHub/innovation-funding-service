package org.innovateuk.ifs.application.validation;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.transactional.ApplicationProgressService;
import org.innovateuk.ifs.application.validator.ValidatorTestUtil;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionApplicationConfig;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.repository.FileEntryRepository;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.EmployeesAndTurnover;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.handler.item.GrantClaimPercentageHandler;
import org.innovateuk.ifs.finance.handler.item.TravelCostHandler;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.GrantClaimPercentage;
import org.innovateuk.ifs.finance.resource.cost.TravelCost;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceRowService;
import org.innovateuk.ifs.finance.transactional.ApplicationFinanceService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.finance.validator.FinanceValidationUtil;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.EmployeesAndTurnoverResourceBuilder.newEmployeesAndTurnoverResource;
import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.TRAVEL;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.procurement.milestone.builder.ApplicationProcurementMilestoneBuilder.newApplicationProcurementMilestone;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class ApplicationValidatorServiceImplTest extends BaseServiceUnitTest<ApplicationValidatorServiceImpl> {

    @Mock
    private FormInputResponseRepository formInputResponseRepository;

    @Mock
    private ApplicationValidationUtil applicationValidationUtil;

    @Mock
    private FinanceValidationUtil financeValidationUtil;

    @Mock
    private FormInputRepository formInputRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationFinanceRowService financeRowCostsService;

    @Mock
    private ApplicationFinanceService financeService;

    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Mock
    private ProjectFinanceRowService projectFinanceRowService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationProgressService applicationProgressService;

    @Mock
    private ApplicationFinanceHandler applicationFinanceHandler;

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepository;

    @Test
    public void validateFormInputResponse() {
        long applicationId = 1L;
        long formInputId = 2L;

        List<FormInputResponse> formInputResponses = newFormInputResponse().withValue("response...").build(2);

        FormInputResponse formInputResponse1 = formInputResponses.get(0);
        FormInputResponse formInputResponse2 = formInputResponses.get(1);

        BindingResult bindingResult1 =  ValidatorTestUtil.getBindingResult(formInputResponse1);
        BindingResult bindingResult2 =  ValidatorTestUtil.getBindingResult(formInputResponse2);

        when(formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, formInputId)).thenReturn(formInputResponses);
        when(applicationValidationUtil.validateResponse(formInputResponse1, false)).thenReturn(bindingResult1);
        when(applicationValidationUtil.validateResponse(formInputResponse2, false)).thenReturn(bindingResult2);
        when(formInputRepository.findById(formInputId)).thenReturn(Optional.of(newFormInput().withQuestion(newQuestion().withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION).build()).withType(FormInputType.ASSESSOR_SCORE).build()));

        List<BindingResult> bindingResults = service.validateFormInputResponse(applicationId, formInputId);

        assertEquals(formInputResponses.size(), bindingResults.size());
        assertEquals(asList(bindingResult1, bindingResult2), bindingResults);

        verify(formInputResponseRepository).findByApplicationIdAndFormInputId(applicationId, formInputId);
        verify(applicationValidationUtil).validateResponse(formInputResponse1, false);
        verify(applicationValidationUtil).validateResponse(formInputResponse2, false);
        verify(formInputRepository, times(1)).findById(formInputId);
    }


    @Test
    public void validateFormInputResponseWhenEmptyResponse() {
        long applicationId = 1L;
        long formInputId = 2L;

        List<FormInputResponse> formInputResponses = emptyList();

        FormInputResponse emptyResponse = new FormInputResponse();

        BindingResult bindingResultForEmptyResponse =  ValidatorTestUtil.getBindingResult(emptyResponse);

        when(formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, formInputId)).thenReturn(formInputResponses);
        when(formInputRepository.findById(formInputId)).thenReturn(Optional.of(newFormInput().withQuestion(newQuestion().withQuestionSetupType(QuestionSetupType.ASSESSED_QUESTION).build()).build()));
        when(applicationValidationUtil.validateResponse(isA(FormInputResponse.class), eq(false))).thenReturn(bindingResultForEmptyResponse);

        List<BindingResult> bindingResults = service.validateFormInputResponse(applicationId, formInputId);

        assertEquals(1, bindingResults.size());
        assertEquals(bindingResultForEmptyResponse, bindingResults.get(0));

        verify(formInputResponseRepository).findByApplicationIdAndFormInputId(applicationId, formInputId);
        verify(formInputRepository, times(2)).findById(formInputId);
        verify(applicationValidationUtil).validateResponse(isA(FormInputResponse.class), eq(false));
    }


    @Test
    public void validateFormInputResponseWhenApplicationDetails() {
        long applicationId = 1L;
        long formInputId = 2L;

        List<FormInputResponse> formInputResponses = newFormInputResponse().withValue("response...").build(2);

        FormInputResponse formInputResponse1 = formInputResponses.get(0);
        FormInputResponse formInputResponse2 = formInputResponses.get(1);

        BindingResult bindingResult1 = new DataBinder(formInputResponse1).getBindingResult();
        BindingResult bindingResult2 = new DataBinder(formInputResponse2).getBindingResult();
        Application application = newApplication().build();

        BindingResult applicationBindingResult = new DataBinder(application).getBindingResult();

        when(formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, formInputId)).thenReturn(formInputResponses);
        when(applicationValidationUtil.validateResponse(formInputResponse1, false)).thenReturn(bindingResult1);
        when(applicationValidationUtil.validateResponse(formInputResponse2, false)).thenReturn(bindingResult2);
        when(formInputRepository.findById(formInputId)).thenReturn(Optional.of(newFormInput().withQuestion(newQuestion().withQuestionSetupType(QuestionSetupType.APPLICATION_DETAILS).build()).build()));
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationValidationUtil.addValidation(eq(application), isA(Validator.class))).thenReturn(applicationBindingResult);

        List<BindingResult> bindingResults = service.validateFormInputResponse(applicationId, formInputId);

        assertEquals(formInputResponses.size() + 1, bindingResults.size());
        assertEquals(asList(bindingResult1, bindingResult2, applicationBindingResult), bindingResults);

        verify(formInputResponseRepository).findByApplicationIdAndFormInputId(applicationId, formInputId);
        verify(applicationValidationUtil).validateResponse(formInputResponse1, false);
        verify(applicationValidationUtil).validateResponse(formInputResponse2, false);
        verify(formInputRepository).findById(formInputId);
        verify(applicationRepository).findById(applicationId);
        verify(applicationValidationUtil).addValidation(eq(application), isA(Validator.class));

    }

    @Test
    public void validateFormInputResponseWhenMarkedAsComplete() {
        Application application = newApplication().build();
        long markedAsCompleteById = 4L;
        FormInputResponse formInputResponse = newFormInputResponse().build();
        BindingResult bindingResult = ValidatorTestUtil.getBindingResult(formInputResponse);
        FormInput formInput = newFormInput().build();
        long formInputId = formInput.getId();
        ValidationMessages expectedValidationMessage = ValidationMessages.fromBindingResult(bindingResult);

        when(formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(application.getId(), markedAsCompleteById, formInputId)).thenReturn(formInputResponse);
        when(applicationValidationUtil.validateResponse(formInputResponse, false)).thenReturn(bindingResult);
        when(formInputRepository.findById(formInputId)).thenReturn(Optional.of(formInput));

        ValidationMessages actual = service.validateFormInputResponse(application, formInputId, markedAsCompleteById);

        assertEquals(expectedValidationMessage, actual);

        verify(formInputResponseRepository, only()).findByApplicationIdAndUpdatedByIdAndFormInputId(application.getId(), markedAsCompleteById, formInputId);
        verify(applicationValidationUtil).validateResponse(formInputResponse, false);
    }

    @Test
    public void validateFormInputResponseWhenIsResearchUser() {
        Application application = newApplication().withCompetition(newCompetition().withIncludeJesForm(true).build()).build();
        long markedAsCompleteById = 4L;
        long organisationId = 999L;
        ProcessRole processRole = newProcessRole()
                .withOrganisationId(organisationId)
                .withId(markedAsCompleteById)
                .build();

        FormInputResponse formInputResponse = newFormInputResponse().build();
        OrganisationResource organisationResult = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.RESEARCH.getId()).build();
        UserResource loggedInUser = newUserResource().build();
        setLoggedInUser(loggedInUser);
        User user = newUser().build();
        ValidationMessages expectedValidationMessage = new ValidationMessages();
        expectedValidationMessage.addError(fieldError("jesFileUpload", null, "validation.application.jes.upload.required"));

        when(processRoleRepository.findById(markedAsCompleteById)).thenReturn(Optional.of(processRole));
        when(organisationService.findById(organisationId)).thenReturn(ServiceResult.serviceSuccess(organisationResult));
        when(userRepository.findById(loggedInUser.getId())).thenReturn(Optional.of(user));
        ValidationMessages actual = service.validateAcademicUpload(application, markedAsCompleteById);

        assertEquals(expectedValidationMessage, actual);
    }

    @Test
    public void validateCostItem() {
        long applicationId = 1L;
        long organisationId = 999L;
        Question question = newQuestion().build();
        long questionId = question.getId();

        long markedAsCompleteById = 5L;

        List<ValidationMessages> validationMessages = emptyList();

        ProcessRole processRole = newProcessRole()
                .withOrganisationId(organisationId)
                .withId(1L)
                .build();

        ApplicationFinanceResource expectedFinances = newApplicationFinanceResource()
                .withId(1L)
                .withApplication(1L)
                .withFinanceFileEntry(1L)
                .build();
        List<FinanceRowItem> costItems = asList(new TravelCost(expectedFinances.getId()), new TravelCost(expectedFinances.getId()));
        FinanceRowCostCategory costCategory = newDefaultCostCategory().withCosts(costItems).build();
        expectedFinances.setFinanceOrganisationDetails(asMap(TRAVEL, costCategory));

        List<ValidationMessages> expected = emptyList();

        when(processRoleRepository.findById(markedAsCompleteById)).thenReturn(Optional.of(processRole));
        when(financeService.financeDetails(applicationId, organisationId)).thenReturn(serviceSuccess(expectedFinances));
        when(financeValidationUtil.validateCostItem(TRAVEL, costCategory)).thenReturn(validationMessages);

        List<ValidationMessages> result = service.validateCostItem(applicationId, FinanceRowType.TRAVEL, markedAsCompleteById);

        assertEquals(expected, result);

        verify(processRoleRepository).findById(markedAsCompleteById);
        verify(financeService).financeDetails(applicationId, organisationId);
        verify(financeValidationUtil).validateCostItem(TRAVEL, costCategory);

    }

    @Test
    public void validateCostItemWhenMarkedAsCompleteByIdIsNull() {
        long applicationId = 1L;
        long organisationId = 999L;
        Question question = newQuestion().build();
        long questionId = question.getId();
        long applicationFinanceId = 1L;

        Long markedAsCompleteById = null;

        List<ValidationMessages> validationMessages = emptyList();

        ProcessRole processRole = newProcessRole()
                .withOrganisationId(organisationId)
                .withId(1L)
                .build();

        ApplicationFinanceResource expectedFinances = newApplicationFinanceResource()
                .withId(1L)
                .withApplication(1L)
                .withFinanceFileEntry(1L)
                .build();

        List<FinanceRowItem> costItems = asList(new TravelCost(expectedFinances.getId()), new TravelCost(expectedFinances.getId()));
        FinanceRowCostCategory costCategory = newDefaultCostCategory().withCosts(costItems).build();
        expectedFinances.setFinanceOrganisationDetails(asMap(TRAVEL, costCategory));

        when(processRoleRepository.findById(markedAsCompleteById)).thenReturn(Optional.of(processRole));
        when(financeService.financeDetails(applicationId, organisationId)).thenReturn(serviceSuccess(expectedFinances));
        when(financeValidationUtil.validateCostItem(TRAVEL, costCategory)).thenReturn(validationMessages);

        List<ValidationMessages> result = service.validateCostItem(applicationId, FinanceRowType.TRAVEL, markedAsCompleteById);

        assertEquals(validationMessages, result);

        verify(processRoleRepository).findById(markedAsCompleteById);
        verify(financeService).financeDetails(applicationId, organisationId);
        verify(financeValidationUtil).validateCostItem(TRAVEL, costCategory);
    }

    @Test
    public void getCostHandler() {
        TravelCost travelCost = new TravelCost(1L, "transport", new BigDecimal("25.00"), 5, 1L);
        FinanceRowHandler expected = new TravelCostHandler();

        when(financeRowCostsService.getCostHandler(1L)).thenReturn(expected);

        FinanceRowHandler result = service.getCostHandler(travelCost);

        assertEquals(expected, result);

        verify(financeRowCostsService, only()).getCostHandler(1L);
    }

    @Test
    public void getProjectCostHandler() {
        GrantClaimPercentage grantClaim = new GrantClaimPercentage(1L, BigDecimal.valueOf(20), 1L);
        FinanceRowHandler expected = new GrantClaimPercentageHandler();

        when(projectFinanceRowService.getCostHandler(grantClaim)).thenReturn(expected);

        FinanceRowHandler result = service.getProjectCostHandler(grantClaim);

        assertEquals(expected, result);

        verify(projectFinanceRowService, only()).getCostHandler(grantClaim);
    }

    @Test
    public void isApplicationComplete() {
        long applicationId = 1l;
        long competitionId = 2l;

        CompetitionApplicationConfig competitionApplicationConfig = new CompetitionApplicationConfig();

        Competition competition = newCompetition()
                .withId(competitionId)
                .withMaxResearchRatio(100)
                .withCompetitionApplicationConfig(competitionApplicationConfig)
                .withCollaborationLevel(CollaborationLevel.SINGLE).build();

        Application application = newApplication()
                .withCompetition(competition)
                .withId(applicationId)
                .build();

        List<ApplicationFinanceResource> applicationFinanceResources = newApplicationFinanceResource()
                .withIndustrialCosts()
                .withGrantClaimAmount(new BigDecimal(999))
                .build(1);

        when(applicationProgressService.getApplicationProgress(applicationId)).thenReturn(serviceSuccess(BigDecimal.valueOf(100)));
        when(applicationFinanceHandler.getApplicationTotals(applicationId)).thenReturn(applicationFinanceResources);
        when(applicationFinanceHandler.getResearchParticipationPercentage(applicationId)).thenReturn(new BigDecimal("29"));

        boolean result = service.isApplicationComplete(application);
        assertTrue(result);
    }

    @Test
    public void applicationNotReadyComplete_ResearchParticipationTooHigh() {
        long applicationId = 1l;
        long competitionId = 2l;

        CompetitionApplicationConfig competitionApplicationConfig = new CompetitionApplicationConfig();

        Competition competition = newCompetition()
                .withId(competitionId)
                .withMaxResearchRatio(50)
                .withCompetitionApplicationConfig(competitionApplicationConfig)
                .withCollaborationLevel(CollaborationLevel.SINGLE).build();

        Application application = newApplication()
                .withCompetition(competition)
                .withId(applicationId)
                .build();

        List<ApplicationFinanceResource> applicationFinanceResources = newApplicationFinanceResource()
                .withIndustrialCosts()
                .withGrantClaimAmount(new BigDecimal(999))
                .build(1);

        when(applicationProgressService.getApplicationProgress(applicationId)).thenReturn(serviceSuccess(BigDecimal.valueOf(100)));
        when(applicationFinanceHandler.getApplicationTotals(applicationId)).thenReturn(applicationFinanceResources);
        when(applicationFinanceHandler.getResearchParticipationPercentage(applicationId)).thenReturn(new BigDecimal("60"));

        boolean result = service.isApplicationComplete(application);
        assertFalse(result);
    }

    @Test
    public void applicationNotReadyComplete_ProgressNotComplete() {
        long applicationId = 1l;
        long competitionId = 2l;

        CompetitionApplicationConfig competitionApplicationConfig = new CompetitionApplicationConfig();

        Competition competition = newCompetition()
                .withId(competitionId)
                .withMaxResearchRatio(100)
                .withCompetitionApplicationConfig(competitionApplicationConfig)
                .withCollaborationLevel(CollaborationLevel.SINGLE).build();

        Application application = newApplication()
                .withCompetition(competition)
                .withId(applicationId)
                .build();

        List<ApplicationFinanceResource> applicationFinanceResources = newApplicationFinanceResource()
                .withIndustrialCosts()
                .withGrantClaimAmount(new BigDecimal(999))
                .build(1);

        when(applicationProgressService.getApplicationProgress(applicationId)).thenReturn(serviceSuccess(BigDecimal.valueOf(7)));
        when(applicationFinanceHandler.getApplicationTotals(applicationId)).thenReturn(applicationFinanceResources);
        when(applicationFinanceHandler.getResearchParticipationPercentage(applicationId)).thenReturn(new BigDecimal("29"));

        boolean result = service.isApplicationComplete(application);
        assertFalse(result);
    }

    @Test
    public void validateFECCertificateUploadedBeforeMarkAsComplete() {
        long markedAsCompleteById = 4L;
        long organisationId = 7L;
        long applicationId = 1L;

        Organisation organisation = newOrganisation().withId(organisationId).build();
        Application application = newApplication().withId(applicationId).withCompetition(newCompetition().build())
                .withApplicationFinancesList(asList(
                        newApplicationFinance()
                                .withOrganisation(organisation)
                                .withFecFileEntry(null)
                                .withFecEnabled(true)
                                .build())).build();
        ProcessRole processRole = newProcessRole()
                .withOrganisationId(organisationId)
                .withId(markedAsCompleteById)
                .build();

        OrganisationResource organisationResult = newOrganisationResource().withId(organisationId).build();
        UserResource loggedInUser = newUserResource().build();
        setLoggedInUser(loggedInUser);
        User user = newUser().build();
        ValidationMessages expectedValidationMessage = new ValidationMessages();
        expectedValidationMessage.addError(fieldError("fecCertificateFileUpload", null, "validation.application.fec.upload.required"));

        when(processRoleRepository.findById(markedAsCompleteById)).thenReturn(Optional.of(processRole));
        when(organisationService.findById(organisationId)).thenReturn(ServiceResult.serviceSuccess(organisationResult));
        when(userRepository.findById(loggedInUser.getId())).thenReturn(Optional.of(user));
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationFinanceRepository.findByApplicationIdAndOrganisationId(application.getId(), organisation.getId()))
                .thenReturn(Optional.of(application.getApplicationFinances().get(0)));

        ValidationMessages actual = service.validateFECCertificateUpload(application, markedAsCompleteById);

        assertEquals(expectedValidationMessage, actual);
    }

    @Override
    protected ApplicationValidatorServiceImpl supplyServiceUnderTest() {
        return new ApplicationValidatorServiceImpl();
    }
}