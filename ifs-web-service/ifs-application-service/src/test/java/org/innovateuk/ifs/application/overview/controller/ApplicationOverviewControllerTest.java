package org.innovateuk.ifs.application.overview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.overview.populator.ApplicationOverviewModelPopulator;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.HashSet;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.ASSIGN_QUESTION_PARAM;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.resource.Role.KNOWLEDGE_TRANSFER_ADVISER;
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
    private ProcessRoleRestService processRoleRestService;
    @Mock
    private ApplicationRestService applicationRestService;

    @Override
    protected ApplicationOverviewController supplyControllerUnderTest() {
        return new ApplicationOverviewController(applicationOverviewModelPopulator,
                processRoleRestService, applicationRestService);
    }

    @Test
    public void applicationOverview() throws Exception {
        ApplicationResource application = newApplicationResource()
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withApplicationState(ApplicationState.CREATED)
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        ApplicationOverviewViewModel expectedModel = mock(ApplicationOverviewViewModel.class);
        when(applicationOverviewModelPopulator.populateModel(application, loggedInUser)).thenReturn(expectedModel);
        when(processRoleRestService.findProcessRole(loggedInUser.getId(), application.getId())).thenReturn(
                restSuccess(newProcessRoleResource()
                        .withUser(loggedInUser)
                        .withRole(LEADAPPLICANT).build()));
        when(processRoleRestService.findProcessRole(application.getId())).thenReturn(
                restSuccess(Collections.singletonList(newProcessRoleResource()
                        .withUser(loggedInUser)
                        .withRole(LEADAPPLICANT).build())));
        when(applicationRestService.updateApplicationState(application.getId(), ApplicationState.OPENED)).thenReturn(restSuccess());
        MvcResult result = mockMvc.perform(get("/application/" + application.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-overview"))
                .andReturn();

        ApplicationOverviewViewModel viewModel = (ApplicationOverviewViewModel) result.getModelAndView().getModel().get("model");

        assertSame(expectedModel, viewModel);
        verify(applicationRestService).updateApplicationState(application.getId(), ApplicationState.OPENED);
    }

    @Test
    public void applicationOverviewForKtaWhenCompetitionOpenApplicationNotSubmitted() throws Exception {
        ApplicationResource application = newApplicationResource()
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withApplicationState(ApplicationState.CREATED)
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        ApplicationOverviewViewModel expectedModel = mock(ApplicationOverviewViewModel.class);
        when(applicationOverviewModelPopulator.populateModel(application, loggedInUser)).thenReturn(expectedModel);
        when(processRoleRestService.findProcessRole(application.getId())).thenReturn(
                restSuccess(Collections.singletonList(newProcessRoleResource()
                        .withUser(loggedInUser)
                        .withRole(KNOWLEDGE_TRANSFER_ADVISER).build())));
        when(applicationRestService.updateApplicationState(application.getId(), ApplicationState.OPENED)).thenReturn(restSuccess());

        mockMvc.perform(get("/application/" + application.getId()))
                .andExpect(redirectedUrl("/application/1/summary"));
    }

    @Test
    public void applicationOverviewForKtaWhenCompetitionClosedApplicationSubmitted() throws Exception {
        ApplicationResource application = newApplicationResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withApplicationState(ApplicationState.SUBMITTED)
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        ApplicationOverviewViewModel expectedModel = mock(ApplicationOverviewViewModel.class);
        when(applicationOverviewModelPopulator.populateModel(application, loggedInUser)).thenReturn(expectedModel);
        when(processRoleRestService.findProcessRole(application.getId())).thenReturn(
                restSuccess(Collections.singletonList(newProcessRoleResource()
                        .withUser(loggedInUser)
                        .withRole(KNOWLEDGE_TRANSFER_ADVISER).build())));
        when(applicationRestService.updateApplicationState(application.getId(), ApplicationState.OPENED)).thenReturn(restSuccess());

        mockMvc.perform(get("/application/" + application.getId()))
                .andExpect(redirectedUrl("/application/1/summary"));
    }

    @Test
    public void teesAndCees() throws Exception {
        mockMvc.perform(get("/application/terms-and-conditions"))
                .andExpect(view().name("application-terms-and-conditions"));
    }

}
