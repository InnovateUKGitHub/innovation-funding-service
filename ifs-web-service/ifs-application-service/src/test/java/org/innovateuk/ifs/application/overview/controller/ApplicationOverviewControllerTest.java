package org.innovateuk.ifs.application.overview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.overview.populator.ApplicationOverviewModelPopulator;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.ASSIGN_QUESTION_PARAM;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationOverviewControllerTest extends BaseControllerMockMVCTest<ApplicationOverviewController> {

    @Mock
    private ApplicationOverviewModelPopulator applicationOverviewModelPopulator;
    @Mock
    private QuestionService questionService;
    @Mock
    private UserRestService userRestService;
    @Mock
    private ApplicationRestService applicationRestService;
    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Override
    protected ApplicationOverviewController supplyControllerUnderTest() {
        return new ApplicationOverviewController(applicationOverviewModelPopulator,
                questionService, userRestService, applicationRestService, cookieFlashMessageFilter);
    }

    @Test
    public void applicationOverview() throws Exception {
        ApplicationResource application = newApplicationResource()
                .withApplicationState(ApplicationState.CREATED)
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        ApplicationOverviewViewModel expectedModel = mock(ApplicationOverviewViewModel.class);
        when(applicationOverviewModelPopulator.populateModel(application, loggedInUser)).thenReturn(expectedModel);
        when(userRestService.findProcessRole(loggedInUser.getId(), application.getId())).thenReturn(restSuccess(newProcessRoleResource().withRole(LEADAPPLICANT).build()));
        when(applicationRestService.updateApplicationState(application.getId(), ApplicationState.OPEN)).thenReturn(restSuccess());

        MvcResult result = mockMvc.perform(get("/application/" + application.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-overview"))
                .andReturn();

        ApplicationOverviewViewModel viewModel = (ApplicationOverviewViewModel) result.getModelAndView().getModel().get("model");

        assertSame(expectedModel, viewModel);
        verify(applicationRestService).updateApplicationState(application.getId(), ApplicationState.OPEN);
    }

    @Test
    public void applicationDetailsAssign() throws Exception {
        ApplicationResource application = newApplicationResource().build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        mockMvc.perform(post("/application/" + application.getId()).param(ASSIGN_QUESTION_PARAM, "1_2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/" + application.getId()));
    }

    @Test
    public void teesAndCees() throws Exception {
        mockMvc.perform(get("/application/terms-and-conditions"))
                .andExpect(view().name("application-terms-and-conditions"));
    }

}
