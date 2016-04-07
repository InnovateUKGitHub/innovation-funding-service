package com.worth.ifs.application;

import com.worth.ifs.Application;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.SectionResource;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.InstanceOf;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
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
    }

    @Test
     public void testApplicationDetails() throws Exception {
        ApplicationResource app = applications.get(0);

       // when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        LOG.debug("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/" + app.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("application-details"))
                .andExpect(model().attribute("currentApplication", app))
                .andExpect(model().attribute("completedSections", Arrays.asList(1L, 2L)))
                .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
                .andExpect(model().attribute("responses", formInputsToFormInputResponses))
                .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(0)))
                .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasSize(0)));
    }

    @Test
    public void testApplicationDetailsAssign() throws Exception {
        ApplicationResource app = applications.get(0);

        // when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
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

       when(applicationService.getById(app.getId())).thenReturn(app);
       when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

       InviteResource inv1 = inviteResource("kirk", "teamA", InviteStatusConstants.CREATED);
       InviteResource inv2 = inviteResource("spock", "teamA", InviteStatusConstants.SEND);
       InviteResource inv3 = inviteResource("bones", "teamA",  InviteStatusConstants.ACCEPTED);
       
       InviteResource inv4 = inviteResource("picard", "teamB", InviteStatusConstants.CREATED);
       
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
               .andExpect(model().attribute("completedSections", Arrays.asList(1L, 2L)))
               .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
               .andExpect(model().attribute("responses", formInputsToFormInputResponses))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(3)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv1)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv2)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv4)))
               .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasSize(2)))
               .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasItem("teamA")))
               .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasItem("teamB")));
   }
    
    @Test
    public void testPendingOrganisationNamesOmitsEmptyOrganisationName() throws Exception {
       ApplicationResource app = applications.get(0);

       when(applicationService.getById(app.getId())).thenReturn(app);
       when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

       InviteResource inv1 = inviteResource("kirk", "teamA", InviteStatusConstants.CREATED);
       
       InviteResource inv2 = inviteResource("picard", "", InviteStatusConstants.CREATED);
       
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
               .andExpect(model().attribute("completedSections", Arrays.asList(1L, 2L)))
               .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
               .andExpect(model().attribute("responses", formInputsToFormInputResponses))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(2)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv1)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv2)))
               .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasSize(1)))
               .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasItem("teamA")));
   }
    
    @Test
    public void testPendingOrganisationNamesOmitsOrganisationNamesThatAreAlreadyCollaborators() throws Exception {
       ApplicationResource app = applications.get(0);

       when(applicationService.getById(app.getId())).thenReturn(app);
       when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

       InviteResource inv1 = inviteResource("kirk", "teamA", InviteStatusConstants.CREATED);
       
       InviteResource inv2 = inviteResource("picard", organisations.get(0).getName(), InviteStatusConstants.CREATED);
       
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
               .andExpect(model().attribute("completedSections", Arrays.asList(1L, 2L)))
               .andExpect(model().attribute("currentCompetition", competitionService.getById(app.getCompetition())))
               .andExpect(model().attribute("responses", formInputsToFormInputResponses))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasSize(2)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv1)))
               .andExpect(model().attribute("pendingAssignableUsers", Matchers.hasItem(inv2)))
               .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasSize(1)))
               .andExpect(model().attribute("pendingOrganisationNames", Matchers.hasItem("teamA")));
   }

    private InviteOrganisationResource inviteOrganisationResource(InviteResource... invs) {
    	InviteOrganisationResource ior = new InviteOrganisationResource();
    	ior.setInviteResources(Arrays.asList(invs));
		return ior;
	}

	private InviteResource inviteResource(String name, String organisation, InviteStatusConstants status) {
		InviteResource invRes = new InviteResource();
		invRes.setName(name);
		invRes.setInviteOrganisationName(organisation);
		invRes.setStatus(status);
		return invRes;
	}

	@Test
    public void testApplicationSummary() throws Exception {
        ApplicationResource app = applications.get(0);
        //when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
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
    public void testNotExistingApplicationDetails() throws Exception {
        ApplicationResource app = applications.get(0);

        when(env.acceptsProfiles("uat", "dev", "test")).thenReturn(true);
        when(messageSource.getMessage(ObjectNotFoundException.class.getName(), null, Locale.ENGLISH)).thenReturn(
                testMessageSource().getMessage(ObjectNotFoundException.class.getName(), null, Locale.ENGLISH));
        when(applicationService.getById(app.getId())).thenReturn(app);
        when(applicationService.getById(1234l)).thenThrow(new ObjectNotFoundException(testMessageSource().getMessage
                (ObjectNotFoundException.class.getName(), null, Locale.ENGLISH), Arrays.asList(1234l)));

        List<Object> arguments = new ArrayList<>();
        arguments.add(Application.class.getName());
        arguments.add(1234l);

        LOG.debug("Show dashboard for application: " + app.getId());
        mockMvc.perform(get("/application/1234"))
                .andExpect(view().name("404"))
                .andExpect(model().attribute("url", "http://localhost/application/1234"))
                .andExpect(model().attribute("exception", new InstanceOf(ObjectNotFoundException.class)))
                .andExpect(model().attribute("message",
                        testMessageSource().getMessage(ObjectNotFoundException.class.getName(), arguments.toArray(), Locale.ENGLISH)))
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

            //when(applicationService.getApplicationsByUserId(loggedInUser.getId())).thenReturn(applications);
            when(applicationService.getById(app.getId())).thenReturn(app);
            when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

            mockMvc.perform(get("/application/1/confirm-submit"))
                    .andExpect(view().name("application-confirm-submit"))
                    .andExpect(model().attribute("currentApplication", app))
                    .andExpect(model().attribute("responses", formInputsToFormInputResponses));

    }

    @Test
    public void testApplicationSubmitWithoutAgreeingToTerms() throws Exception {

        mockMvc.perform(post("/application/1/submit"))
                .andExpect(redirectedUrl("/application/1/confirm-submit"));
          
        verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("agreeToTerms"));
        verifyNoMoreInteractions(userAuthenticationService, applicationService, questionService);
        
    }
    
    @Test
    public void testApplicationSubmitAgreeingToTerms() throws Exception {
        ApplicationResource app = applications.get(0);

        when(applicationService.getById(app.getId())).thenReturn(app);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        MvcResult result = mockMvc.perform(post("/application/1/submit")
        		.param("agreeTerms", "yes"))
                .andExpect(view().name("application-submitted"))
                .andExpect(model().attribute("currentApplication", app))
                .andReturn();

        // TODO: test the application status, but how without having a database in place?
        //        Application updatedApplication = (Application) result.getModelAndView().getModel().get("currentApplication");
        //        String name = updatedApplication.getApplicationStatus().getName();
        //        assertEquals(name, "submitted");
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
}