package org.innovateuk.ifs.form.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

        when(formInputServiceMock.findResponsesByApplication(applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(post("/forminputresponse/findResponsesByApplication/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(expected)));

        verify(formInputServiceMock, only()).findResponsesByApplication(applicationId);
    }

    @Test
    public void findByFormInputIdAndApplication() throws Exception {
        long applicationId = 1L;
        long formInputId = 2L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputServiceMock.findResponsesByFormInputIdAndApplicationId(formInputId, applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(post("/forminputresponse/findResponseByFormInputIdAndApplicationId/{formInputId}/{applicationId}", formInputId, applicationId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(expected)));

        verify(formInputServiceMock, only()).findResponsesByFormInputIdAndApplicationId(formInputId, applicationId);
    }

    @Test
    public void findByApplicationIdAndQuestionName() throws Exception {
        long applicationId = 1L;
        String questionName = "question";

        FormInputResponseResource expected = newFormInputResponseResource().build();

        when(formInputServiceMock.findResponseByApplicationIdAndQuestionName(applicationId, questionName)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(post("/forminputresponse/findByApplicationIdAndQuestionName/{applicationId}/{questionName}", applicationId, questionName))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(expected)));

        verify(formInputServiceMock, only()).findResponseByApplicationIdAndQuestionName(applicationId, questionName);
    }

    @Test
    public void findByApplicationIdAndQuestionId() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputServiceMock.findResponseByApplicationIdAndQuestionId(applicationId, questionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(post("/forminputresponse/findByApplicationIdAndQuestionId/{applicationId}/{questionId}", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(expected)));

        verify(formInputServiceMock, only()).findResponseByApplicationIdAndQuestionId(applicationId, questionId);
    }

    @Test
    @Ignore("TODO DW - INFUND-1555 - re-establish test")
    public void testSaveQuestionResponse() throws Exception {

        Application application = newApplication().withId(456L).build();
        User user = newUser().with(id(123L)).build();
        ProcessRole applicantProcessRole = newProcessRole().withRole(newRole().withType(COLLABORATOR).build()).build();

        FormInput formInput = newFormInput().with(id(789L)).build();

        when(formInputRepositoryMock.findOne(789L)).thenReturn(formInput);
        when(userRepositoryMock.findOne(123L)).thenReturn(user);
        when(applicationRepositoryMock.findOne(456L)).thenReturn(application);
        when(processRoleRepositoryMock.findByUserAndApplicationId(user, application.getId())).thenReturn(singletonList(applicantProcessRole));

        String contentString = "{\"userId\":123,\"applicationId\":456,\"formInputId\":789,\"value\":\"\"}";
        mockMvc.perform(post("/forminputresponse/saveQuestionResponse/")
                    .content(contentString)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }
}
