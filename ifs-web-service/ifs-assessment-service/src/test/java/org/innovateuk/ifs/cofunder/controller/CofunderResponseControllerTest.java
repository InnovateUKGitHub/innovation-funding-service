package org.innovateuk.ifs.cofunder.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.cofunder.form.CofunderResponseForm;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDecisionResource;
import org.innovateuk.ifs.cofunder.resource.CofunderState;
import org.innovateuk.ifs.cofunder.service.CofunderAssignmentRestService;
import org.innovateuk.ifs.cofunder.viewmodel.CofunderResponseViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.cofunder.builder.CofunderAssignmentResourceBuilder.newCofunderAssignmentResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CofunderResponseControllerTest extends BaseControllerMockMVCTest<CofunderResponseController> {

    @Mock
    private CofunderAssignmentRestService cofunderAssignmentRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Override
    protected CofunderResponseController supplyControllerUnderTest() {
        return new CofunderResponseController();
    }

    @Test
    public void editResponse() throws Exception {
        CofunderAssignmentResource cofunderAssignmentResource = newCofunderAssignmentResource()
                .withAssignmentId(100L)
                .withState(CofunderState.CREATED)
                .build();
        ApplicationResource application = newApplicationResource().withCompetition(1l).build();
        when(cofunderAssignmentRestService.getAssignment(getLoggedInUser().getId(), application.getId())).thenReturn(restSuccess(cofunderAssignmentResource));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));


        MvcResult result = mockMvc.perform(get("/cofunder/application/{applicationId}/response", application.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("cofunder/response"))
                .andReturn();

        CofunderResponseForm form = (CofunderResponseForm) result.getModelAndView().getModel().get("form");
        assertThat(form.getComments(), nullValue());
        assertThat(form.getDecision(), nullValue());
        assertThat(form.getAssignmentId(), equalTo(cofunderAssignmentResource.getAssignmentId()));

        CofunderResponseViewModel viewModel = (CofunderResponseViewModel) result.getModelAndView().getModel().get("model");
        assertThat(viewModel.getApplicationId(), equalTo(application.getId()));
        assertThat(viewModel.getApplicationName(), equalTo(application.getName()));
        assertThat(viewModel.isReadonly(), equalTo(false));
    }

    @Test
    public void editResponse_withPrevious() throws Exception {
        CofunderAssignmentResource previous = newCofunderAssignmentResource()
                .withAssignmentId(100L)
                .withState(CofunderState.ACCEPTED)
                .withComments("Wonderful")
                .build();
        ApplicationResource application = newApplicationResource().withCompetition(1l).build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));

        MvcResult result = mockMvc.perform(get("/cofunder/application/{applicationId}/response", application.getId())
                .flashAttr("previousResponse", previous))
                .andExpect(status().isOk())
                .andExpect(view().name("cofunder/response"))
                .andReturn();

        CofunderResponseForm form = (CofunderResponseForm) result.getModelAndView().getModel().get("form");
        assertThat(form.getComments(), equalTo(previous.getComments()));
        assertThat(form.getDecision(), equalTo(true));
        assertThat(form.getAssignmentId(), equalTo(previous.getAssignmentId()));
        verifyZeroInteractions(cofunderAssignmentRestService);
    }

    @Test
    public void editResponse_redirectWhenDecisionMade() throws Exception {
        long applicationId = 1L;
        CofunderAssignmentResource cofunderAssignmentResource = newCofunderAssignmentResource()
                .withAssignmentId(100L)
                .withState(CofunderState.ACCEPTED)
                .withComments("Wonderful")
                .build();
        when(cofunderAssignmentRestService.getAssignment(getLoggedInUser().getId(), applicationId)).thenReturn(restSuccess(cofunderAssignmentResource));

        mockMvc.perform(get("/cofunder/application/{applicationId}/response", applicationId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/cofunder/application/%d/response/view", applicationId)));
    }


    @Test
    public void viewResponse() throws Exception {
        CofunderAssignmentResource cofunderAssignmentResource = newCofunderAssignmentResource()
                .withAssignmentId(100L)
                .withState(CofunderState.ACCEPTED)
                .withComments("Wonderful")
                .build();
        ApplicationResource application = newApplicationResource().withCompetition(1l).build();
        when(cofunderAssignmentRestService.getAssignment(getLoggedInUser().getId(), application.getId())).thenReturn(restSuccess(cofunderAssignmentResource));
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));


        MvcResult result = mockMvc.perform(get("/cofunder/application/{applicationId}/response/view", application.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("cofunder/response"))
                .andReturn();

        CofunderResponseForm form = (CofunderResponseForm) result.getModelAndView().getModel().get("form");
        assertThat(form.getComments(), equalTo(cofunderAssignmentResource.getComments()));
        assertThat(form.getDecision(), equalTo(true));
        assertThat(form.getAssignmentId(), equalTo(cofunderAssignmentResource.getAssignmentId()));

        CofunderResponseViewModel viewModel = (CofunderResponseViewModel) result.getModelAndView().getModel().get("model");
        assertThat(viewModel.getApplicationId(), equalTo(application.getId()));
        assertThat(viewModel.getApplicationName(), equalTo(application.getName()));
        assertThat(viewModel.isReadonly(), equalTo(true));
    }

    @Test
    public void viewResponse_redirectWhenDecisionNotMade() throws Exception {
        long applicationId = 1L;
        CofunderAssignmentResource cofunderAssignmentResource = newCofunderAssignmentResource()
                .withAssignmentId(100L)
                .withState(CofunderState.CREATED)
                .build();
        when(cofunderAssignmentRestService.getAssignment(getLoggedInUser().getId(), applicationId)).thenReturn(restSuccess(cofunderAssignmentResource));

        mockMvc.perform(get("/cofunder/application/{applicationId}/response/view", applicationId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/cofunder/application/%d/response", applicationId)));
    }

    @Test
    public void changeResponse() throws Exception {
        long applicationId = 1L;
        CofunderAssignmentResource cofunderAssignmentResource = newCofunderAssignmentResource()
                .withAssignmentId(100L)
                .withState(CofunderState.ACCEPTED)
                .withComments("Wonderful")
                .build();
        when(cofunderAssignmentRestService.getAssignment(getLoggedInUser().getId(), applicationId)).thenReturn(restSuccess(cofunderAssignmentResource));
        when(cofunderAssignmentRestService.edit(cofunderAssignmentResource.getAssignmentId())).thenReturn(restSuccess());

        mockMvc.perform(post("/cofunder/application/{applicationId}/response/view", applicationId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/cofunder/application/%d/response", applicationId)));
    }

    @Test
    public void saveResponse_success() throws Exception {
        long applicationId = 1L;
        long assignmentId = 100L;
        boolean decision = true;
        String comments = "comments";

        CofunderDecisionResource resource = new CofunderDecisionResource();
        resource.setAccept(decision);
        resource.setComments(comments);
        when(cofunderAssignmentRestService.decision(eq(assignmentId), refEq(resource))).thenReturn(restSuccess());

        mockMvc.perform(post("/cofunder/application/{applicationId}/response", applicationId)
                .param("assignmentId", String.valueOf(assignmentId))
                .param("decision", String.valueOf(decision))
                .param("comments", comments))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/cofunder/application/%d/response/view", applicationId)));

        verify(cofunderAssignmentRestService).decision(eq(assignmentId), refEq(resource));
    }

    @Test
    public void saveResponse_failure() throws Exception {
        ApplicationResource application = newApplicationResource().withCompetition(1l).build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));

        mockMvc.perform(post("/cofunder/application/{applicationId}/response", application.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("form", "comments", "NotBlank"))
                .andExpect(view().name("cofunder/response"));

        verifyZeroInteractions(cofunderAssignmentRestService);
    }
}
