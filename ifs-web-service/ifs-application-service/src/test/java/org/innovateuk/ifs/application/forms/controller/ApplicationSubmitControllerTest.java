package org.innovateuk.ifs.application.forms.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.ASSIGN_QUESTION_PARAM;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.MARK_AS_COMPLETE;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationSubmitControllerTest extends BaseControllerMockMVCTest<ApplicationSubmitController> {
    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Spy
    @InjectMocks
    private ApplicationModelPopulator applicationModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Override
    protected ApplicationSubmitController supplyControllerUnderTest() {
        return new ApplicationSubmitController();
    }

    @Before
    public void setUp() {
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
        this.setupInvites();

        questionResources.forEach((id, questionResource) -> when(applicantRestService.getQuestion(any(), any(), eq(questionResource.getId()))).thenReturn(newApplicantQuestionResource().build()));
        when(formInputViewModelGenerator.fromQuestion(any(), any())).thenReturn(Collections.emptyList());
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(ofNullable(organisations.get(0)));
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(newResearchCategoryResource().build(2)));
    }

    @Test
    public void testApplicationSummaryReadyForReviewAction() throws Exception {
        ApplicationResource app = applications.get(0);
        QuestionResource question = questionResources.get(questionResources.keySet().iterator().next());
        ProcessRoleResource processRole = processRoles.get(0);

        UserResource user = newUserResource().withId(1L).withFirstName("test").withLastName("name").build();
        when(processRoleService.findProcessRole(user.getId(), app.getId())).thenReturn(processRole);

        mockMvc.perform(post("/application/" + app.getId() + "/summary")
                .param(ASSIGN_QUESTION_PARAM, question.getId() + "_" + processRole.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/" + app.getId() + "/summary"));

        verify(questionService, times(1)).assignQuestion(eq(app.getId()), any(HttpServletRequest.class), any(ProcessRoleResource.class));
    }

    @Test
    public void testApplicationSummaryMarkAsCompleteAction() throws Exception {
        ApplicationResource app = applications.get(0);
        QuestionResource question = questionResources.get(questionResources.keySet().iterator().next());
        ProcessRoleResource processRole = processRoles.get(0);

        UserResource user = newUserResource().withId(1L).withFirstName("test").withLastName("name").build();
        when(processRoleService.findProcessRole(user.getId(), app.getId())).thenReturn(processRole);

        mockMvc.perform(post("/application/" + app.getId() + "/summary")
                .param(MARK_AS_COMPLETE, question.getId().toString())
                .param("formInput[" + question.getId().toString() + "]", "Test value"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/" + app.getId() + "/summary"));
        verify(questionService, times(1)).markAsComplete(question.getId(), app.getId(), user.getId());
    }

    @Test
    public void testApplicationSummaryMarkAsCompleteActionWithFailure() throws Exception {
        ApplicationResource app = applications.get(0);
        QuestionResource question = questionResources.get(questionResources.keySet().iterator().next());
        ProcessRoleResource processRole = processRoles.get(0);

        UserResource user = newUserResource().withId(1L).withFirstName("test").withLastName("name").build();
        when(processRoleService.findProcessRole(user.getId(), app.getId())).thenReturn(processRole);
        ValidationMessages validationMessages = new ValidationMessages();
        validationMessages.addError(Error.fieldError("asdf", new Error("as", HttpStatus.BAD_REQUEST)));
        when(questionService.markAsComplete(question.getId(), app.getId(), user.getId())).thenReturn(asList(validationMessages));

        mockMvc.perform(post("/application/" + app.getId() + "/summary")
                .param(MARK_AS_COMPLETE, question.getId().toString())
                .param("formInput[" + question.getId().toString() + "]", "Invalid value"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/" + app.getId() + "/form/question/edit/" + question.getId() + "?mark_as_complete=true"));
        verify(questionService, times(1)).markAsComplete(question.getId(), app.getId(), user.getId());
    }

    @Test
    public void testApplicationConfirmSubmit() throws Exception {
        ApplicationResource app = applications.get(0);

        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        mockMvc.perform(get("/application/" + app.getId() + "/confirm-submit"))
                .andExpect(view().name("application-confirm-submit"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("responses", formInputsToFormInputResponses));

    }

    @Test
    public void testApplicationSubmitAgreeingToTerms() throws Exception {
        ApplicationResource app = newApplicationResource().withId(1L).withCompetitionStatus(OPEN).build();
        when(userService.isLeadApplicant(users.get(0).getId(), app)).thenReturn(true);
        when(userService.getLeadApplicantProcessRoleOrNull(app.getId())).thenReturn(new ProcessRoleResource());

        when(applicationService.getById(app.getId())).thenReturn(app);
        when(applicationRestService.updateApplicationState(app.getId(), SUBMITTED)).thenReturn(restSuccess());
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));


        mockMvc.perform(post("/application/" + app.getId() + "/submit")
                .param("agreeTerms", "yes"))
                .andExpect(view().name("application-submitted"))
                .andExpect(model().attribute("currentApplication", app));

        verify(applicationRestService).updateApplicationState(app.getId(), SUBMITTED);
    }

    @Test
    public void testApplicationSubmitAppisNotSubmittable() throws Exception {
        ApplicationResource app = newApplicationResource().withId(1L).withCompetitionStatus(FUNDERS_PANEL).build();
        when(userService.isLeadApplicant(users.get(0).getId(), app)).thenReturn(true);
        when(userService.getLeadApplicantProcessRoleOrNull(app.getId())).thenReturn(new ProcessRoleResource());

        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));


        mockMvc.perform(post("/application/" + app.getId() + "/submit")
                .param("agreeTerms", "yes"))
                .andExpect(redirectedUrl("/application/1/confirm-submit"));

        verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("cannotSubmit"));
        verify(applicationRestService, never()).updateApplicationState(any(Long.class), any(ApplicationState.class));
    }

    @Test
    public void testApplicationTrack() throws Exception {
        ApplicationResource app = applications.get(0);
        app.setApplicationState(ApplicationState.SUBMITTED);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(competitionService.getById(anyLong())).thenReturn(competitionResource);

        mockMvc.perform(get("/application/" + app.getId() + "/track"))
                .andExpect(view().name("application-track"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", competitionResource));

    }

    @Test
    public void testNotSubmittedApplicationTrack() throws Exception {
        ApplicationResource app = applications.get(0);
        app.setApplicationState(ApplicationState.OPEN);

        when(applicationService.getById(app.getId())).thenReturn(app);
        when(competitionService.getById(app.getCompetition())).thenReturn(competitionResource);

        mockMvc.perform(get("/application/" + app.getId() + "/track"))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/application/" + app.getId()));
    }
}
