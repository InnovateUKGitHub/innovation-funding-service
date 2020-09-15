package org.innovateuk.ifs.application.forms.questions.terms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.common.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationTermsPartnerModelPopulator;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsPartnerViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.application.forms.questions.terms.form.ApplicationTermsForm;
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
import static org.innovateuk.ifs.application.resource.ApplicationState.OPENED;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
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
    private ApplicationRestService applicationRestServiceMock;
    @Mock
    private ApplicationTermsModelPopulator applicationTermsModelPopulatorMock;
    @Mock
    private ApplicationTermsPartnerModelPopulator applicationTermsPartnerModelPopulatorMock;

    @Override
    protected ApplicationTermsController supplyControllerUnderTest() {
        return new ApplicationTermsController(userRestServiceMock, questionStatusRestServiceMock, applicationRestServiceMock,
                applicationTermsPartnerModelPopulatorMock, applicationTermsModelPopulatorMock);
    }

    @Test
    public void getTerms() throws Exception {
        long applicationId = 3L;
        long compeitionId = 5L;
        long questionId = 7L;
        String competitionTermsTemplate = "terms-template";
        boolean collaborativeApplication = false;
        boolean termsAccepted = false;
        boolean additionalTerms = true;
        UserResource loggedInUser = newUserResource()
                .withFirstName("Tom")
                .withLastName("Baldwin")
                .build();
        ZonedDateTime termsAcceptedOn = now();

        ApplicationTermsViewModel viewModel = new ApplicationTermsViewModel(applicationId, "compName", compeitionId, questionId,
                competitionTermsTemplate, collaborativeApplication, termsAccepted, loggedInUser.getName(), termsAcceptedOn, true, additionalTerms);

        when(applicationTermsModelPopulatorMock.populate(loggedInUser, applicationId, questionId, false)).thenReturn(viewModel);

        setLoggedInUser(loggedInUser);

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/terms-and-conditions", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("application/sections/terms-and-conditions/terms-and-conditions"));

        verify(applicationTermsModelPopulatorMock, only()).populate(loggedInUser, applicationId, questionId, false);
    }

    @Test
    public void getTerms_readOnly() throws Exception {
        long applicationId = 3L;
        long compeitionId = 5L;
        long questionId = 7L;
        String competitionTermsTemplate = "terms-template";
        boolean collaborativeApplication = false;
        boolean termsAccepted = false;
        boolean additionalTerms = true;
        UserResource loggedInUser = newUserResource()
                .withFirstName("Tom")
                .withLastName("Baldwin")
                .build();
        ZonedDateTime termsAcceptedOn = now();

        ApplicationTermsViewModel viewModel = new ApplicationTermsViewModel(applicationId, "compeName", compeitionId, questionId,
                competitionTermsTemplate, collaborativeApplication, termsAccepted, loggedInUser.getName(), termsAcceptedOn, true, additionalTerms);

        when(applicationTermsModelPopulatorMock.populate(loggedInUser, applicationId, questionId, true)).thenReturn(viewModel);

        setLoggedInUser(loggedInUser);

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/terms-and-conditions?readonly=true", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("application/sections/terms-and-conditions/terms-and-conditions"));

        verify(applicationTermsModelPopulatorMock, only()).populate(loggedInUser, applicationId, questionId, true);
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

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/terms-and-conditions", application.getId(), questionId)
                .param("agreed", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attribute("form", form))
                .andExpect(model().hasNoErrors())
                .andExpect(redirectedUrlTemplate("/application/{applicationId}/form/question/{questionId}/terms-and-conditions#terms-accepted", application.getId(), questionId));

        InOrder inOrder = inOrder(userRestServiceMock, questionStatusRestServiceMock);
        inOrder.verify(userRestServiceMock).findProcessRole(processRole.getUser(), processRole.getApplicationId());
        inOrder.verify(questionStatusRestServiceMock).markAsComplete(questionId, application.getId(), processRole.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptTerms_notAgreed() throws Exception {
        String competitionTermsTemplate = "terms-template";
        boolean collaborativeApplication = false;
        boolean termsAccepted = false;
        boolean additionalTerms = true;

        long questionId = 7L;
        CompetitionResource competition = newCompetitionResource()
                .withId(5L)
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
        when(questionStatusRestServiceMock.markAsComplete(questionId, application.getId(), processRole.getId()))
                .thenReturn(restFailure(fieldError("agreed", "false", "")));

        ApplicationTermsViewModel viewModel = new ApplicationTermsViewModel(application.getId(), "compName",competition.getId(), questionId,
                competitionTermsTemplate, collaborativeApplication, termsAccepted, loggedInUser.getName(), null, true, additionalTerms);

        when(applicationTermsModelPopulatorMock.populate(loggedInUser, application.getId(), questionId, false)).thenReturn(viewModel);

        ApplicationTermsForm form = new ApplicationTermsForm();

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/terms-and-conditions", application.getId(), questionId)
                .param("agreed", "false"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", form))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "agreed"))
                .andExpect(view().name("application/sections/terms-and-conditions/terms-and-conditions"));

        InOrder inOrder = inOrder(userRestServiceMock, questionStatusRestServiceMock, applicationTermsModelPopulatorMock);
        inOrder.verify(userRestServiceMock).findProcessRole(processRole.getUser(), processRole.getApplicationId());
        inOrder.verify(questionStatusRestServiceMock).markAsComplete(questionId, application.getId(), processRole.getId());
        inOrder.verify(applicationTermsModelPopulatorMock).populate(loggedInUser, application.getId(), questionId, false);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getPartnerStatus() throws Exception {
        long questionId = 7L;
        CompetitionResource competition = newCompetitionResource()
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(3L)
                .withCompetition(competition.getId())
                .withCollaborativeProject(true)
                .withApplicationState(OPENED)
                .build();


        ApplicationTermsPartnerViewModel viewModel = new ApplicationTermsPartnerViewModel(application.getId(), "compName", questionId, emptyList());
        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(applicationTermsPartnerModelPopulatorMock.populate(application, questionId)).thenReturn(viewModel);

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/terms-and-conditions/partner-status", application.getId(), questionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("application/sections/terms-and-conditions/terms-and-conditions-partner-status"));

        InOrder inOrder = inOrder(applicationRestServiceMock, applicationTermsPartnerModelPopulatorMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verify(applicationTermsPartnerModelPopulatorMock).populate(application, questionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getPartnerStatus_nonCollaborative() throws Exception {
        long questionId = 7L;
        CompetitionResource competition = newCompetitionResource()
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(3L)
                .withCompetition(competition.getId())
                .withCollaborativeProject(false)
                .withApplicationState(OPENED)
                .build();

        ApplicationTermsPartnerViewModel viewModel = new ApplicationTermsPartnerViewModel(application.getId(), "compName", questionId, emptyList());
        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/terms-and-conditions/partner-status", application.getId(), questionId))
                .andExpect(status().isForbidden());

        InOrder inOrder = inOrder(applicationRestServiceMock, applicationTermsPartnerModelPopulatorMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getPartnerStatus_nonOpen() throws Exception {
        long questionId = 7L;
        CompetitionResource competition = newCompetitionResource()
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(3L)
                .withCompetition(competition.getId())
                .withCollaborativeProject(true)
                .withApplicationState(SUBMITTED)
                .build();

        ApplicationTermsPartnerViewModel viewModel = new ApplicationTermsPartnerViewModel(application.getId(), "compName", questionId, emptyList());
        when(applicationRestServiceMock.getApplicationById(application.getId())).thenReturn(restSuccess(application));

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/terms-and-conditions/partner-status", application.getId(), questionId))
                .andExpect(status().isForbidden());

        InOrder inOrder = inOrder(applicationRestServiceMock, applicationTermsPartnerModelPopulatorMock);
        inOrder.verify(applicationRestServiceMock).getApplicationById(application.getId());
        inOrder.verifyNoMoreInteractions();
    }
}