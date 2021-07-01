package org.innovateuk.ifs.application.assign.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.assign.model.AssignQuestionViewModel;
import org.innovateuk.ifs.application.assign.populator.AssignQuestionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.navigation.PageHistory;
import org.innovateuk.ifs.navigation.PageHistoryService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

import static java.lang.String.valueOf;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssignQuestionControllerTest extends BaseControllerMockMVCTest<AssignQuestionController> {

    @Override
    protected AssignQuestionController supplyControllerUnderTest() {
        return new AssignQuestionController();
    }

    @Mock
    private ProcessRoleRestService processRoleRestServiceMock;

    @Mock
    private QuestionService questionServiceMock;

    @Mock
    private AssignQuestionModelPopulator assignQuestionModelPopulatorMock;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilterMock;

    @Mock
    private PageHistoryService pageHistoryService;

    @Test
    public void viewAssign() throws Exception {
        QuestionResource question = newQuestionResource().build();
        ApplicationResource applicationResource = newApplicationResource()
                .withName("Super imaginative application name")
                .build();
        UserResource user = newUserResource().build();
        QuestionStatusResource questionStatus = newQuestionStatusResource().withAssignee(user.getId()).build();

        AssignQuestionViewModel model = new AssignQuestionViewModel(applicationResource,
                                                                    emptyList(),
                                                                    question);

        when(questionServiceMock.findQuestionStatusesByQuestionAndApplicationId(anyLong(), anyLong()))
                .thenReturn(singletonList(questionStatus));
        when(assignQuestionModelPopulatorMock.populateModel(question.getId(), applicationResource.getId())).thenReturn(model);

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/assign", applicationResource.getId(), question.getId()))
                .andExpect(status().isOk());

        verify(questionServiceMock).findQuestionStatusesByQuestionAndApplicationId(question.getId(), applicationResource.getId());
        verify(assignQuestionModelPopulatorMock).populateModel(question.getId(), applicationResource.getId());
    }

    @Test
    public void assignQuestion() throws Exception {

        QuestionResource question = newQuestionResource().build();
        ApplicationResource application = newApplicationResource().build();
        ProcessRoleResource processRole = newProcessRoleResource().build();
        UserResource user = newUserResource().build();
        setLoggedInUser(user);
        long assigneeId = 123L;
        String redirect = "/blah/blah";
        when(processRoleRestServiceMock.findProcessRole(user.getId(), application.getId())).thenReturn(restSuccess(processRole));
        when(questionServiceMock.assign(question.getId(), application.getId(), assigneeId, processRole.getId())).thenReturn(serviceSuccess());
        when(pageHistoryService.getPreviousPage(any())).thenReturn(Optional.of(new PageHistory(redirect)));

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/assign", application.getId(), question.getId())
                                .param("assignee", valueOf(assigneeId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirect));

        verify(processRoleRestServiceMock).findProcessRole(user.getId(), application.getId());
        verify(questionServiceMock).assign(question.getId(), application.getId(), assigneeId, processRole.getId());
        verify(cookieFlashMessageFilterMock).setFlashMessage(isA(HttpServletResponse.class), eq("assignedQuestion"));
    }
}
