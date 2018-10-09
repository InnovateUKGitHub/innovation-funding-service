package org.innovateuk.ifs.application.validation;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.validator.ValidatorTestUtil;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.handler.item.GrantClaimHandler;
import org.innovateuk.ifs.finance.handler.item.TravelCostHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.innovateuk.ifs.finance.resource.cost.TravelCost;
import org.innovateuk.ifs.finance.transactional.FinanceRowCostsService;
import org.innovateuk.ifs.finance.transactional.FinanceService;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceRowService;
import org.innovateuk.ifs.finance.validator.AcademicJesValidator;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.QuestionBuilder.newQuestion;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class ApplicationValidatorServiceImplTest extends BaseServiceUnitTest<ApplicationValidatorServiceImpl> {

    @Mock
    private FormInputResponseRepository formInputResponseRepository;

    @Mock
    private  ApplicationValidationUtil applicationValidationUtil;

    @Mock
    private FormInputRepository formInputRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private FinanceRowCostsService financeRowCostsService;

    @Mock
    private FinanceService financeService;

    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Mock
    private ProjectFinanceRowService projectFinanceRowService;

    @Mock
    private AcademicJesValidator academicJesValidator;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private UserRepository userRepository;

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
        when(formInputRepository.findById(formInputId)).thenReturn(Optional.of(newFormInput().withType(FormInputType.ASSESSOR_SCORE).build()));

        List<BindingResult> bindingResults = service.validateFormInputResponse(applicationId, formInputId);

        assertEquals(formInputResponses.size(), bindingResults.size());
        assertEquals(Arrays.asList(bindingResult1, bindingResult2), bindingResults);

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
        when(formInputRepository.findById(formInputId)).thenReturn(Optional.of(newFormInput().withType(FormInputType.ASSESSOR_SCORE).build()));
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
        when(formInputRepository.findById(formInputId)).thenReturn(Optional.of(newFormInput().withType(FormInputType.APPLICATION_DETAILS).build()));
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationValidationUtil.addValidation(eq(application), isA(Validator.class))).thenReturn(applicationBindingResult);

        List<BindingResult> bindingResults = service.validateFormInputResponse(applicationId, formInputId);

        assertEquals(formInputResponses.size() + 1, bindingResults.size());
        assertEquals(Arrays.asList(bindingResult1, bindingResult2, applicationBindingResult), bindingResults);

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
        BindingResult bindingResultExpected = ValidatorTestUtil.getBindingResult(formInputResponse);
        FormInput formInput = newFormInput().build();
        long formInputId = formInput.getId();

        when(formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(application.getId(), markedAsCompleteById, formInputId)).thenReturn(formInputResponse);
        when(applicationValidationUtil.validateResponse(formInputResponse, false)).thenReturn(bindingResultExpected);
        when(formInputRepository.findById(formInputId)).thenReturn(Optional.of(formInput));

        BindingResult actual = service.validateFormInputResponse(application, formInputId, markedAsCompleteById);

        assertEquals(bindingResultExpected, actual);

        verify(formInputResponseRepository, only()).findByApplicationIdAndUpdatedByIdAndFormInputId(application.getId(), markedAsCompleteById, formInputId);
        verify(applicationValidationUtil).validateResponse(formInputResponse, false);
        verify(formInputRepository, only()).findById(formInputId);
    }

    @Test
    public void validateFormInputResponseWhenIsResearchUser() {
        Application application = newApplication().build();
        long markedAsCompleteById = 4L;
        FormInputResponse formInputResponse = newFormInputResponse().build();
        BindingResult bindingResultExpected = ValidatorTestUtil.getBindingResult(formInputResponse);
        FormInput formInput = newFormInput().withType(FormInputType.FINANCE_UPLOAD).build();
        long formInputId = formInput.getId();
        OrganisationResource organisationResult = newOrganisationResource().withOrganisationType(OrganisationTypeEnum.RESEARCH.getId()).build();
        UserResource loggedInUser = newUserResource().build();
        setLoggedInUser(loggedInUser);
        User user = newUser().build();

        when(formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(application.getId(), markedAsCompleteById, formInputId)).thenReturn(formInputResponse);
        when(applicationValidationUtil.validateResponse(formInputResponse, false)).thenReturn(bindingResultExpected);
        when(formInputRepository.findById(formInputId)).thenReturn(Optional.of(formInput));
        when(applicationValidationUtil.addValidation(application, academicJesValidator)).thenReturn(bindingResultExpected);
        when(organisationService.getByUserAndApplicationId(user.getId(), application.getId())).thenReturn(ServiceResult.serviceSuccess(organisationResult));
        when(userRepository.findById(loggedInUser.getId())).thenReturn(Optional.of(user));
        BindingResult actual = service.validateFormInputResponse(application, formInputId, markedAsCompleteById);

        assertEquals(bindingResultExpected, actual);

        verify(formInputResponseRepository, only()).findByApplicationIdAndUpdatedByIdAndFormInputId(application.getId(), markedAsCompleteById, formInputId);
        verify(applicationValidationUtil).validateResponse(formInputResponse, false);
        verify(formInputRepository, only()).findById(formInputId);
        verify(applicationValidationUtil).addValidation(application, academicJesValidator);
        verify(organisationService, only()).getByUserAndApplicationId(user.getId(), application.getId());
        verify(userRepository, only()).findById(loggedInUser.getId());

    }

    @Test
    public void validateCostItem() {
        long applicationId = 1L;
        long organisationId = 999L;
        Question question = newQuestion().build();
        long questionId = question.getId();

        long markedAsCompleteById = 5L;

        List<ValidationMessages> validationMessages = emptyList();

        List<FinanceRowItem> costItems = Arrays.asList(new TravelCost(), new TravelCost());

        ProcessRole processRole = newProcessRole()
                .withOrganisationId(organisationId)
                .withId(1L)
                .build();

        ApplicationFinanceResource expectedFinances = newApplicationFinanceResource()
                .withId(1L)
                .withApplication(1L)
                .withFinanceFileEntry(1L)
                .build();

        List<ValidationMessages> expected = emptyList();


        when(processRoleRepository.findById(markedAsCompleteById)).thenReturn(Optional.of(processRole));
        when(financeService.financeDetails(applicationId, organisationId)).thenReturn(serviceSuccess(expectedFinances));
        when(financeRowCostsService.getCostItems(1L, questionId)).thenReturn(serviceSuccess(costItems));
        when(applicationValidationUtil.validateCostItem(costItems, question)).thenReturn(validationMessages);


        List<ValidationMessages> result = service.validateCostItem(applicationId, question, markedAsCompleteById);


        assertEquals(expected, result);


        verify(processRoleRepository).findById(markedAsCompleteById);
        verify(financeService).financeDetails(applicationId, organisationId);
        verify(financeRowCostsService, times(1)).getCostItems(1L, questionId);
        verify(applicationValidationUtil).validateCostItem(costItems, question);

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

        List<FinanceRowItem> costItems = Arrays.asList(new TravelCost(), new TravelCost());

        ProcessRole processRole = newProcessRole()
                .withOrganisationId(organisationId)
                .withId(1L)
                .build();

        ApplicationFinanceResource expectedFinances = newApplicationFinanceResource()
                .withId(1L)
                .withApplication(1L)
                .withFinanceFileEntry(1L)
                .build();

        when(processRoleRepository.findById(markedAsCompleteById)).thenReturn(Optional.of(processRole));
        when(financeService.financeDetails(applicationId, organisationId)).thenReturn(serviceSuccess(expectedFinances));
        when(financeRowCostsService.getCostItems(applicationFinanceId, questionId)).thenReturn(serviceSuccess(costItems));
        when(applicationValidationUtil.validateCostItem(costItems, question)).thenReturn(validationMessages);

        List<ValidationMessages> result = service.validateCostItem(applicationId, question, markedAsCompleteById);

        assertEquals(validationMessages, result);

        verify(processRoleRepository).findById(markedAsCompleteById);
        verify(financeService).financeDetails(applicationId, organisationId);
        verify(financeRowCostsService).getCostItems(applicationFinanceId, questionId);
        verify(applicationValidationUtil).validateCostItem(costItems, question);
    }

    @Test
    public void getCostHandler() {
        TravelCost travelCost = new TravelCost(1L, "transport", new BigDecimal("25.00"), 5);
        FinanceRowHandler expected = new TravelCostHandler();

        when(financeRowCostsService.getCostHandler(1L)).thenReturn(expected);

        FinanceRowHandler result = service.getCostHandler(travelCost);

        assertEquals(expected, result);

        verify(financeRowCostsService, only()).getCostHandler(1L);
    }

    @Test
    public void getProjectCostHandler() {
        GrantClaim grantClaim = new GrantClaim(1L, 20);
        FinanceRowHandler expected = new GrantClaimHandler();

        when(projectFinanceRowService.getCostHandler(grantClaim)).thenReturn(expected);

        FinanceRowHandler result = service.getProjectCostHandler(grantClaim);

        assertEquals(expected, result);

        verify(projectFinanceRowService, only()).getCostHandler(grantClaim);
    }


    @Override
    protected ApplicationValidatorServiceImpl supplyServiceUnderTest() {
        return new ApplicationValidatorServiceImpl();
    }
}