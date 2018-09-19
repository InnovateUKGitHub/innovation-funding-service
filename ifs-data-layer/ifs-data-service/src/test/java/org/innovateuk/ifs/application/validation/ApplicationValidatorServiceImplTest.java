package org.innovateuk.ifs.application.validation;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.FormInputResponseRepository;
import org.innovateuk.ifs.application.validator.ApplicationMarkAsCompleteValidator;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.handler.item.TravelCostHandler;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.TravelCost;
import org.innovateuk.ifs.finance.transactional.FinanceRowCostsService;
import org.innovateuk.ifs.finance.transactional.FinanceService;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.junit.Assert.*;
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
    private FinanceService financeService;

    @Mock
    private FinanceRowCostsService financeRowCostsService;

    @Test
    public void validateFormInputResponse() {
        long applicationId = 1L;
        long formInputId = 2L;

        List<FormInputResponse> formInputResponses = newFormInputResponse().withValue("response...").build(2);

        FormInputResponse formInputResponse1 = formInputResponses.get(0);
        FormInputResponse formInputResponse2 = formInputResponses.get(1);

        BindingResult bindingResult1 = new DataBinder(formInputResponse1).getBindingResult();
        BindingResult bindingResult2 = new DataBinder(formInputResponse2).getBindingResult();

        when(formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, formInputId)).thenReturn(formInputResponses);
        when(applicationValidationUtil.validateResponse(formInputResponse1, false)).thenReturn(bindingResult1);
        when(applicationValidationUtil.validateResponse(formInputResponse2, false)).thenReturn(bindingResult2);
        when(formInputRepository.findOne(formInputId)).thenReturn(newFormInput().withType(FormInputType.ASSESSOR_SCORE).build());

        List<BindingResult> bindingResults = service.validateFormInputResponse(applicationId, formInputId);

        assertEquals(formInputResponses.size(), bindingResults.size());
        assertEquals(Arrays.asList(bindingResult1, bindingResult2), bindingResults);
    }


    @Test
    public void validateFormInputResponse_emptyResponse() {
        long applicationId = 1L;
        long formInputId = 2L;

        List<FormInputResponse> formInputResponses = Collections.emptyList();

        FormInputResponse emptyResponse = new FormInputResponse();

        BindingResult bindingResultForEmptyResponse = new DataBinder(emptyResponse).getBindingResult();

        when(formInputResponseRepository.findByApplicationIdAndFormInputId(applicationId, formInputId)).thenReturn(formInputResponses);
        when(formInputRepository.findOne(formInputId)).thenReturn(newFormInput().withType(FormInputType.ASSESSOR_SCORE).build());
        when(applicationValidationUtil.validateResponse(isA(FormInputResponse.class), eq(false))).thenReturn(bindingResultForEmptyResponse);

        List<BindingResult> bindingResults = service.validateFormInputResponse(applicationId, formInputId);

        assertEquals(1, bindingResults.size());
        assertEquals(bindingResultForEmptyResponse, bindingResults.get(0));
    }


    @Test
    public void validateFormInputResponse_applicationDetails() {
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
        when(formInputRepository.findOne(formInputId)).thenReturn(newFormInput().withType(FormInputType.APPLICATION_DETAILS).build());
        when(applicationRepository.findOne(applicationId)).thenReturn(application);
        when(applicationValidationUtil.addValidation(eq(application), isA(ApplicationMarkAsCompleteValidator.class))).thenReturn(applicationBindingResult);

        List<BindingResult> bindingResults = service.validateFormInputResponse(applicationId, formInputId);

        assertEquals(formInputResponses.size() + 1, bindingResults.size());
        assertEquals(Arrays.asList(bindingResult1, bindingResult2, applicationBindingResult), bindingResults);
    }


    @Test
    public void validateFormInputResponseWithMarkedAsComplete() {
/*        Application application = new Application();
        Long formInputId = 1L;
        Long markedAsCompleteById = 2L;

        FormInputResponse formInputResponse = newFormInputResponse().withValue("response").build();

        when(formInputResponseRepository.findByApplicationIdAndUpdatedByIdAndFormInputId(application.getId(), markedAsCompleteById, formInputId)).thenReturn(formInputResponse);
        when(applicationValidationUtil.validateResponse(formInputResponse, false)).thenReturn();

        BindingResult bindingResult = service.validateFormInputResponse(application, formInputId, markedAsCompleteById);

        assertEquals(formInputResponse, bindingResult);*/
    }


    @Test
    public void validateCostItem() {
        Long applicationId = 1L;
        Long markedAsCompleteId = 2L;
        Question question = new Question();

        //List<ValidationMessages> validationMessages = newValidationMessages().build();

        when(financeService.financeDetails(applicationId));

        List<ValidationMessages> validationMessages = service.validateCostItem(applicationId, question, markedAsCompleteId);
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
    }

    @Test
    public void validateFileUploads() {

    }






    @Override
    protected ApplicationValidatorServiceImpl supplyServiceUnderTest() {
        return new ApplicationValidatorServiceImpl();
    }
}