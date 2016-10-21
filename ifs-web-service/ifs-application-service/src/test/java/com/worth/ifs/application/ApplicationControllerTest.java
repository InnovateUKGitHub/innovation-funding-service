package com.worth.ifs.application;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.constant.ApplicationStatusConstants;
import com.worth.ifs.application.populator.ApplicationModelPopulator;
import com.worth.ifs.application.model.ApplicationOverviewModelPopulator;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource.Status;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.invite.constant.InviteStatus;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.user.resource.ProcessRoleResource;
import com.worth.ifs.user.resource.UserResource;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.matchers.InstanceOf;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationControllerTest extends BaseControllerMockMVCTest<ApplicationController> {
    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Spy
    @InjectMocks
    private ApplicationOverviewModelPopulator applicationOverviewModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationModelPopulator applicationModelPopulator;

    @Override
    protected ApplicationController supplyControllerUnderTest() {
        return new ApplicationController();
    }

    @Before
    public void setUp(){
        super.setUp();

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
        this.setupInvites();
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(Optional.ofNullable(organisations.get(0)));
    }

    @Test
     public void testApplicationDetails() throws Exception {
        ApplicationResource app = applications.get(0);
        Set<Long> sections = newHashSet(1L,2L);
        Map<Long, Set<Long>> mappedSections = new HashMap();
        mappedSections.put(organisations.get(0).getId(), sections);
        when(sectionService.getCompletedSectionsByOrganisation(anyLong())).thenReturn(mappedSections);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));


        LOG.debug("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/" + app.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("completedSections", sections))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
                .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(0)));
    }

    @Test
    public void testApplicationDetailsAssign() throws Exception {
        ApplicationResource app = applications.get(0);

        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        LOG.debug("Show dashboard for application: " + app.getId());
        mockMvc.perform(post("/application/" + app.getId()).param(AbstractApplicationController.ASSIGN_QUESTION_PARAM, "1_2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/"+app.getId()));
    }
    
    @Test
    public void testNonAcceptedInvitationsAffectPendingAssignableUsersAndPendingOrganisationNames() throws Exception {
        ApplicationResource app = applications.get(0);
        Set<Long> sections = newHashSet(1L, 2L);
        Map<Long, Set<Long>> mappedSections = new HashMap();
        mappedSections.put(organisations.get(0).getId(), sections);
        when(sectionService.getCompletedSectionsByOrganisation(anyLong())).thenReturn(mappedSections);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        ApplicationInviteResource inv1 = inviteResource("kirk", "teamA", InviteStatus.CREATED);
        ApplicationInviteResource inv2 = inviteResource("spock", "teamA", InviteStatus.SENT);
        ApplicationInviteResource inv3 = inviteResource("bones", "teamA",  InviteStatus.OPENED);

        ApplicationInviteResource inv4 = inviteResource("picard", "teamB", InviteStatus.CREATED);
       
        InviteOrganisationResource inviteOrgResource1 = inviteOrganisationResource(inv1, inv2, inv3);
        InviteOrganisationResource inviteOrgResource2 = inviteOrganisationResource(inv4);
       
        List<InviteOrganisationResource> inviteOrgResources = Arrays.asList(inviteOrgResource1, inviteOrgResource2);
        RestResult<List<InviteOrganisationResource>> invitesResult = RestResult.<List<InviteOrganisationResource>>restSuccess(inviteOrgResources, HttpStatus.OK);
       
        when(inviteRestService.getInvitesByApplication(app.getId())).thenReturn(invitesResult);
       
        LOG.debug("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/" + app.getId()))
               .andExpect(status().isOk())
               .andExpect(view().name("application-details"))
               .andExpect(model().attribute("currentApplication", app))
               .andExpect(model().attribute("completedSections", sections))
               .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(3)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv1)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv2)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv4)));
   }
    
    @Test
    public void testPendingOrganisationNamesOmitsEmptyOrganisationName() throws Exception {
        ApplicationResource app = applications.get(0);
        Set<Long> sections = newHashSet(1L, 2L);
        Map<Long, Set<Long>> mappedSections = new HashMap();
        mappedSections.put(organisations.get(0).getId(), sections);
        when(sectionService.getCompletedSectionsByOrganisation(anyLong())).thenReturn(mappedSections);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        ApplicationInviteResource inv1 = inviteResource("kirk", "teamA", InviteStatus.CREATED);

        ApplicationInviteResource inv2 = inviteResource("picard", "", InviteStatus.CREATED);
       
       InviteOrganisationResource inviteOrgResource1 = inviteOrganisationResource(inv1);
       InviteOrganisationResource inviteOrgResource2 = inviteOrganisationResource(inv2);
       
       List<InviteOrganisationResource> inviteOrgResources = Arrays.asList(inviteOrgResource1, inviteOrgResource2);
       RestResult<List<InviteOrganisationResource>> invitesResult = RestResult.<List<InviteOrganisationResource>>restSuccess(inviteOrgResources, HttpStatus.OK);
       
       when(inviteRestService.getInvitesByApplication(app.getId())).thenReturn(invitesResult);
       
       LOG.debug("Show dashboard for application: " + app.getId());
       mockMvc.perform(get("/application/" + app.getId()))
               .andExpect(status().isOk())
               .andExpect(view().name("application-details"))
               .andExpect(model().attribute("currentApplication", app))
               .andExpect(model().attribute("completedSections", sections))
               .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(2)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv1)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv2)));
   }
    
    @Test
    public void testPendingOrganisationNamesOmitsOrganisationNamesThatAreAlreadyCollaborators() throws Exception {
       ApplicationResource app = applications.get(0);
        Set<Long> sections = newHashSet(1L, 2L);
        Map<Long, Set<Long>> mappedSections = new HashMap();
        mappedSections.put(organisations.get(0).getId(), sections);
        when(sectionService.getCompletedSectionsByOrganisation(anyLong())).thenReturn(mappedSections);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));


        ApplicationInviteResource inv1 = inviteResource("kirk", "teamA", InviteStatus.CREATED);

        ApplicationInviteResource inv2 = inviteResource("picard", organisations.get(0).getName(), InviteStatus.CREATED);
       
        InviteOrganisationResource inviteOrgResource1 = inviteOrganisationResource(inv1);
        InviteOrganisationResource inviteOrgResource2 = inviteOrganisationResource(inv2);
       
       
       
        List<InviteOrganisationResource> inviteOrgResources = Arrays.asList(inviteOrgResource1, inviteOrgResource2);
        RestResult<List<InviteOrganisationResource>> invitesResult = RestResult.<List<InviteOrganisationResource>>restSuccess(inviteOrgResources, HttpStatus.OK);

        when(inviteRestService.getInvitesByApplication(app.getId())).thenReturn(invitesResult);
       
        LOG.debug("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/" + app.getId()))
               .andExpect(status().isOk())
               .andExpect(view().name("application-details"))
               .andExpect(model().attribute("currentApplication", app))
               .andExpect(model().attribute("completedSections", sections))
               .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(2)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv1)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv2)));
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
    public void testApplicationSummary() throws Exception {
        ApplicationResource app = applications.get(0);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));
        mockMvc.perform(get("/application/" + app.getId()+"/summary"))
                .andExpect(status().isOk())
                .andExpect(view().name("application-summary"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition",  competitionService.getById(app.getCompetition())))
                .andExpect(model().attribute("leadOrganisation", organisations.get(0)))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasSize(organisations.size())))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(organisations.get(0))))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(organisations.get(1))))
                .andExpect(model().attribute("responses", formInputsToFormInputResponses))
                .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(0)))
                .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasSize(0)));
    }

    @Test
    public void testApplicationSummaryReadyForReviewAction() throws Exception {
        ApplicationResource app = applications.get(0);
        QuestionResource question = questionResources.get(questionResources.keySet().iterator().next());
        ProcessRoleResource processRole = processRoles.get(0);

        UserResource user = newUserResource().withId(1L).withFirstName("test").withLastName("name").build();
        when(processRoleService.findProcessRole(user.getId(), app.getId())).thenReturn(processRole);

        mockMvc.perform(post("/application/" + app.getId() + "/summary")
                .param(AbstractApplicationController.ASSIGN_QUESTION_PARAM, question.getId() + "_" + processRole.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/" + app.getId() + "/summary"));

        verify(questionService, times(1)).assignQuestion(Mockito.eq(app.getId()), Mockito.any(), Mockito.any());
    }

    @Test
    public void testApplicationSummaryMarkAsCompleteAction() throws Exception {
        ApplicationResource app = applications.get(0);
        QuestionResource question = questionResources.get(questionResources.keySet().iterator().next());
        ProcessRoleResource processRole = processRoles.get(0);

        UserResource user = newUserResource().withId(1L).withFirstName("test").withLastName("name").build();
        when(processRoleService.findProcessRole(user.getId(), app.getId())).thenReturn(processRole);

        mockMvc.perform(post("/application/" + app.getId() + "/summary")
                .param(AbstractApplicationController.MARK_AS_COMPLETE, question.getId().toString())
                .param("formInput[" + question.getId().toString() + "]", "Test value"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/" + app.getId() + "/summary"));
        verify(questionService, times(1)).markAsComplete(question.getId(), app.getId(), user.getId());
    }

    @Test
    public void testNotExistingApplicationDetails() throws Exception {
        ApplicationResource app = applications.get(0);

        when(env.acceptsProfiles("debug")).thenReturn(true);
        when(messageSource.getMessage(ObjectNotFoundException.class.getName(), null, Locale.ENGLISH)).thenReturn("Not found");
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(applicationService.getById(1234l)).thenThrow(new ObjectNotFoundException("1234 not found", Collections.singletonList(1234L)));

        List<Object> arguments = new ArrayList<>();
        arguments.add("Application");
        arguments.add(1234l);

        LOG.debug("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/1234"))
                .andExpect(view().name("404"))
                .andExpect(model().attribute("url", "http://localhost/application/1234"))
                .andExpect(model().attribute("exception", new InstanceOf(ObjectNotFoundException.class)))
                .andExpect(model().attribute("message","1234 not found"))
                .andExpect(model().attributeExists("stacktrace"));
    }

    @Test
    public void testApplicationDetailsOpenSection() throws Exception {
        ApplicationResource app = applications.get(0);
        SectionResource section = sectionResources.get(2);

        Map<Long, SectionResource> collectedSections =
                sectionResources.stream().collect(Collectors.toMap(SectionResource::getId,
                        Function.identity()));

        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        LOG.debug("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/" + app.getId() +"/section/"+ section.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
                .andExpect(model().attribute("sections", collectedSections))
                .andExpect(model().attribute("currentSectionId", section.getId()))
                .andExpect(model().attribute("leadOrganisation", organisations.get(0)))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasSize(organisations.size())))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(organisations.get(0))))
                .andExpect(model().attribute("applicationOrganisations", Matchers.hasItem(organisations.get(1))))
                .andExpect(model().attribute("responses", formInputsToFormInputResponses))
                .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(0)))
                .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasSize(0)));
    }

    @Test
    public void testApplicationConfirmSubmit() throws Exception {
            ApplicationResource app = applications.get(0);

            when(applicationService.getById(app.getId())).thenReturn(app);
            when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

            mockMvc.perform(get("/application/1/confirm-submit"))
                    .andExpect(view().name("application-confirm-submit"))
                    .andExpect(model().attribute("currentApplication", app))
                    .andExpect(model().attribute("responses", formInputsToFormInputResponses));

    }

    @Test
    public void testApplicationSubmitAgreeingToTerms() throws Exception {
        ApplicationResource app = newApplicationResource().withId(1L).withCompetitionStatus(Status.OPEN).build();
        when(userService.isLeadApplicant(users.get(0).getId(), app)).thenReturn(true);
        when(userService.getLeadApplicantProcessRoleOrNull(app)).thenReturn(new ProcessRoleResource());

        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        
        MvcResult result = mockMvc.perform(post("/application/1/submit")
        		.param("agreeTerms", "yes"))
                .andExpect(view().name("application-submitted"))
                .andExpect(model().attribute("currentApplication", app))
                .andReturn();
        
        verify(applicationService).updateStatus(app.getId(), ApplicationStatusConstants.SUBMITTED.getId());
    }
    
    @Test
    public void testApplicationSubmitAppisNotSubmittable() throws Exception {
        ApplicationResource app = newApplicationResource().withId(1L).withCompetitionStatus(Status.FUNDERS_PANEL).build();
        when(userService.isLeadApplicant(users.get(0).getId(), app)).thenReturn(true);
        when(userService.getLeadApplicantProcessRoleOrNull(app)).thenReturn(new ProcessRoleResource());

        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        
        mockMvc.perform(post("/application/1/submit")
        		.param("agreeTerms", "yes"))
        		.andExpect(redirectedUrl("/application/1/confirm-submit"));
        
        verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("cannotSubmit"));
        verify(applicationService, never()).updateStatus(any(Long.class), any(Long.class));
    }

    @Test
    public void testApplicationCreateView() throws Exception {
        MvcResult result = mockMvc.perform(get("/application/create/1"))
                .andExpect(view().name("application-create"))
                .andReturn();
    }

    @Test
     public void testApplicationCreateWithoutApplicationName() throws Exception {
        ApplicationResource application = new ApplicationResource();
        application.setName("application");

        UserResource user = newUserResource().withId(1L).withFirstName("test").withLastName("name").build();

        when(userAuthenticationService.getAuthenticatedUser(anyObject())).thenReturn(user);
        when(applicationService.createApplication(eq(1L), eq(1L), anyString())).thenReturn(application);
        MvcResult result = mockMvc.perform(post("/application/create/1").param("application_name", ""))
                .andExpect(view().name("application-create"))
                .andExpect(model().attribute("applicationNameEmpty", true))
                .andReturn();
    }

    @Test
    public void testApplicationCreateWithWhitespaceAsApplicationName() throws Exception {
        ApplicationResource application = new ApplicationResource();
        application.setName("application");

        UserResource user = newUserResource().withId(1L).withFirstName("test").withLastName("name").build();

        when(userAuthenticationService.getAuthenticatedUser(anyObject())).thenReturn(user);
        when(applicationService.createApplication(eq(1L), eq(1L), anyString())).thenReturn(application);
        mockMvc.perform(post("/application/create/1").param("application_name", "     "))
                .andExpect(view().name("application-create"))
                .andExpect(model().attribute("applicationNameEmpty", true));
    }

    @Test
    public void testApplicationCreateWithApplicationName() throws Exception {
        ApplicationResource application = new ApplicationResource();
        application.setName("application");
        application.setId(1L);

        UserResource user = newUserResource().withId(1L).withFirstName("test").withLastName("name").build();

        when(userAuthenticationService.getAuthenticatedUser(anyObject())).thenReturn(user);
        when(applicationService.createApplication(eq(1L), eq(1L), anyString())).thenReturn(application);
        mockMvc.perform(post("/application/create/1").param("application_name", "testApplication"))
                .andExpect(view().name("redirect:/application/"+application.getId()))
                .andExpect(model().attributeDoesNotExist("applicationNameEmpty"));
    }

    @Test
    public void testApplicationCreateConfirmCompetitionView() throws Exception {
        mockMvc.perform(get("/application/create-confirm-competition"))
                .andExpect(view().name("application-create-confirm-competition"));
    }

    @Test
    public void testDownloadAssessorFeedback() throws Exception {

        ByteArrayResource fileContents = new ByteArrayResource("File contents".getBytes());
        FileEntryResource fileEntry = newFileEntryResource().withMediaType("text/special").build();

        when(assessorFeedbackRestService.getAssessorFeedbackFile(123L)).thenReturn(restSuccess(fileContents));
        when(assessorFeedbackRestService.getAssessorFeedbackFileDetails(123L)).thenReturn(restSuccess(fileEntry));

        mockMvc.perform(get("/application/123/assessorFeedback"))
            .andExpect(status().isOk())
            .andExpect(content().string("File contents"))
            .andExpect(header().string("Content-Type", "text/special"))
            .andExpect(header().longValue("Content-Length", "File contents".length()));

        verify(assessorFeedbackRestService).getAssessorFeedbackFile(123L);
        verify(assessorFeedbackRestService).getAssessorFeedbackFileDetails(123L);
    }
}