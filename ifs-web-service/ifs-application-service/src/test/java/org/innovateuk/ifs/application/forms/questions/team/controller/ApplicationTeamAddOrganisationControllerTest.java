package org.innovateuk.ifs.application.forms.questions.team.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.forms.questions.team.viewmodel.ApplicationTeamAddOrganisationViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ApplicationTeamAddOrganisationControllerTest extends BaseControllerMockMVCTest<ApplicationTeamAddOrganisationController> {

    @Override
    protected ApplicationTeamAddOrganisationController supplyControllerUnderTest() {
        return new ApplicationTeamAddOrganisationController();
    }

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private InviteRestService inviteRestService;

    @Test
    public void addOrganisationForm() throws Exception {
        Long applicationId = 1L;
        long questionId = 2L;
        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withName("Name")
                .build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));

        MvcResult result = mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/team/new-organisation", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(view().name("application/questions/application-team-organisation"))
                .andReturn();

        ApplicationTeamAddOrganisationViewModel actual = (ApplicationTeamAddOrganisationViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(applicationId, actual.getApplicationId());
        assertEquals("Name", actual.getApplicationName());
        assertEquals(questionId, actual.getQuestionId());
    }

    @Test
    public void addOrganisation() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        when(inviteRestService.createInvitesByInviteOrganisation(
                "New organisation",
                singletonList(new ApplicationInviteResource("Someone", "example@gmail.com", applicationId))))
            .thenReturn(restSuccess());

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/team/new-organisation", applicationId, questionId)
                .param("organisationName", "New organisation")
                .param("name", "Someone")
                .param("email", "example@gmail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/application/%d/form/question/%d/team", applicationId, questionId)));

        verify(inviteRestService).createInvitesByInviteOrganisation(
                "New organisation",
                singletonList(new ApplicationInviteResource("Someone", "example@gmail.com", applicationId)));
    }
}
