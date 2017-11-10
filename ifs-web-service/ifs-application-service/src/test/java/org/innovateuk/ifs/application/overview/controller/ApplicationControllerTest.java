package org.innovateuk.ifs.application.overview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.forms.populator.AssessorQuestionFeedbackPopulator;
import org.innovateuk.ifs.application.forms.populator.FeedbackNavigationPopulator;
import org.innovateuk.ifs.application.forms.viewmodel.AssessQuestionFeedbackViewModel;
import org.innovateuk.ifs.application.overview.populator.ApplicationOverviewModelPopulator;
import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewViewModel;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.SectionType;
import org.innovateuk.ifs.application.viewmodel.NavigationViewModel;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.applicant.builder.ApplicantQuestionResourceBuilder.newApplicantQuestionResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.ASSIGN_QUESTION_PARAM;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.assessment.builder.AssessmentFeedbackAggregateResourceBuilder.newAssessmentFeedbackAggregateResource;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.ASSESSOR_FEEDBACK;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.PROJECT_SETUP;
import static org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationControllerTest extends BaseControllerMockMVCTest<ApplicationController> {
    @Spy
    @InjectMocks
    private ApplicationOverviewModelPopulator applicationOverviewModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationModelPopulator applicationModelPopulator;

    @Spy
    @InjectMocks
    private AssessorQuestionFeedbackPopulator assessorQuestionFeedbackPopulator;

    @Spy
    @InjectMocks
    private FeedbackNavigationPopulator feedbackNavigationPopulator;

    @Spy
    @InjectMocks
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Override
    protected ApplicationController supplyControllerUnderTest() {
        return new ApplicationController();
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
        ApplicantResource applicant = newApplicantResource().withProcessRole(processRoles.get(0)).withOrganisation(organisations.get(0)).build();
        when(applicantRestService.getQuestion(anyLong(), anyLong(), anyLong())).thenReturn(newApplicantQuestionResource().withApplication(applications.get(0)).withCompetition(competitionResource).withCurrentApplicant(applicant).withApplicants(asList(applicant)).withQuestion(questionResources.values().iterator().next()).withCurrentUser(loggedInUser).build());

        when(applicationRestService.updateApplicationState(applications.get(0).getId(), ApplicationState.OPEN)).thenReturn(restSuccess());

        ApplicantSectionResourceBuilder sectionBuilder = newApplicantSectionResource().withApplication(applications.get(0)).withCompetition(competitionResource).withCurrentApplicant(applicant).withApplicants(asList(applicant)).withSection(newSectionResource().withType(SectionType.FINANCE).build()).withCurrentUser(loggedInUser);
        sectionResources.forEach(sectionResource -> {
            when(applicantRestService.getSection(anyLong(), anyLong(), eq(sectionResource.getId()))).thenReturn(sectionBuilder.withSection(sectionResource).build());
        });
         when(formInputViewModelGenerator.fromQuestion(any(), any())).thenReturn(Collections.emptyList());

        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(ofNullable(organisations.get(0)));
        when(categoryRestServiceMock.getResearchCategories()).thenReturn(restSuccess(newResearchCategoryResource().build(2)));
    }

    @Test
    public void testApplicationDetails() throws Exception {
        ApplicationResource app = applications.get(0);
        app.setCompetitionStatus(CompetitionStatus.OPEN);

        Set<Long> sections = newHashSet(1L, 2L);
        Map<Long, Set<Long>> mappedSections = new HashMap<>();
        mappedSections.put(organisations.get(0).getId(), sections);
        when(sectionService.getCompletedSectionsByOrganisation(anyLong())).thenReturn(mappedSections);
        when(applicationRestService.getApplicationById(app.getId())).thenReturn(restSuccess(app));
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        LOG.debug("Show dashboard for application: " + app.getId());
        Map<String, Object> model = mockMvc.perform(get("/application/" + app.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andReturn().getModelAndView().getModel();

        ApplicationOverviewViewModel viewModel = (ApplicationOverviewViewModel) model.get("model");

        assertEquals(app, viewModel.getCurrentApplication());
        assertEquals(sections, viewModel.getCompleted().getCompletedSections());
        assertEquals(competitionService.getById(app.getCompetition()), viewModel.getCurrentCompetition());

        assertTrue(viewModel.getAssignable().getPendingAssignableUsers().size() == 0);
    }

    @Test
    public void testApplicationDetailsAssign() throws Exception {
        ApplicationResource app = applications.get(0);

        when(applicationRestService.getApplicationById(app.getId())).thenReturn(restSuccess(app));
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        LOG.debug("Show dashboard for application: " + app.getId());
        mockMvc.perform(post("/application/" + app.getId()).param(ASSIGN_QUESTION_PARAM, "1_2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/" + app.getId()));
    }

    @Test
    public void testNonAcceptedInvitationsAffectPendingAssignableUsersAndPendingOrganisationNames() throws Exception {
        ApplicationResource app = applications.get(0);
        app.setCompetitionStatus(CompetitionStatus.OPEN);

        Set<Long> sections = newHashSet(1L, 2L);
        Map<Long, Set<Long>> mappedSections = new HashMap<>();
        mappedSections.put(organisations.get(0).getId(), sections);
        when(sectionService.getCompletedSectionsByOrganisation(anyLong())).thenReturn(mappedSections);
        when(applicationRestService.getApplicationById(app.getId())).thenReturn(restSuccess(app));
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        ApplicationInviteResource inv1 = inviteResource("kirk", "teamA", InviteStatus.CREATED);
        ApplicationInviteResource inv2 = inviteResource("spock", "teamA", InviteStatus.SENT);
        ApplicationInviteResource inv3 = inviteResource("bones", "teamA", InviteStatus.OPENED);

        ApplicationInviteResource inv4 = inviteResource("picard", "teamB", InviteStatus.CREATED);

        InviteOrganisationResource inviteOrgResource1 = inviteOrganisationResource(inv1, inv2, inv3);
        InviteOrganisationResource inviteOrgResource2 = inviteOrganisationResource(inv4);

        List<InviteOrganisationResource> inviteOrgResources = Arrays.asList(inviteOrgResource1, inviteOrgResource2);
        RestResult<List<InviteOrganisationResource>> invitesResult = RestResult.<List<InviteOrganisationResource>>restSuccess(inviteOrgResources, HttpStatus.OK);

        when(inviteRestService.getInvitesByApplication(app.getId())).thenReturn(invitesResult);

        LOG.debug("Show dashboard for application: " + app.getId());
        Map<String, Object> model = mockMvc.perform(get("/application/" + app.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andReturn().getModelAndView().getModel();

        ApplicationOverviewViewModel viewModel = (ApplicationOverviewViewModel) model.get("model");

        assertEquals(app, viewModel.getCurrentApplication());
        assertEquals(sections, viewModel.getCompleted().getCompletedSections());
        assertEquals(competitionService.getById(app.getCompetition()), viewModel.getCurrentCompetition());

        assertTrue(viewModel.getAssignable().getPendingAssignableUsers().size() == 3);
        assertTrue(viewModel.getAssignable().getPendingAssignableUsers().contains(inv1));
        assertTrue(viewModel.getAssignable().getPendingAssignableUsers().contains(inv2));
        assertTrue(viewModel.getAssignable().getPendingAssignableUsers().contains(inv4));
    }

    @Test
    public void testPendingOrganisationNamesOmitsEmptyOrganisationName() throws Exception {
        ApplicationResource app = applications.get(0);
        app.setCompetitionStatus(CompetitionStatus.OPEN);

        Set<Long> sections = newHashSet(1L, 2L);
        Map<Long, Set<Long>> mappedSections = new HashMap<>();
        mappedSections.put(organisations.get(0).getId(), sections);
        when(sectionService.getCompletedSectionsByOrganisation(anyLong())).thenReturn(mappedSections);
        when(applicationRestService.getApplicationById(app.getId())).thenReturn(restSuccess(app));
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        ApplicationInviteResource inv1 = inviteResource("kirk", "teamA", InviteStatus.CREATED);

        ApplicationInviteResource inv2 = inviteResource("picard", "", InviteStatus.CREATED);

        InviteOrganisationResource inviteOrgResource1 = inviteOrganisationResource(inv1);
        InviteOrganisationResource inviteOrgResource2 = inviteOrganisationResource(inv2);

        List<InviteOrganisationResource> inviteOrgResources = Arrays.asList(inviteOrgResource1, inviteOrgResource2);
        RestResult<List<InviteOrganisationResource>> invitesResult = RestResult.restSuccess(inviteOrgResources, HttpStatus.OK);

        when(inviteRestService.getInvitesByApplication(app.getId())).thenReturn(invitesResult);

        LOG.debug("Show dashboard for application: " + app.getId());
        Map<String, Object> model = mockMvc.perform(get("/application/" + app.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andReturn().getModelAndView().getModel();

        ApplicationOverviewViewModel viewModel = (ApplicationOverviewViewModel) model.get("model");

        assertEquals(app, viewModel.getCurrentApplication());
        assertEquals(sections, viewModel.getCompleted().getCompletedSections());
        assertEquals(competitionService.getById(app.getCompetition()), viewModel.getCurrentCompetition());

        assertTrue(viewModel.getAssignable().getPendingAssignableUsers().size() == 2);
        assertTrue(viewModel.getAssignable().getPendingAssignableUsers().contains(inv1));
        assertTrue(viewModel.getAssignable().getPendingAssignableUsers().contains(inv2));
    }

    @Test
    public void testPendingOrganisationNamesOmitsOrganisationNamesThatAreAlreadyCollaborators() throws Exception {
        ApplicationResource app = applications.get(0);
        app.setCompetitionStatus(CompetitionStatus.OPEN);

        Set<Long> sections = newHashSet(1L, 2L);
        Map<Long, Set<Long>> mappedSections = new HashMap<>();
        mappedSections.put(organisations.get(0).getId(), sections);
        when(sectionService.getCompletedSectionsByOrganisation(anyLong())).thenReturn(mappedSections);
        when(applicationRestService.getApplicationById(app.getId())).thenReturn(restSuccess(app));
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        ApplicationInviteResource inv1 = inviteResource("kirk", "teamA", InviteStatus.CREATED);
        ApplicationInviteResource inv2 = inviteResource("picard", organisations.get(0).getName(), InviteStatus.CREATED);

        InviteOrganisationResource inviteOrgResource1 = inviteOrganisationResource(inv1);
        InviteOrganisationResource inviteOrgResource2 = inviteOrganisationResource(inv2);

        List<InviteOrganisationResource> inviteOrgResources = Arrays.asList(inviteOrgResource1, inviteOrgResource2);
        RestResult<List<InviteOrganisationResource>> invitesResult = RestResult.restSuccess(inviteOrgResources, HttpStatus.OK);

        when(inviteRestService.getInvitesByApplication(app.getId())).thenReturn(invitesResult);

        LOG.debug("Show dashboard for application: " + app.getId());
        Map<String, Object> model = mockMvc.perform(get("/application/" + app.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andReturn().getModelAndView().getModel();

        ApplicationOverviewViewModel viewModel = (ApplicationOverviewViewModel) model.get("model");

        assertEquals(app, viewModel.getCurrentApplication());
        assertEquals(sections, viewModel.getCompleted().getCompletedSections());
        assertEquals(competitionService.getById(app.getCompetition()), viewModel.getCurrentCompetition());

        assertTrue(viewModel.getAssignable().getPendingAssignableUsers().size() == 2);
        assertTrue(viewModel.getAssignable().getPendingAssignableUsers().contains(inv1));
        assertTrue(viewModel.getAssignable().getPendingAssignableUsers().contains(inv2));
    }

    private InviteOrganisationResource inviteOrganisationResource(ApplicationInviteResource... invs) {
        InviteOrganisationResource ior = new InviteOrganisationResource();
        ior.setInviteResources(Arrays.asList(invs));
        return ior;
    }

    private ApplicationInviteResource inviteResource(String name, String organisation, InviteStatus status) {
        ApplicationInviteResource invRes = new ApplicationInviteResource();
        invRes.setName(name);
        invRes.setInviteOrganisationName(organisation);
        invRes.setStatus(status);
        return invRes;
    }

    @Test
    public void applicationAssessorQuestionFeedback() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        QuestionResource previousQuestion = newQuestionResource().withId(1L).withShortName("previous").build();
        QuestionResource questionResource = newQuestionResource().withId(questionId).build();
        QuestionResource nextQuestion = newQuestionResource().withId(3L).withShortName("next").build();
        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).withCompetitionStatus(PROJECT_SETUP).build();
        List<FormInputResponseResource> responseResources = newFormInputResponseResource().build(2);
        AssessmentFeedbackAggregateResource aggregateResource = newAssessmentFeedbackAggregateResource().build();
        NavigationViewModel expectedNavigation = new NavigationViewModel();
        expectedNavigation.setNextText("next");
        expectedNavigation.setNextUrl("/application/1/question/3/feedback");
        expectedNavigation.setPreviousText("previous");
        expectedNavigation.setPreviousUrl("/application/1/question/1/feedback");
        AssessQuestionFeedbackViewModel expectedModel =
                new AssessQuestionFeedbackViewModel(applicationResource, questionResource, responseResources, aggregateResource, expectedNavigation);

        when(questionService.getPreviousQuestion(questionId)).thenReturn(Optional.ofNullable(previousQuestion));
        when(questionService.getById(questionId)).thenReturn(questionResource);
        when(questionService.getNextQuestion(questionId)).thenReturn(Optional.ofNullable(nextQuestion));
        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(applicationResource));
        when(formInputResponseRestService.getByApplicationIdAndQuestionId(applicationId, questionId)).thenReturn(restSuccess(responseResources));
        when(assessorFormInputResponseRestService.getAssessmentAggregateFeedback(applicationId, questionId))
                .thenReturn(restSuccess(aggregateResource));

        mockMvc.perform(get("/application/{applicationId}/question/{questionId}/feedback", applicationId, questionId))
                .andExpect(status().isOk())
                .andExpect(view().name("application-assessor-feedback"))
                .andExpect(model().attribute("model", expectedModel));
    }

    @Test
    public void applicationAssessorQuestionFeedback_invalidState() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).withCompetitionStatus(ASSESSOR_FEEDBACK).build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(applicationResource));

        mockMvc.perform(get("/application/{applicationId}/question/{questionId}/feedback", applicationId, questionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/" + applicationId + "/summary"));
    }

    @Test
    public void testNotExistingApplicationDetails() throws Exception {
        ApplicationResource app = applications.get(0);

        when(env.acceptsProfiles("debug")).thenReturn(true);
        when(messageSource.getMessage(ObjectNotFoundException.class.getName(), null, Locale.ENGLISH)).thenReturn("Not found");
        when(applicationRestService.getApplicationById(app.getId())).thenReturn(restSuccess(app));
        when(applicationRestService.getApplicationById(1234L))
                .thenReturn(restFailure(new Error("Object not found", HttpStatus.NOT_FOUND)));

        LOG.debug("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/1234"))
                .andExpect(view().name("error"))
                .andExpect(model().attribute("url", "http://localhost/application/1234"))
                .andExpect(model().attributeExists("stacktrace"));
    }

    @Test
    public void testTeesAndCees() throws Exception {

        mockMvc.perform(get("/application/terms-and-conditions"))
                .andExpect(view().name("application-terms-and-conditions"));

    }
}
