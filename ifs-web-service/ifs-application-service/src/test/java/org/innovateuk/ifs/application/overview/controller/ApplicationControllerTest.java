package org.innovateuk.ifs.application.overview.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.service.ApplicantRestService;
import org.innovateuk.ifs.application.feedback.populator.AssessorQuestionFeedbackPopulator;
import org.innovateuk.ifs.application.feedback.populator.FeedbackNavigationPopulator;
import org.innovateuk.ifs.application.overview.populator.*;
import org.innovateuk.ifs.application.overview.viewmodel.*;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.populator.forminput.FormInputViewModelGenerator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.service.AssessorFormInputResponseRestService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.populator.OrganisationDetailsModelPopulator;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
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
import static org.innovateuk.ifs.application.forms.ApplicationFormUtil.ASSIGN_QUESTION_PARAM;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.category.builder.ResearchCategoryResourceBuilder.newResearchCategoryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationControllerTest extends AbstractApplicationMockMVCTest<ApplicationController> {
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

    @Spy
    @InjectMocks
    private OrganisationDetailsModelPopulator organisationDetailsModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationOverviewUserModelPopulator applicationOverviewUserModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationOverviewAssignableModelPopulator applicationOverviewAssignableModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationOverviewCompletedDetailsModelPopulator applicationOverviewCompletedDetailsModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationOverviewSectionModelPopulator applicationOverviewSectionModelPopulator;

    @Mock
    private ApplicantRestService applicantRestService;

    @Mock
    private FormInputViewModelGenerator formInputViewModelGenerator;

    @Mock
    private CategoryRestService categoryRestServiceMock;

    @Mock
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Mock
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Mock
    private ProjectService projectService;

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
                .andExpect(view().name("application-overview"))
                .andReturn().getModelAndView().getModel();

        ApplicationOverviewViewModel viewModel = (ApplicationOverviewViewModel) model.get("model");

        assertEquals(app.getId(), viewModel.getApplicationId());
        assertEquals(app.getName(), viewModel.getApplicationName());
        assertEquals(app.getApplicationState(), viewModel.getApplicationState());
        assertEquals(app.isSubmitted(), viewModel.isApplicationSubmitted());
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
                .andExpect(view().name("application-overview"))
                .andReturn().getModelAndView().getModel();

        ApplicationOverviewViewModel viewModel = (ApplicationOverviewViewModel) model.get("model");

        assertEquals(app.getId(), viewModel.getApplicationId());
        assertEquals(app.getName(), viewModel.getApplicationName());
        assertEquals(app.getApplicationState(), viewModel.getApplicationState());
        assertEquals(app.isSubmitted(), viewModel.isApplicationSubmitted());
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
                .andExpect(view().name("application-overview"))
                .andReturn().getModelAndView().getModel();

        ApplicationOverviewViewModel viewModel = (ApplicationOverviewViewModel) model.get("model");

        assertEquals(app.getId(), viewModel.getApplicationId());
        assertEquals(app.getName(), viewModel.getApplicationName());
        assertEquals(app.getApplicationState(), viewModel.getApplicationState());
        assertEquals(app.isSubmitted(), viewModel.isApplicationSubmitted());
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
                .andExpect(view().name("application-overview"))
                .andReturn().getModelAndView().getModel();

        ApplicationOverviewViewModel viewModel = (ApplicationOverviewViewModel) model.get("model");

        assertEquals(app.getId(), viewModel.getApplicationId());
        assertEquals(app.getName(), viewModel.getApplicationName());
        assertEquals(app.getApplicationState(), viewModel.getApplicationState());
        assertEquals(app.isSubmitted(), viewModel.isApplicationSubmitted());
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
    public void testApplicationDetails_applicationStateIsForwardedToOpenWhenLeadApplicationVisitsOverview() throws Exception {
        ApplicationResource app = applications.get(0);
        app.setApplicationState(ApplicationState.CREATED);
        app.setCompetitionStatus(CompetitionStatus.OPEN);

        ProcessRoleResource processRoleResource = newProcessRoleResource().withRole(LEADAPPLICANT).build();

        when(processRoleService.findProcessRole(this.loggedInUser.getId(), app.getId())).thenReturn(processRoleResource);
        when(applicationRestService.getApplicationById(app.getId())).thenReturn(restSuccess(app));

        mockMvc.perform(get("/application/" + app.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-overview"))
                .andReturn().getModelAndView().getModel();

        verify(applicationRestService, times(1)).updateApplicationState(app.getId(), ApplicationState.OPEN);
    }

    @Test
    public void testApplicationDetails_applicationStateIsNotForwardedToOpenWhenCollaboratorVisitsOverview() throws Exception {
        ApplicationResource app = applications.get(0);
        app.setApplicationState(ApplicationState.CREATED);
        app.setCompetitionStatus(CompetitionStatus.OPEN);

        ProcessRoleResource processRoleResource = newProcessRoleResource().withRole(COLLABORATOR).build();

        when(processRoleService.findProcessRole(this.loggedInUser.getId(), app.getId())).thenReturn(processRoleResource);
        when(applicationRestService.getApplicationById(app.getId())).thenReturn(restSuccess(app));

        mockMvc.perform(get("/application/" + app.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-overview"))
                .andReturn().getModelAndView().getModel();

        verify(applicationRestService, times(0)).updateApplicationState(app.getId(), ApplicationState.OPEN);
    }

    @Test
    public void testTeesAndCees() throws Exception {

        mockMvc.perform(get("/application/terms-and-conditions"))
                .andExpect(view().name("application-terms-and-conditions"));

    }
}
