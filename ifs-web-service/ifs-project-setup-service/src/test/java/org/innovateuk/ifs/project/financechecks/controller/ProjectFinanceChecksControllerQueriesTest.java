package org.innovateuk.ifs.project.financechecks.controller;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.financechecks.form.FinanceChecksQueryResponseForm;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.status.StatusService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModelPopulator;
import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.user.resource.FinanceUtil;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.innovateuk.ifs.util.CookieUtil;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.CookieTestUtil.*;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectFinanceChecksControllerQueriesTest extends BaseControllerMockMVCTest<ProjectFinanceChecksController> {

    private Long projectId = 123L;
    private Long organisationId = 234L;
    private Long projectFinanceId = 45L;
    private Long queryId = 1L;

    ApplicationResource applicationResource = newApplicationResource().build();

    OrganisationResource innovateOrganisationResource = newOrganisationResource().withName("Innovate").withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();

    OrganisationResource leadOrganisationResource = newOrganisationResource().withName("Org1").withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).withId(organisationId).build();

    UserResource financeTeamUser = newUserResource().withFirstName("A").withLastName("Z").withRolesGlobal(singletonList(PROJECT_FINANCE)).build();
    UserResource financeContactUser = newUserResource().withFirstName("B").withLastName("Z").build();

    ProjectUserResource financeContactProjectUser = newProjectUserResource().withOrganisation(organisationId).withUserName("User1").withUser(financeContactUser.getId()).withEmail("e@mail.com").withPhoneNumber("0117").withRole(FINANCE_CONTACT).build();

    ProjectPartnerStatusResource statusResource = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE)
            .withFinanceContactStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).build();
    ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().withPartnerStatuses(singletonList(statusResource)).build();
    OrganisationResource partnerOrganisation = newOrganisationResource().withId(organisationId).withOrganisationType(OrganisationTypeEnum.RESEARCH.getId()).build();
    ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(organisationId).withId(projectFinanceId).build();

    ProjectResource project = newProjectResource().withId(projectId).withName("Project1").
            withApplication(applicationResource).
            withProjectUsers(singletonList(financeContactProjectUser.getId())).build();

    QueryResource thread;
    QueryResource thread2;
    QueryResource thread3;

    List<QueryResource> queries;

    @Captor
    ArgumentCaptor<PostResource> savePostArgumentCaptor;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private UserService userService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private ProjectService projectService;

    @Mock
    private StatusService statusService;

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Mock
    private FinanceCheckService financeCheckServiceMock;

    @Mock
    private FinanceUtil financeUtil;

    private ThreadViewModelPopulator threadViewModelPopulator;

    @Before
    public void setup() {
        super.setUp();
        setupCookieUtil(cookieUtil);

        threadViewModelPopulator = new ThreadViewModelPopulator(organisationService);
        spy(threadViewModelPopulator);
        controller.setThreadViewModelPopulator(threadViewModelPopulator);

        when(userService.findById(financeTeamUser.getId())).thenReturn(financeTeamUser);
        when(organisationService.getOrganisationForUser(financeTeamUser.getId())).thenReturn(innovateOrganisationResource);
        when(userService.findById(financeContactUser.getId())).thenReturn(financeContactUser);
        when(organisationService.getOrganisationForUser(financeContactUser.getId())).thenReturn(leadOrganisationResource);
        when(userService.findById(financeContactUser.getId())).thenReturn(financeContactUser);

        // populate viewmodel
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(leadOrganisationResource);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisationResource);

        PostResource firstPost = new PostResource(null, financeTeamUser, "Question", singletonList(new AttachmentResource(23L, "file1.txt", "txt", 1L, null)), ZonedDateTime.now().plusMinutes(10L));
        PostResource firstResponse = new PostResource(null, financeContactUser, "Response", new ArrayList<>(), ZonedDateTime.now().plusMinutes(20L));
        thread = new QueryResource(1L, projectFinanceId, asList(firstPost, firstResponse), FinanceChecksSectionType.ELIGIBILITY, "Query title", false, ZonedDateTime.now(), null, null);

        PostResource firstPost2 = new PostResource(null, financeTeamUser, "Question2", new ArrayList<>(), ZonedDateTime.now().minusMinutes(15L));
        thread2 = new QueryResource(3L, projectFinanceId, singletonList(firstPost2), FinanceChecksSectionType.ELIGIBILITY, "Query2 title", true, ZonedDateTime.now(), null, null);

        PostResource firstPost1 = new PostResource(null, financeTeamUser, "Question3", new ArrayList<>(), ZonedDateTime.now());
        PostResource firstResponse1 = new PostResource(null, financeContactUser, "Response3", new ArrayList<>(), ZonedDateTime.now().plusMinutes(10L));

        thread3 = new QueryResource(5L, projectFinanceId, asList(firstPost1, firstResponse1), FinanceChecksSectionType.ELIGIBILITY, "Query title3", false, ZonedDateTime.now(), null, ZonedDateTime.now().minusDays(5L));

        queries = asList(thread2, thread, thread3);
    }

    @Test
    public void testViewFinanceChecksLandingPage() throws Exception {

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(Collections.emptyList()));
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRole(PARTNER).build(1));
        when(projectService.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

        MvcResult result = mockMvc.perform(get("/project/123/finance-checks")).
                andExpect(view().name("project/finance-checks")).
                andReturn();

        ProjectFinanceChecksViewModel model = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(project.getId(), model.getProjectId());
        assertEquals(partnerOrganisation.getId(), model.getOrganisationId());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals(Collections.emptyList(), model.getAwaitingResponseQueries());
        assertEquals(Collections.emptyList(), model.getPendingQueries());
        assertEquals(Collections.emptyList(), model.getClosedQueries());
        assertFalse(model.isApproved());
    }

    @Test
    public void testViewFinanceChecksWithQueries() throws Exception {
        setLoggedInUser(financeContactUser);
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(financeContactUser.getId()).withOrganisation(organisationId).withRole(PARTNER).build(1));
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(projectService.getOrganisationIdFromUser(projectId, financeContactUser)).thenReturn(organisationId);

        MvcResult result = mockMvc.perform(get("/project/123/finance-checks")).
                andExpect(view().name("project/finance-checks")).
                andReturn();

        ProjectFinanceChecksViewModel model = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(project.getId(), model.getProjectId());
        assertEquals(partnerOrganisation.getId(), model.getOrganisationId());
        assertEquals(project.getName(), model.getProjectName());
        assertFalse(model.isApproved());
        assertEquals(organisationId, model.getOrganisationId());
        assertEquals(projectId, model.getProjectId());
        assertEquals(1, model.getAwaitingResponseQueries().size());
        assertEquals("Query title", model.getAwaitingResponseQueries().get(0).getTitle());
        assertEquals(true, model.getAwaitingResponseQueries().get(0).isLastPostByExternalUser());
        assertEquals(organisationId, model.getAwaitingResponseQueries().get(0).getOrganisationId());
        assertEquals(projectId, model.getAwaitingResponseQueries().get(0).getProjectId());
        assertEquals(1L, model.getAwaitingResponseQueries().get(0).getId().longValue());
        assertEquals(2, model.getAwaitingResponseQueries().get(0).getViewModelPosts().size());
        assertEquals("Question", model.getAwaitingResponseQueries().get(0).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUser.getId(), model.getAwaitingResponseQueries().get(0).getViewModelPosts().get(0).author.getId());
        assertEquals("Innovate UK - Finance team", model.getAwaitingResponseQueries().get(0).getViewModelPosts().get(0).getUsername());
        assertTrue(ZonedDateTime.now().plusMinutes(10L).isAfter(model.getAwaitingResponseQueries().get(0).getViewModelPosts().get(0).createdOn));
        assertEquals(1, model.getAwaitingResponseQueries().get(0).getViewModelPosts().get(0).attachments.size());
        assertEquals(23L, model.getAwaitingResponseQueries().get(0).getViewModelPosts().get(0).attachments.get(0).id.longValue());
        assertEquals("file1.txt", model.getAwaitingResponseQueries().get(0).getViewModelPosts().get(0).attachments.get(0).name);
        assertEquals("Response", model.getAwaitingResponseQueries().get(0).getViewModelPosts().get(1).body);
        assertEquals(financeContactUser.getId(), model.getAwaitingResponseQueries().get(0).getViewModelPosts().get(1).author.getId());
        assertEquals("B Z - Org1", model.getAwaitingResponseQueries().get(0).getViewModelPosts().get(1).getUsername());
        assertTrue(ZonedDateTime.now().plusMinutes(20L).isAfter(model.getAwaitingResponseQueries().get(0).getViewModelPosts().get(1).createdOn));
        assertEquals(0, model.getAwaitingResponseQueries().get(0).getViewModelPosts().get(1).attachments.size());

        assertEquals(1, model.getClosedQueries().size());
        assertEquals("Query title3", model.getClosedQueries().get(0).getTitle());
        assertEquals(true, model.getClosedQueries().get(0).isClosed());
        assertEquals(organisationId, model.getClosedQueries().get(0).getOrganisationId());
        assertEquals(projectId, model.getClosedQueries().get(0).getProjectId());
        assertEquals(5L, model.getClosedQueries().get(0).getId().longValue());
        assertEquals(2, model.getClosedQueries().get(0).getViewModelPosts().size());
        assertEquals("Question3", model.getClosedQueries().get(0).getViewModelPosts().get(0).body);
        assertEquals("Response3", model.getClosedQueries().get(0).getViewModelPosts().get(1).body);
        assertEquals(financeTeamUser.getId(), model.getClosedQueries().get(0).getViewModelPosts().get(0).author.getId());
        assertEquals("Innovate UK - Finance team", model.getClosedQueries().get(0).getViewModelPosts().get(0).getUsername());
        assertTrue(ZonedDateTime.now().plusMinutes(15L).isAfter(model.getClosedQueries().get(0).getViewModelPosts().get(0).createdOn));
        assertEquals(0, model.getClosedQueries().get(0).getViewModelPosts().get(0).attachments.size());
        assertEquals(financeContactUser.getId(), model.getClosedQueries().get(0).getViewModelPosts().get(1).author.getId());
        assertEquals("B Z - Org1", model.getClosedQueries().get(0).getViewModelPosts().get(1).getUsername());
        assertTrue(ZonedDateTime.now().plusMinutes(10L).isAfter(model.getClosedQueries().get(0).getViewModelPosts().get(1).createdOn));
        assertEquals(0, model.getClosedQueries().get(0).getViewModelPosts().get(1).attachments.size());

        assertEquals(1, model.getPendingQueries().size());
        assertEquals("Query2 title", model.getPendingQueries().get(0).getTitle());
        assertEquals(false, model.getPendingQueries().get(0).isLastPostByExternalUser());
        assertEquals(organisationId, model.getPendingQueries().get(0).getOrganisationId());
        assertEquals(projectId, model.getPendingQueries().get(0).getProjectId());
        assertEquals(3, model.getPendingQueries().get(0).getId().longValue());
        assertEquals(1, model.getPendingQueries().get(0).getViewModelPosts().size());
        assertEquals("Question2", model.getPendingQueries().get(0).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUser.getId(), model.getPendingQueries().get(0).getViewModelPosts().get(0).author.getId());
        assertEquals("Innovate UK - Finance team", model.getPendingQueries().get(0).getViewModelPosts().get(0).getUsername());
        assertTrue(ZonedDateTime.now().isAfter(model.getPendingQueries().get(0).getViewModelPosts().get(0).createdOn));
        assertEquals(0, model.getPendingQueries().get(0).getViewModelPosts().get(0).attachments.size());

    }

    @Test
    public void testViewFinanceChecksLandingPageApproved() throws Exception {
        ProjectPartnerStatusResource statusResource = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE)
                .withFinanceContactStatus(ProjectActivityStates.COMPLETE).withFinanceChecksStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).build();
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().withPartnerStatuses(singletonList(statusResource)).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().withId(organisationId).withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(organisationId).withId(projectFinanceId).build();

        when(projectService.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(Collections.emptyList()));

        MvcResult result = mockMvc.perform(get("/project/123/finance-checks")).
                andExpect(view().name("project/finance-checks")).
                andReturn();

        ProjectFinanceChecksViewModel model = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(model.getProjectId(), project.getId());
        assertEquals(model.getOrganisationId(), partnerOrganisation.getId());
        assertEquals(model.getProjectName(), project.getName());
        assertTrue(model.isApproved());
    }

    @Test
    public void testDownloadAttachmentFailsNoContent() throws Exception {

        when(financeCheckServiceMock.downloadFile(1L)).thenThrow(new ForbiddenActionException());
        MvcResult result = mockMvc.perform(get("/project/123/finance-checks/attachment/1"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("forbidden"))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Test
    public void testDownloadAttachmentFailsNoInfo() throws Exception {

        ByteArrayResource bytes = new ByteArrayResource("File contents".getBytes());

        when(financeCheckServiceMock.downloadFile(1L)).thenReturn(bytes);
        when(financeCheckServiceMock.getAttachmentInfo(1L)).thenThrow(new ForbiddenActionException());
        MvcResult result = mockMvc.perform(get("/project/123/finance-checks/attachment/1"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("forbidden"))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Test
    public void testViewNewResponse() throws Exception {

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(projectService.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

        MvcResult result = mockMvc.perform(get("/project/123/finance-checks/1/new-response"))
                .andExpect(view().name("project/finance-checks"))
                .andReturn();

        ProjectFinanceChecksViewModel responseViewModel = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Project1", responseViewModel.getProjectName());
        assertEquals(organisationId, responseViewModel.getOrganisationId());
        assertEquals(projectId, responseViewModel.getProjectId());
        assertEquals(1L, responseViewModel.getQueryId().longValue());
        assertEquals("/project/{projectId}/finance-checks", responseViewModel.getBaseUrl());
        assertEquals(4000, responseViewModel.getMaxQueryCharacters());
        assertEquals(400, responseViewModel.getMaxQueryWords());
        assertEquals(0, responseViewModel.getNewAttachmentLinks().size());
    }

    @Test
    public void testSaveNewResponse() throws Exception {
        when(financeCheckServiceMock.saveQueryPost(any(PostResource.class), eq(1L))).thenReturn(ServiceResult.serviceSuccess());

        MvcResult result = mockMvc.perform(post("/project/123/finance-checks/1/new-response")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", "Query text"))
                .andExpect(redirectedUrlPattern("/project/123/finance-checks/**"))
                .andReturn();

        verify(financeCheckServiceMock).saveQueryPost(savePostArgumentCaptor.capture(), eq(1L));

        assertEquals("Query text", savePostArgumentCaptor.getAllValues().get(0).body);
        assertEquals(loggedInUser, savePostArgumentCaptor.getAllValues().get(0).author);
        assertEquals(0, savePostArgumentCaptor.getAllValues().get(0).attachments.size());
        assertTrue(ZonedDateTime.now().compareTo(savePostArgumentCaptor.getAllValues().get(0).createdOn) >= 0);

        FinanceChecksQueryResponseForm form = (FinanceChecksQueryResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query text", form.getResponse());
        assertEquals(null, form.getAttachment());
    }

    @Test
    public void testSaveNewResponseNoFieldsSet() throws Exception {

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(projectService.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

        MvcResult result = mockMvc.perform(post("/project/123/finance-checks/1/new-response")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", ""))
                .andExpect(view().name("project/finance-checks"))
                .andReturn();

        FinanceChecksQueryResponseForm form = (FinanceChecksQueryResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("", form.getResponse());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("response"));
        assertEquals("This field cannot be left blank.", bindingResult.getFieldError("response").getDefaultMessage());
    }

    @Test
    public void testSaveNewResponseFieldsTooLong() throws Exception {

        String tooLong = StringUtils.leftPad("a", 4001, 'a');

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(projectService.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

        MvcResult result = mockMvc.perform(post("/project/123/finance-checks/1/new-response")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", tooLong))
                .andExpect(view().name("project/finance-checks"))
                .andReturn();


        FinanceChecksQueryResponseForm form = (FinanceChecksQueryResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals(tooLong, form.getResponse());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("response"));
        assertEquals("This field cannot contain more than {1} characters.", bindingResult.getFieldError("response").getDefaultMessage());
    }

    @Test
    public void testSaveNewResponseTooManyWords() throws Exception {

        String tooManyWords = StringUtils.leftPad("a ", 802, "a ");

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(projectService.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

        MvcResult result = mockMvc.perform(post("/project/123/finance-checks/1/new-response")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", tooManyWords))
                .andExpect(view().name("project/finance-checks"))
                .andReturn();

        FinanceChecksQueryResponseForm form = (FinanceChecksQueryResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals(tooManyWords, form.getResponse());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("response"));
        assertEquals("Maximum word count exceeded. Please reduce your word count to {1}.", bindingResult.getFieldError("response").getDefaultMessage());
    }

    @Test
    public void testSaveNewResponseAttachment() throws Exception {

        MockMultipartFile uploadedFile = new MockMultipartFile("attachment", "testFile.pdf", "application/pdf", "My content!".getBytes());
        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L, null);
        FileEntryResource fileEntry = new FileEntryResource(1L, "name", "mediaType", 2L);

        when(financeCheckServiceMock.uploadFile(projectId, uploadedFile.getContentType(), uploadedFile.getSize(), uploadedFile.getOriginalFilename(), uploadedFile.getBytes())).thenReturn(ServiceResult.serviceSuccess(attachment));
        when(financeCheckServiceMock.getAttachmentInfo(1L)).thenReturn(fileEntry);
        when(projectService.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        MvcResult result = mockMvc.perform(
                fileUpload("/project/123/finance-checks/1/new-response").
                        file(uploadedFile).
                        param("uploadAttachment", ""))
                .andExpect(cookie().exists("query_new_response_attachments_123_234_1"))
                .andExpect(view().name("project/finance-checks"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<>();
        expectedAttachmentIds.add(1L);
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "query_new_response_attachments_123_234_1"));

        FinanceChecksQueryResponseForm form = (FinanceChecksQueryResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals(uploadedFile, form.getAttachment());

    }

    @Test
    public void testDownloadResponseAttachmentFailsNoContent() throws Exception {
        when(financeCheckServiceMock.downloadFile(1L)).thenThrow(new ForbiddenActionException());

        MvcResult result = mockMvc.perform(get("/project/123/finance-checks/1/new-response/attachment/1"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404"))
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Test
    public void testCancelNewResponse() throws Exception {

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie ck = createAttachmentsCookie(attachmentIds);

        when(financeCheckServiceMock.deleteFile(1L)).thenReturn(ServiceResult.serviceSuccess());
        when(projectService.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

        MvcResult result = mockMvc.perform(get("/project/123/finance-checks/1/new-response/cancel")
                .cookie(ck))
                .andExpect(redirectedUrlPattern("/project/123/finance-checks/**"))
                .andReturn();

        Optional<Cookie> cookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("query_new_response_attachments_123_234_1"))
                .findAny();
        assertEquals(true, cookieFound.get().getValue().isEmpty());

        verify(financeCheckServiceMock).deleteFile(1L);
    }

    @Test
    public void testViewNewResponseWithAttachments() throws Exception {

        FileEntryResource fileEntryResource = new FileEntryResource(1L, "name", "mediaType", 2L);

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(projectService.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);
        when(financeCheckServiceMock.getAttachmentInfo(1L)).thenReturn(fileEntryResource);

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        Cookie cookie = new Cookie("query_new_response_attachments_123_234_1", encryptedData);
        MvcResult result = mockMvc.perform(get("/project/123/finance-checks/1/new-response")
                .cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("project/finance-checks"))
                .andReturn();

        ProjectFinanceChecksViewModel queryViewModel = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Project1", queryViewModel.getProjectName());
        assertEquals(organisationId, queryViewModel.getOrganisationId());
        assertEquals(projectId, queryViewModel.getProjectId());
        assertEquals("/project/{projectId}/finance-checks", queryViewModel.getBaseUrl());
        assertEquals(4000, queryViewModel.getMaxQueryCharacters());
        assertEquals(400, queryViewModel.getMaxQueryWords());
        assertEquals(1L, queryViewModel.getQueryId().longValue());
        assertEquals(1, queryViewModel.getNewAttachmentLinks().size());
        assertEquals("name", queryViewModel.getNewAttachmentLinks().get(1L));
    }

    @Test
    public void testRemoveAttachment() throws Exception {

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(projectService.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);
        when(financeCheckServiceMock.deleteFile(1L)).thenReturn(ServiceResult.serviceSuccess());

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie cookie = createAttachmentsCookie(attachmentIds);
        MvcResult result = mockMvc.perform(post("/project/123/finance-checks/1/new-response")
                .param("removeAttachment", "1")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", "Query"))
                .andExpect(view().name("project/finance-checks"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<>();
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "query_new_response_attachments_123_234_1"));

        verify(financeCheckServiceMock).deleteFile(1L);

        FinanceChecksQueryResponseForm form = (FinanceChecksQueryResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query", form.getResponse());
        assertEquals(null, form.getAttachment());

        ProjectFinanceChecksViewModel queryViewModel = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(0, queryViewModel.getNewAttachmentLinks().size());
    }

    @Test
    public void testRemoveAttachmentDoesNotRemoveAttachmentNotInCookie() throws Exception {
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(projectService.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

        FileEntryResource attachment = new FileEntryResource(1L, "name", "mediaType", 2L);

        when(financeCheckServiceMock.getAttachmentInfo(1L)).thenReturn(attachment);

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie cookie = createAttachmentsCookie(attachmentIds);
        MvcResult result = mockMvc.perform(post("/project/123/finance-checks/1/new-response")
                .param("removeAttachment", "2")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", "Query"))
                .andExpect(view().name("project/finance-checks"))
                .andReturn();

        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(attachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "query_new_response_attachments_123_234_1"));

        FinanceChecksQueryResponseForm form = (FinanceChecksQueryResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query", form.getResponse());
        assertEquals(null, form.getAttachment());

        ProjectFinanceChecksViewModel queryViewModel = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(1, queryViewModel.getNewAttachmentLinks().size());
        assertEquals("name", queryViewModel.getNewAttachmentLinks().get(1L));
    }

    @Test
    public void testSaveNewResponseQueryCannotRespondToQuery() throws Exception {

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(projectService.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);
        when(financeCheckServiceMock.saveQueryPost(any(PostResource.class), eq(5L))).thenReturn(ServiceResult.serviceFailure(CommonFailureKeys.GENERAL_FORBIDDEN));
        MvcResult result = mockMvc.perform(post("/project/123/finance-checks/5/new-response")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", "Query"))
                .andExpect(view().name("project/finance-checks"))
                .andReturn();

        List<? extends ObjectError> errors = (List<? extends ObjectError>) result.getModelAndView().getModel().get("nonFormErrors");
        assertEquals(1, errors.size());
        assertEquals("validation.notesandqueries.query.response.save.failed", errors.get(0).getCode());
    }

    private Cookie createAttachmentsCookie(List<Long> attachmentIds) throws Exception{
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        return new Cookie("query_new_response_attachments_"+projectId+"_"+organisationId+"_"+queryId, encryptedData);
    }

    @Override
    protected ProjectFinanceChecksController supplyControllerUnderTest() {
        return new ProjectFinanceChecksController();
    }
}
