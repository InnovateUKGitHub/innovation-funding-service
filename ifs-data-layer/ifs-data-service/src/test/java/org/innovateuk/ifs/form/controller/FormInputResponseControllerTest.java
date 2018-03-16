package org.innovateuk.ifs.form.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.FormInputResponseController;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.application.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FormInputResponseControllerTest extends BaseControllerMockMVCTest<FormInputResponseController> {

    @Override
    protected FormInputResponseController supplyControllerUnderTest() {
        return new FormInputResponseController();
    }

    @Test
    public void findResponsesByApplication() throws Exception {

        long applicationId = 123L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputResponseService.findResponsesByApplication(applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/findResponsesByApplication/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(expected)));

        verify(formInputResponseService, only()).findResponsesByApplication(applicationId);
    }

    @Test
    public void findByFormInputIdAndApplication() throws Exception {
        long applicationId = 1L;
        long formInputId = 2L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputResponseService.findResponsesByFormInputIdAndApplicationId(formInputId, applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/findResponseByFormInputIdAndApplicationId/{formInputId}/{applicationId}", formInputId, applicationId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(expected)));

        verify(formInputResponseService, only()).findResponsesByFormInputIdAndApplicationId(formInputId, applicationId);
    }

    @Test
    public void findByApplicationIdAndQuestionName() throws Exception {
        long applicationId = 1L;
        String questionName = "question";

        FormInputResponseResource expected = newFormInputResponseResource().build();

        when(formInputResponseService.findResponseByApplicationIdAndQuestionName(applicationId, questionName)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/findByApplicationIdAndQuestionName/{applicationId}/{questionName}", applicationId, questionName))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(expected)));

        verify(formInputResponseService, only()).findResponseByApplicationIdAndQuestionName(applicationId, questionName);
    }

    @Test
    public void findByApplicationIdAndQuestionId() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputResponseService.findResponseByApplicationIdAndQuestionId(applicationId, questionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/findByApplicationIdAndQuestionId/{applicationId}/{questionId}", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(expected)));

        verify(formInputResponseService, only()).findResponseByApplicationIdAndQuestionId(applicationId, questionId);
    }

    @Test
    public void testSaveQuestionResponse() throws Exception {
        long appId = 456L;
        long userId = 123L;
        long formInputId = 789L;

        FormInputResponse formInputResponse = newFormInputResponse().build();
        BindingResult bindingResult = new DataBinder(formInputResponse).getBindingResult();
        ValidationMessages expected = new ValidationMessages(bindingResult);
        when(validationUtilMock.validateResponse(formInputResponse, false)).thenReturn(bindingResult);

        when(formInputResponseService.saveQuestionResponse(argThat(new ArgumentMatcher<FormInputResponseCommand>() {
            @Override
            public boolean matches(Object argument) {
                FormInputResponseCommand firArgument = (FormInputResponseCommand) argument;
                assertEquals(appId, firArgument.getApplicationId());
                assertEquals(userId, firArgument.getUserId());
                assertEquals(formInputId, firArgument.getFormInputId());
                assertEquals("", firArgument.getValue());
                return true;
            }
        }))).thenReturn(serviceSuccess(formInputResponse));

        String contentString = String.format("{\"userId\":%s,\"applicationId\":%s,\"formInputId\":%s,\"value\":\"\"}",userId, appId, formInputId);
        mockMvc.perform(post("/forminputresponse/saveQuestionResponse/")
                    .content(contentString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(expected)));
    }
}
