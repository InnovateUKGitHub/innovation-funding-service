package org.innovateuk.ifs.form.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.form.builder.FormInputBuilder.newFormInput;
import static org.innovateuk.ifs.form.builder.FormInputResponseBuilder.newFormInputResponse;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.COLLABORATOR;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Ignore("TODO DW - INFUND-1555 - re-establish test")
public class FormInputResponseControllerTest extends BaseControllerMockMVCTest<FormInputResponseController> {

    @Override
    protected FormInputResponseController supplyControllerUnderTest() {
        return new FormInputResponseController();
    }

    @Test
    public void testFindResponsesByApplication() throws Exception {

        ProcessRole[] consortiumsProcessRoles = newProcessRole().buildArray(2, ProcessRole.class);
        Application application = newApplication().withId(123L).withProcessRoles(consortiumsProcessRoles).build();
        FormInput formInput = newFormInput().build();

        List<FormInputResponse> consortiumResponses = newFormInputResponse().withFormInputs(formInput).build(2);

        when(applicationRepositoryMock.findOne(123L)).thenReturn(application);
        when(formInputResponseRepositoryMock.findByApplicationId(123L)).thenReturn(consortiumResponses);

        mockMvc.perform(post("/forminputresponse/findResponsesByApplication/123")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(consortiumResponses)));
    }

    @Test
    public void testSaveQuestionResponse() throws Exception {

        Application application = newApplication().withId(456L).build();
        User user = newUser().with(id(123L)).build();
        ProcessRole applicantProcessRole = newProcessRole().withRole(newRole().withType(COLLABORATOR).build()).build();

        FormInput formInput = newFormInput().with(id(789L)).build();

        when(formInputRepositoryMock.findOne(789L)).thenReturn(formInput);
        when(userRepositoryMock.findOne(123L)).thenReturn(user);
        when(applicationRepositoryMock.findOne(456L)).thenReturn(application);
        when(processRoleRepositoryMock.findByUserAndApplication(user, application)).thenReturn(singletonList(applicantProcessRole));

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
