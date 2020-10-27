package org.innovateuk.ifs.supporter.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.supporter.form.SupporterResponseForm;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterDecisionResource;
import org.innovateuk.ifs.supporter.resource.SupporterState;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.supporter.viewmodel.SupporterResponseViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.supporter.builder.SupporterAssignmentResourceBuilder.newSupporterAssignmentResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class SupporterResponseControllerTest extends BaseControllerMockMVCTest<SupporterResponseController> {

    @Mock
    private SupporterAssignmentRestService supporterAssignmentRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Override
    protected SupporterResponseController supplyControllerUnderTest() {
        return new SupporterResponseController();
    }

    @Test
    public void editResponse() throws Exception {
        SupporterAssignmentResource supporterAssignmentResource = newSupporterAssignmentResource()
                .withAssignmentId(100L)
                .withState(SupporterState.CREATED)
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT)
                .withCompetition(1L)
                .build();
        when(supporterAssignmentRestService.getAssignment(getLoggedInUser().getId(), application.getId())).thenReturn(restSuccess(supporterAssignmentResource));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));


        MvcResult result = mockMvc.perform(get("/supporter/application/{applicationId}/response", application.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("supporter/response"))
                .andReturn();

        SupporterResponseForm form = (SupporterResponseForm) result.getModelAndView().getModel().get("form");
        assertThat(form.getComments(), nullValue());
        assertThat(form.getDecision(), nullValue());
        assertThat(form.getAssignmentId(), equalTo(supporterAssignmentResource.getAssignmentId()));

        SupporterResponseViewModel viewModel = (SupporterResponseViewModel) result.getModelAndView().getModel().get("model");
        assertThat(viewModel.getApplicationId(), equalTo(application.getId()));
        assertThat(viewModel.getApplicationName(), equalTo(application.getName()));
        assertThat(viewModel.isReadonly(), equalTo(false));
        assertThat(viewModel.isCanEdit(), equalTo(true));
    }

    @Test
    public void editResponse_withPrevious() throws Exception {
        SupporterAssignmentResource previous = newSupporterAssignmentResource()
                .withAssignmentId(100L)
                .withState(SupporterState.ACCEPTED)
                .withComments("Wonderful")
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT)
                .withCompetition(1L)
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));

        MvcResult result = mockMvc.perform(get("/supporter/application/{applicationId}/response", application.getId())
                .flashAttr("previousResponse", previous))
                .andExpect(status().isOk())
                .andExpect(view().name("supporter/response"))
                .andReturn();

        SupporterResponseForm form = (SupporterResponseForm) result.getModelAndView().getModel().get("form");
        assertThat(form.getComments(), equalTo(previous.getComments()));
        assertThat(form.getDecision(), equalTo(true));
        assertThat(form.getAssignmentId(), equalTo(previous.getAssignmentId()));
        verifyZeroInteractions(supporterAssignmentRestService);
    }

    @Test
    public void editResponse_redirectWhenDecisionMade() throws Exception {
        long applicationId = 1L;
        SupporterAssignmentResource supporterAssignmentResource = newSupporterAssignmentResource()
                .withAssignmentId(100L)
                .withState(SupporterState.ACCEPTED)
                .withComments("Wonderful")
                .build();
        when(supporterAssignmentRestService.getAssignment(getLoggedInUser().getId(), applicationId)).thenReturn(restSuccess(supporterAssignmentResource));

        mockMvc.perform(get("/supporter/application/{applicationId}/response", applicationId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/supporter/application/%d/response/view", applicationId)));
    }


    @Test
    public void viewResponse() throws Exception {
        SupporterAssignmentResource supporterAssignmentResource = newSupporterAssignmentResource()
                .withAssignmentId(100L)
                .withState(SupporterState.ACCEPTED)
                .withComments("Wonderful")
                .build();
        ApplicationResource application = newApplicationResource()
                .withCompetitionStatus(CompetitionStatus.FUNDERS_PANEL)
                .withCompetition(1L)
                .build();
        when(supporterAssignmentRestService.getAssignment(getLoggedInUser().getId(), application.getId())).thenReturn(restSuccess(supporterAssignmentResource));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));


        MvcResult result = mockMvc.perform(get("/supporter/application/{applicationId}/response/view", application.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("supporter/response"))
                .andReturn();

        SupporterResponseForm form = (SupporterResponseForm) result.getModelAndView().getModel().get("form");
        assertThat(form.getComments(), equalTo(supporterAssignmentResource.getComments()));
        assertThat(form.getDecision(), equalTo(true));
        assertThat(form.getAssignmentId(), equalTo(supporterAssignmentResource.getAssignmentId()));

        SupporterResponseViewModel viewModel = (SupporterResponseViewModel) result.getModelAndView().getModel().get("model");
        assertThat(viewModel.getApplicationId(), equalTo(application.getId()));
        assertThat(viewModel.getApplicationName(), equalTo(application.getName()));
        assertThat(viewModel.isReadonly(), equalTo(true));
        assertThat(viewModel.isCanEdit(), equalTo(false));
    }

    @Test
    public void viewResponse_redirectWhenDecisionNotMade() throws Exception {
        long applicationId = 1L;
        SupporterAssignmentResource supporterAssignmentResource = newSupporterAssignmentResource()
                .withAssignmentId(100L)
                .withState(SupporterState.CREATED)
                .build();
        when(supporterAssignmentRestService.getAssignment(getLoggedInUser().getId(), applicationId)).thenReturn(restSuccess(supporterAssignmentResource));

        mockMvc.perform(get("/supporter/application/{applicationId}/response/view", applicationId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/supporter/application/%d/response", applicationId)));
    }

    @Test
    public void changeResponse() throws Exception {
        long applicationId = 1L;
        SupporterAssignmentResource supporterAssignmentResource = newSupporterAssignmentResource()
                .withAssignmentId(100L)
                .withState(SupporterState.ACCEPTED)
                .withComments("Wonderful")
                .build();
        when(supporterAssignmentRestService.getAssignment(getLoggedInUser().getId(), applicationId)).thenReturn(restSuccess(supporterAssignmentResource));
        when(supporterAssignmentRestService.edit(supporterAssignmentResource.getAssignmentId())).thenReturn(restSuccess());

        mockMvc.perform(post("/supporter/application/{applicationId}/response/view", applicationId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/supporter/application/%d/response", applicationId)));
    }

    @Test
    public void saveResponse_success() throws Exception {
        long applicationId = 1L;
        long assignmentId = 100L;
        boolean decision = true;
        String comments = "comments";

        SupporterDecisionResource resource = new SupporterDecisionResource();
        resource.setAccept(decision);
        resource.setComments(comments);
        when(supporterAssignmentRestService.decision(eq(assignmentId), refEq(resource))).thenReturn(restSuccess());

        mockMvc.perform(post("/supporter/application/{applicationId}/response", applicationId)
                .param("assignmentId", String.valueOf(assignmentId))
                .param("decision", String.valueOf(decision))
                .param("comments", comments))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/supporter/application/%d/response/view", applicationId)));

        verify(supporterAssignmentRestService).decision(eq(assignmentId), refEq(resource));
    }

    @Test
    public void saveResponse_failure() throws Exception {
        ApplicationResource application = newApplicationResource()
                .withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT)
                .withCompetition(1L)
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));

        mockMvc.perform(post("/supporter/application/{applicationId}/response", application.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("form", "comments", "NotBlank"))
                .andExpect(view().name("supporter/response"));

        verifyZeroInteractions(supporterAssignmentRestService);
    }
}
