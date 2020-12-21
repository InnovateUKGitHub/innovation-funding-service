package org.innovateuk.ifs.application.forms.questions.terms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.common.populator.ApplicationTermsModelPopulator;
import org.innovateuk.ifs.application.common.populator.ApplicationTermsPartnerModelPopulator;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsPartnerViewModel;
import org.innovateuk.ifs.application.common.viewmodel.ApplicationTermsViewModel;
import org.innovateuk.ifs.application.forms.questions.terms.form.ApplicationTermsForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionStatusRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
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
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationTermsControllerTest extends BaseControllerMockMVCTest<ApplicationTermsController> {

    @Mock
    private ProcessRoleRestService processRoleRestService;
    @Mock
    private QuestionStatusRestService questionStatusRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private ApplicationTermsModelPopulator applicationTermsModelPopulator;

    @Mock
    private ApplicationTermsPartnerModelPopulator applicationTermsPartnerModelPopulator;

    private CompetitionResource competition;
    private ApplicationResource application;
    private GrantTermsAndConditionsResource grantTermsAndConditions;
    private boolean termsAccepted;
    private boolean additionalTerms;
    private ZonedDateTime termsAcceptedOn;
    private long questionId;

    @Override
    protected ApplicationTermsController supplyControllerUnderTest() {
        return new ApplicationTermsController(
                processRoleRestService,
                questionStatusRestService,
                applicationRestService,
                applicationTermsPartnerModelPopulator,
                applicationTermsModelPopulator);
    }

    @Before
    public void setUp() {

        grantTermsAndConditions = new GrantTermsAndConditionsResource(
                "T&C",
                "terms-template",
                1);

        competition = newCompetitionResource()
                .withName("Competition name")
                .withTermsAndConditions(grantTermsAndConditions)
                .build();

        application = newApplicationResource()
                .withName("Application name")
                .withCollaborativeProject(false)
                .build();

        questionId = 7L;
        termsAccepted = false;
        additionalTerms = true;
        termsAcceptedOn = now();
    }

    @Test
    public void getTerms() throws Exception {

        UserResource loggedInUser = newUserResource()
                .withFirstName("Tom")
                .withLastName("Baldwin")
                .build();

        ApplicationTermsViewModel viewModel = new ApplicationTermsViewModel(
                application.getId(),
                competition.getName(),
                competition.getId(),
                questionId,
                competition.getTermsAndConditions().getTemplate(),
                application.isCollaborativeProject(),
                termsAccepted,
                loggedInUser.getName(),
                termsAcceptedOn,
                true,
                additionalTerms,
                competition.isHeukar());

        when(applicationTermsModelPopulator.populate(loggedInUser, application.getId(), questionId, false)).thenReturn(viewModel);

        setLoggedInUser(loggedInUser);

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/terms-and-conditions", application.getId(), questionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("application/sections/terms-and-conditions/terms-and-conditions"));

        verify(applicationTermsModelPopulator, only()).populate(loggedInUser, application.getId(), questionId, false);
    }

    @Test
    public void getTerms_readOnly() throws Exception {

        UserResource loggedInUser = newUserResource()
                .withFirstName("Tom")
                .withLastName("Baldwin")
                .build();

        ApplicationTermsViewModel viewModel = new ApplicationTermsViewModel(
                application.getId(),
                competition.getName(),
                competition.getId(),
                questionId,
                competition.getTermsAndConditions().getTemplate(),
                application.isCollaborativeProject(),
                termsAccepted,
                loggedInUser.getName(),
                termsAcceptedOn,
                true,
                additionalTerms,
                competition.isHeukar());

        when(applicationTermsModelPopulator.populate(loggedInUser, application.getId(), questionId, true)).thenReturn(viewModel);

        setLoggedInUser(loggedInUser);

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/terms-and-conditions?readonly=true", application.getId(), questionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("application/sections/terms-and-conditions/terms-and-conditions"));

        verify(applicationTermsModelPopulator, only()).populate(loggedInUser, application.getId(), questionId, true);
    }

    @Test
    public void acceptTerms() throws Exception {

        CompetitionResource competition = newCompetitionResource()
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(3L)
                .withCompetition(competition.getId())
                .build();

        ProcessRoleResource processRole = newProcessRoleResource()
                .withUser(getLoggedInUser())
                .withApplication(application.getId())
                .build();

        when(processRoleRestService.findProcessRole(processRole.getUser(), processRole.getApplicationId())).thenReturn(restSuccess(processRole));
        when(questionStatusRestService.markAsComplete(questionId, application.getId(), processRole.getId())).thenReturn(restSuccess(emptyList()));

        ApplicationTermsForm form = new ApplicationTermsForm();

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/terms-and-conditions", application.getId(), questionId)
                .param("agreed", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attribute("form", form))
                .andExpect(model().hasNoErrors())
                .andExpect(redirectedUrlTemplate("/application/{applicationId}/form/question/{questionId}/terms-and-conditions#terms-accepted", application.getId(), questionId));

        InOrder inOrder = inOrder(processRoleRestService, questionStatusRestService);
        inOrder.verify(processRoleRestService).findProcessRole(processRole.getUser(), processRole.getApplicationId());
        inOrder.verify(questionStatusRestService).markAsComplete(questionId, application.getId(), processRole.getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void acceptTerms_notAgreed() throws Exception {

        ProcessRoleResource processRole = newProcessRoleResource()
                .withUser(getLoggedInUser())
                .withApplication(application.getId())
                .build();

        when(processRoleRestService.findProcessRole(processRole.getUser(), processRole.getApplicationId())).thenReturn(restSuccess(processRole));
        when(questionStatusRestService.markAsComplete(questionId, application.getId(), processRole.getId()))
                .thenReturn(restFailure(fieldError("agreed", "false", "")));

        ApplicationTermsViewModel viewModel = new ApplicationTermsViewModel(
                application.getId(),
                competition.getName(),
                competition.getId(),
                questionId,
                competition.getTermsAndConditions().getTemplate(),
                application.isCollaborativeProject(),
                termsAccepted,
                loggedInUser.getName(),
                null,
                true,
                additionalTerms,
                competition.isHeukar());

        when(applicationTermsModelPopulator.populate(loggedInUser, application.getId(), questionId, false)).thenReturn(viewModel);

        ApplicationTermsForm form = new ApplicationTermsForm();

        mockMvc.perform(post("/application/{applicationId}/form/question/{questionId}/terms-and-conditions", application.getId(), questionId)
                .param("agreed", "false"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", form))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "agreed"))
                .andExpect(view().name("application/sections/terms-and-conditions/terms-and-conditions"));

        InOrder inOrder = inOrder(processRoleRestService, questionStatusRestService, applicationTermsModelPopulator);
        inOrder.verify(processRoleRestService).findProcessRole(processRole.getUser(), processRole.getApplicationId());
        inOrder.verify(questionStatusRestService).markAsComplete(questionId, application.getId(), processRole.getId());
        inOrder.verify(applicationTermsModelPopulator).populate(loggedInUser, application.getId(), questionId, false);
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
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(applicationTermsPartnerModelPopulator.populate(application, questionId)).thenReturn(viewModel);

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/terms-and-conditions/partner-status", application.getId(), questionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", viewModel))
                .andExpect(view().name("application/sections/terms-and-conditions/terms-and-conditions-partner-status"));

        InOrder inOrder = inOrder(applicationRestService, applicationTermsPartnerModelPopulator);
        inOrder.verify(applicationRestService).getApplicationById(application.getId());
        inOrder.verify(applicationTermsPartnerModelPopulator).populate(application, questionId);
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

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/terms-and-conditions/partner-status", application.getId(), questionId))
                .andExpect(status().isForbidden());

        InOrder inOrder = inOrder(applicationRestService, applicationTermsPartnerModelPopulator);
        inOrder.verify(applicationRestService).getApplicationById(application.getId());
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

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));

        mockMvc.perform(get("/application/{applicationId}/form/question/{questionId}/terms-and-conditions/partner-status", application.getId(), questionId))
                .andExpect(status().isForbidden());

        InOrder inOrder = inOrder(applicationRestService, applicationTermsPartnerModelPopulator);
        inOrder.verify(applicationRestService).getApplicationById(application.getId());
        inOrder.verifyNoMoreInteractions();
    }
}