package org.innovateuk.ifs.project.financechecks;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.financechecks.controller.ProjectFinanceChecksController;
import org.innovateuk.ifs.project.financechecks.form.FinanceChecksQueryResponseForm;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.util.JsonUtil;
import org.innovateuk.ifs.utils.UserOrganisationUtil;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectFinanceChecksControllerQueriesTest extends BaseControllerMockMVCTest<ProjectFinanceChecksController> {

    private Long projectId = 123L;
    private Long financeTeamUserId = 18L;
    private Long applicantFinanceContactUserId = 55L;
    private Long organisationId = 234L;
    private Long projectFinanceId = 45L;
    private Long queryId = 1L;

    @Mock
    public UserOrganisationUtil userOrganisationUtilMock;

    ProjectUserResource projectUser = newProjectUserResource().withOrganisation(organisationId).withUserName("User1").withUser(applicantFinanceContactUserId).withEmail("e@mail.com").withPhoneNumber("0117").withRoleName(UserRoleType.FINANCE_CONTACT).build();

    ApplicationResource applicationResource = newApplicationResource().build();
    ProjectResource project = newProjectResource().withId(projectId).withName("Project1").withApplication(applicationResource).withProjectUsers(Collections.singletonList(projectUser.getId())).build();

    OrganisationResource innovateOrganisationResource = newOrganisationResource().withName("Innovate").build();

    OrganisationResource leadOrganisationResource = newOrganisationResource().withName("Org1").withId(organisationId).build();


    RoleResource financeTeamRole = newRoleResource().withType(PROJECT_FINANCE).build();
    UserResource financeTeamUser = newUserResource().withFirstName("A").withLastName("Z").withId(financeTeamUserId).withRolesGlobal(Arrays.asList(financeTeamRole)).build();
    UserResource projectManagerUser = newUserResource().withFirstName("B").withLastName("Z").withId(applicantFinanceContactUserId).build();


    ProjectPartnerStatusResource statusResource = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE)
            .withFinanceContactStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).build();
    ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().withPartnerStatuses(Collections.singletonList(statusResource)).build();
    OrganisationResource partnerOrganisation = newOrganisationResource().withId(organisationId).build();
    ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(organisationId).withId(projectFinanceId).build();


    QueryResource thread;
    UserResource user1;
    PostResource firstPost;
    UserResource user2;
    PostResource firstResponse;

    QueryResource thread2;
    PostResource firstPost2;

    QueryResource thread3;
    PostResource firstPost1 ;
    PostResource firstResponse1;

    List<QueryResource> queries;


    @Captor
    ArgumentCaptor<PostResource> savePostArgumentCaptor;

    @Before
    public void setup() {
        super.setUp();
        this.setupCookieUtil();
        when(userService.findById(financeTeamUserId)).thenReturn(financeTeamUser);
        when(organisationService.getOrganisationForUser(financeTeamUserId)).thenReturn(innovateOrganisationResource);
        when(userService.findById(applicantFinanceContactUserId)).thenReturn(projectManagerUser);
        when(organisationService.getOrganisationForUser(applicantFinanceContactUserId)).thenReturn(leadOrganisationResource);
        when(userService.findById(applicantFinanceContactUserId)).thenReturn(projectManagerUser);

        // populate viewmodel
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(leadOrganisationResource);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisationResource);
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(Arrays.asList(projectUser));

        UserResource user1 = new UserResource();
        user1.setId(18L);
        PostResource firstPost = new PostResource(null, user1, "Question", Arrays.asList(new AttachmentResource(23L, "file1.txt", "txt", 1L)), LocalDateTime.now().plusMinutes(10L));
        UserResource user2 = new UserResource();
        user2.setId(55L);
        PostResource firstResponse = new PostResource(null, user2, "Response", new ArrayList<>(), LocalDateTime.now().plusMinutes(20L));
        thread = new QueryResource(1L, projectFinanceId, Arrays.asList(firstPost, firstResponse), FinanceChecksSectionType.ELIGIBILITY, "Query title", false, LocalDateTime.now());

        PostResource firstPost2 = new PostResource(null, user1, "Question2", new ArrayList<>(), LocalDateTime.now().plusMinutes(15L));
        thread2 = new QueryResource(3L, projectFinanceId, Arrays.asList(firstPost2), FinanceChecksSectionType.ELIGIBILITY, "Query2 title", true, LocalDateTime.now());

        PostResource firstPost1 = new PostResource(null, user1, "Question3", new ArrayList<>(), LocalDateTime.now());
        PostResource firstResponse1 = new PostResource(null, user2, "Response3", new ArrayList<>(), LocalDateTime.now().plusMinutes(10L));

        thread3 = new QueryResource(5L, projectFinanceId, Arrays.asList(firstPost1, firstResponse1), FinanceChecksSectionType.ELIGIBILITY, "Query title3", false, LocalDateTime.now());

        queries = Arrays.asList(thread2, thread, thread3);
    }

    @Test
    public void testViewFinanceChecksLandingPage() throws Exception {

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(Collections.emptyList()));
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

        MvcResult result = mockMvc.perform(get("/project/123/finance-checks")).
                andExpect(view().name("project/finance-checks")).
                andReturn();

        ProjectFinanceChecksViewModel model = (ProjectFinanceChecksViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(project.getId(), model.getProjectId());
        assertEquals(partnerOrganisation.getId(), model.getOrganisationId());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals(Collections.emptyList(), model.getQueries());
        assertFalse(model.isApproved());

    }


    @Test
    public void testViewFinanceChecksWithQueries() throws Exception {
        setLoggedInUser(projectManagerUser);
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(projectManagerUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, projectManagerUser)).thenReturn(organisationId);

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
        assertEquals(3, model.getQueries().size());
        assertEquals("Query title", model.getQueries().get(0).getTitle());
        assertEquals(false, model.getQueries().get(0).isAwaitingResponse());
        assertEquals(organisationId, model.getQueries().get(0).getOrganisationId());
        assertEquals(projectId, model.getQueries().get(0).getProjectId());
        assertEquals(1L, model.getQueries().get(0).getId().longValue());
        assertEquals(2, model.getQueries().get(0).getViewModelPosts().size());
        assertEquals("Question", model.getQueries().get(0).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUserId, model.getQueries().get(0).getViewModelPosts().get(0).author.getId());
        assertEquals("Innovate UK - Finance team", model.getQueries().get(0).getViewModelPosts().get(0).getUsername());
        assertTrue(LocalDateTime.now().plusMinutes(10L).isAfter(model.getQueries().get(0).getViewModelPosts().get(0).createdOn));
        assertEquals(1, model.getQueries().get(0).getViewModelPosts().get(0).attachments.size());
        assertEquals(23L, model.getQueries().get(0).getViewModelPosts().get(0).attachments.get(0).id.longValue());
        assertEquals("file1.txt", model.getQueries().get(0).getViewModelPosts().get(0).attachments.get(0).name);
        assertEquals("Response", model.getQueries().get(0).getViewModelPosts().get(1).body);
        assertEquals(applicantFinanceContactUserId, model.getQueries().get(0).getViewModelPosts().get(1).author.getId());
        assertEquals("B Z - Org1", model.getQueries().get(0).getViewModelPosts().get(1).getUsername());
        assertTrue(LocalDateTime.now().plusMinutes(20L).isAfter(model.getQueries().get(0).getViewModelPosts().get(1).createdOn));
        assertEquals(0, model.getQueries().get(0).getViewModelPosts().get(1).attachments.size());
        assertEquals("Query2 title", model.getQueries().get(1).getTitle());
        assertEquals(true, model.getQueries().get(1).isAwaitingResponse());
        assertEquals(organisationId, model.getQueries().get(1).getOrganisationId());
        assertEquals(projectId, model.getQueries().get(1).getProjectId());
        assertEquals(3L, model.getQueries().get(1).getId().longValue());
        assertEquals(1, model.getQueries().get(1).getViewModelPosts().size());
        assertEquals("Question2", model.getQueries().get(1).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUserId, model.getQueries().get(1).getViewModelPosts().get(0).author.getId());
        assertEquals("Innovate UK - Finance team", model.getQueries().get(1).getViewModelPosts().get(0).getUsername());
        assertTrue(LocalDateTime.now().plusMinutes(15L).isAfter(model.getQueries().get(1).getViewModelPosts().get(0).createdOn));
        assertEquals(0, model.getQueries().get(1).getViewModelPosts().get(0).attachments.size());

        assertEquals("Query title3", model.getQueries().get(2).getTitle());
        assertEquals(false, model.getQueries().get(2).isAwaitingResponse());
        assertEquals(organisationId, model.getQueries().get(2).getOrganisationId());
        assertEquals(projectId, model.getQueries().get(2).getProjectId());
        assertEquals(5L, model.getQueries().get(2).getId().longValue());
        assertEquals(2, model.getQueries().get(2).getViewModelPosts().size());
        assertEquals("Question3", model.getQueries().get(2).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUserId, model.getQueries().get(2).getViewModelPosts().get(0).author.getId());
        assertEquals("Innovate UK - Finance team", model.getQueries().get(2).getViewModelPosts().get(0).getUsername());
        assertTrue(LocalDateTime.now().isAfter(model.getQueries().get(2).getViewModelPosts().get(0).createdOn));
        assertEquals(0, model.getQueries().get(2).getViewModelPosts().get(0).attachments.size());
        assertEquals("Response3", model.getQueries().get(2).getViewModelPosts().get(1).body);
        assertEquals(applicantFinanceContactUserId, model.getQueries().get(2).getViewModelPosts().get(1).author.getId());
        assertEquals("B Z - Org1", model.getQueries().get(2).getViewModelPosts().get(1).getUsername());
        assertTrue(LocalDateTime.now().plusMinutes(10L).isAfter(model.getQueries().get(2).getViewModelPosts().get(1).createdOn));
        assertEquals(0, model.getQueries().get(2).getViewModelPosts().get(1).attachments.size());

    }

    @Test
    public void testViewFinanceChecksLandingPageApproved() throws Exception {

        ProjectPartnerStatusResource statusResource = newProjectPartnerStatusResource().withProjectDetailsStatus(ProjectActivityStates.COMPLETE)
                .withFinanceContactStatus(ProjectActivityStates.COMPLETE).withFinanceChecksStatus(ProjectActivityStates.COMPLETE).withOrganisationId(organisationId).build();
        ProjectTeamStatusResource expectedProjectTeamStatusResource = newProjectTeamStatusResource().withPartnerStatuses(Collections.singletonList(statusResource)).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().withId(organisationId).build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(organisationId).withId(projectFinanceId).build();
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
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

        FileEntryResource fileEntry = new FileEntryResource(1L, "name", "mediaType", 2L);

        when(financeCheckServiceMock.downloadFile(1L)).thenReturn(ServiceResult.serviceFailure(CommonFailureKeys.GENERAL_NOT_FOUND));
        when(financeCheckServiceMock.getAttachmentInfo(1L)).thenReturn(ServiceResult.serviceSuccess(fileEntry));
        MvcResult result = mockMvc.perform(get("/project/123/finance-checks/attachment/1"))
                .andExpect(status().isNoContent())
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

        when(financeCheckServiceMock.downloadFile(1L)).thenReturn(ServiceResult.serviceSuccess(Optional.of(bytes)));
        when(financeCheckServiceMock.getAttachmentInfo(1L)).thenReturn(ServiceResult.serviceFailure(CommonFailureKeys.GENERAL_NOT_FOUND));
        MvcResult result = mockMvc.perform(get("/project/123/finance-checks/attachment/1"))
                .andExpect(status().isNoContent())
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
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

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
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
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
        assertTrue(LocalDateTime.now().compareTo(savePostArgumentCaptor.getAllValues().get(0).createdOn) >= 0);

        FinanceChecksQueryResponseForm form = (FinanceChecksQueryResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query text", form.getResponse());
        assertEquals(null, form.getAttachment());
    }

    @Test
    public void testSaveNewResponseNoFieldsSet() throws Exception {

        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

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

        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

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
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

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
        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L);
        FileEntryResource fileEntry = new FileEntryResource(1L, "name", "mediaType", 2L);

        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(financeCheckServiceMock.uploadFile(projectId, uploadedFile.getContentType(), uploadedFile.getSize(), uploadedFile.getOriginalFilename(), uploadedFile.getBytes())).thenReturn(ServiceResult.serviceSuccess(attachment));
        when(financeCheckServiceMock.getAttachmentInfo(1L)).thenReturn(ServiceResult.serviceSuccess(fileEntry));
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
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
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));

        MvcResult result = mockMvc.perform(get("/project/123/finance-checks/1/new-response/attachment/1"))
                .andExpect(status().isNoContent())
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

        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(financeCheckServiceMock.deleteFile(1L)).thenReturn(ServiceResult.serviceSuccess());
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

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
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);
        when(financeCheckServiceMock.getAttachmentInfo(1L)).thenReturn(ServiceResult.serviceSuccess(fileEntryResource));

        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));

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

        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);
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
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);

        FileEntryResource attachment = new FileEntryResource(1L, "name", "mediaType", 2L);

        when(financeCheckServiceMock.getAttachmentInfo(1L)).thenReturn(ServiceResult.serviceSuccess(attachment));

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

        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource().withUser(loggedInUser.getId()).withOrganisation(organisationId).withRoleName(UserRoleType.PARTNER.getName()).build(1));
        when(projectService.getById(projectId)).thenReturn(project);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(partnerOrganisation);
        when(projectService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(expectedProjectTeamStatusResource);
        when(projectFinanceService.getProjectFinance(projectId, organisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        when(userOrganisationUtilMock.getOrganisationIdFromUser(projectId, loggedInUser)).thenReturn(organisationId);
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
