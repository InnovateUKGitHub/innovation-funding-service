package org.innovateuk.ifs.application.terms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.terms.form.ApplicationTermsForm;
import org.innovateuk.ifs.application.terms.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.application.terms.viewmodel.ApplicationTermsViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;
import static org.assertj.core.util.Lists.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationTermsControllerTest extends BaseControllerMockMVCTest<ApplicationTermsController> {

    @Mock
    private UserRestService userRestServiceMock;
    @Mock
    private QuestionStatusRestService questionStatusRestServiceMock;
    @Mock
    private ApplicationTermsModelPopulator applicationTermsModelPopulatorMock;

    @Override
    protected ApplicationTermsController supplyControllerUnderTest() {
        return new ApplicationTermsController(userRestServiceMock, questionStatusRestServiceMock, applicationTermsModelPopulatorMock);
    }

    @Test
    public void getTerms() throws Exception {
        long applicationId = 3L;
        long questionId = 7L;
        String competitionTermsTemplate = "terms-template";
        boolean collaborativeApplication = false;
        boolean termsAccepted = false;
        UserResource loggedInUser = newUserResource()
                .withFirstName("Tom")
                .withLastName("Baldwin")
                .build();
        ZonedDateTime termsAcceptedOn = now();

        ApplicationTermsViewModel viewModel = new ApplicationTermsViewModel(applicationId, questionId,
                competitionTermsTemplate, collaborativeApplication, termsAccepted, loggedInUser.getName(), termsAcceptedOn);

        when(applicationTermsModelPopulatorMock.populate(loggedInUser, applicationId, questionId)).thenReturn(viewModel);

        setLoggedInUser(loggedInUser);

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/terms-and-conditions", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("application/terms-and-conditions"));

        verify(applicationTermsModelPopulatorMock, only()).populate(loggedInUser, applicationId, questionId);
    }

    @Test
    public void acceptTerms() throws Exception {
        long questionId = 7L;
        CompetitionResource competition = newCompetitionResource()
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(3L)
                .withCompetition(competition.getId())
                .build();

        SectionResource termsAndConditionsSection = newSectionResource().build();

        ProcessRoleResource processRole = newProcessRoleResource()
                .withUser(getLoggedInUser())
                .withApplication(application.getId())
                .build();

        when(userRestServiceMock.findProcessRole(processRole.getUser(), processRole.getApplicationId())).thenReturn(restSuccess(processRole));
        when(questionStatusRestServiceMock.markAsComplete(questionId, application.getId(), processRole.getId())).thenReturn(restSuccess(emptyList()));

        ApplicationTermsForm form = new ApplicationTermsForm();
        form.setAgreed(true);

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/terms-and-conditions", application.getId(), questionId)
                .param("agreed", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attribute("form", form))
                .andExpect(model().hasNoErrors())
                .andExpect(redirectedUrlTemplate("/application/{applicationId}/form/question/{questionId}/terms-and-conditions", application.getId(), questionId));

        InOrder inOrder = inOrder(userRestServiceMock, questionStatusRestServiceMock);
        inOrder.verify(userRestServiceMock).findProcessRole(processRole.getUser(), processRole.getApplicationId());
        inOrder.verify(questionStatusRestServiceMock).markAsComplete(questionId, application.getId(), processRole.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptTerms_notAgreed() throws Exception {
        long questionId = 7L;
        CompetitionResource competition = newCompetitionResource()
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(3L)
                .withCompetition(competition.getId())
                .build();

        SectionResource termsAndConditionsSection = newSectionResource().build();

        ProcessRoleResource processRole = newProcessRoleResource()
                .withUser(getLoggedInUser())
                .withApplication(application.getId())
                .build();

        ApplicationTermsForm form = new ApplicationTermsForm();
        form.setAgreed(false);

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/terms-and-conditions", application.getId(), questionId)
                .param("agreed", "false"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", form))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "agreed"))
                .andExpect(view().name("application/terms-and-conditions"));

        InOrder inOrder = inOrder(userRestServiceMock, questionStatusRestServiceMock);
        inOrder.verifyNoMoreInteractions();
    }
}