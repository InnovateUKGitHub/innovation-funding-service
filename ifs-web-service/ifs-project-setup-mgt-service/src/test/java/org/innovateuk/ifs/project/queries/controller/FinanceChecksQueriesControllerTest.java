package org.innovateuk.ifs.project.queries.controller;

import java.net.URLEncoder;
import java.time.ZonedDateTime;
import java.util.*;
import javax.servlet.http.Cookie;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.ProjectFinanceService;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesAddResponseForm;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.thread.viewmodel.ThreadViewModelPopulator;
import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.FINANCE_CONTACT;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CookieTestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceChecksQueriesControllerTest extends BaseControllerMockMVCTest<FinanceChecksQueriesController> {

    private Long projectId = 3L;
    private Long innovateOrganisationId = 11L;
    private Long applicantOrganisationId = 22L;
    private Long projectFinanceId = 45L;
    private Long queryId = 1L;

    private ApplicationResource applicationResource = newApplicationResource().build();
    private ProjectResource projectResource = newProjectResource()
            .withId(projectId)
            .withName("Project1")
            .withApplication(applicationResource)
            .withProjectState(SETUP)
            .build();

    private OrganisationResource innovateOrganisationResource = newOrganisationResource().withName("Innovate").withId(innovateOrganisationId).build();

    private OrganisationResource leadOrganisationResource = newOrganisationResource().withName("Org1").withId(applicantOrganisationId).build();

    private Role financeTeamRole = Role.PROJECT_FINANCE;
    private UserResource financeTeamUser = newUserResource().withFirstName("A").withLastName("Z").withRolesGlobal(singletonList(financeTeamRole)).build();
    private UserResource financeContactUser = newUserResource().withFirstName("B").withLastName("Z").build();
    private ProjectUserResource financeContactProjectUser = newProjectUserResource().withUser(financeContactUser.getId()).withOrganisation(applicantOrganisationId).withUserName("User1").withEmail("e@mail.com").withPhoneNumber("0117").withRole(FINANCE_CONTACT).build();
    private UserResource financeContact2User = newUserResource().withFirstName("C").withLastName("Z").build();
    private ProjectUserResource financeContact2ProjectUser = newProjectUserResource().withUser(financeContact2User.getId()).withOrganisation(applicantOrganisationId).build();

    private QueryResource thread;
    private QueryResource thread2;
    private QueryResource thread3;

    private List<QueryResource> queries;

    @Captor
    private ArgumentCaptor<PostResource> savePostArgumentCaptor;


    @Mock
    private EncryptedCookieService cookieUtil;

    @Mock
    private UserRestService userRestService;

    @Mock
    private ProjectService projectService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private ProjectFinanceService projectFinanceService;

    @Mock
    private FinanceCheckService financeCheckServiceMock;

    @Mock
    private OrganisationRestService organisationRestService;

    private ThreadViewModelPopulator threadViewModelPopulator;

    @Before
    public void setupCommonExpectations() {

        setupEncryptedCookieService(cookieUtil);


        threadViewModelPopulator = new ThreadViewModelPopulator(organisationRestService);
        spy(threadViewModelPopulator);
        controller.setThreadViewModelPopulator(threadViewModelPopulator);

        when(userRestService.retrieveUserById(financeTeamUser.getId())).thenReturn(restSuccess(financeTeamUser));
        when(organisationRestService.getByUserAndProjectId(financeTeamUser.getId(), projectId)).thenReturn(restSuccess(innovateOrganisationResource));
        when(userRestService.retrieveUserById(financeContactUser.getId())).thenReturn(restSuccess(financeContactUser));
        when(organisationRestService.getByUserAndProjectId(financeContactUser.getId(), projectId)).thenReturn(restSuccess(leadOrganisationResource));
        when(userRestService.retrieveUserById(financeContact2User.getId())).thenReturn(restSuccess(financeContact2User));
        when(organisationRestService.getByUserAndProjectId(financeContact2User.getId(), projectId)).thenReturn(restSuccess(leadOrganisationResource));

        // populate viewmodel
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(organisationRestService.getOrganisationById(applicantOrganisationId)).thenReturn(restSuccess(leadOrganisationResource));
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisationResource);
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(asList(financeContactProjectUser, financeContact2ProjectUser));
        when(partnerOrganisationRestService.getPartnerOrganisation(projectId, applicantOrganisationId)).thenReturn(restSuccess(newPartnerOrganisationResource().build()));

        PostResource firstPost = new PostResource(null, financeTeamUser, "Question", singletonList(new AttachmentResource(23L, "file1.txt", "txt", 1L, null)), ZonedDateTime.now().plusMinutes(10L));
        PostResource firstResponse = new PostResource(null, financeContactUser, "Response", new ArrayList<>(), ZonedDateTime.now().plusMinutes(20L));
        thread = new QueryResource(1L, projectFinanceId, asList(firstPost, firstResponse), FinanceChecksSectionType.ELIGIBILITY, "Query title", false, ZonedDateTime.now(), null, null);

        PostResource firstPost2 = new PostResource(null, financeTeamUser, "Question2", new ArrayList<>(), ZonedDateTime.now().plusMinutes(15L));
        thread2 = new QueryResource(3L, projectFinanceId, singletonList(firstPost2), FinanceChecksSectionType.ELIGIBILITY, "Query2 title", true, ZonedDateTime.now(), null, null);

        PostResource firstPost1 = new PostResource(null, financeTeamUser, "Question3", new ArrayList<>(), ZonedDateTime.now());
        PostResource firstResponse1 = new PostResource(null, financeContactUser, "Response3", new ArrayList<>(), ZonedDateTime.now().plusMinutes(10L));

        thread3 = new QueryResource(5L, projectFinanceId, asList(firstPost1, firstResponse1), FinanceChecksSectionType.ELIGIBILITY, "Query title3", false, ZonedDateTime.now(), null, null);

        queries = asList(thread2, thread, thread3);
    }

    @Test
    public void testGetReadOnlyView() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility"))
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        FinanceChecksQueriesViewModel queryViewModel = (FinanceChecksQueriesViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Eligibility", queryViewModel.getQuerySection());
        assertTrue(queryViewModel.getFinanceContact().isPresent());
        assertEquals("e@mail.com", queryViewModel.getFinanceContact().get().getEmail());
        assertEquals("User1", queryViewModel.getFinanceContact().get().getUserName());
        assertEquals("0117", queryViewModel.getFinanceContact().get().getPhoneNumber());
        assertEquals("Org1", queryViewModel.getOrganisationName());
        assertEquals("Project1", queryViewModel.getProjectName());
        assertEquals(applicantOrganisationId, queryViewModel.getOrganisationId());
        assertEquals(projectId, queryViewModel.getProjectId());
        assertTrue(queryViewModel.isProjectIsActive());

        assertEquals(3, queryViewModel.getQueries().size());
        assertEquals("Query title", queryViewModel.getQueries().get(0).getTitle());
        assertEquals(FinanceChecksSectionType.ELIGIBILITY, queryViewModel.getQueries().get(0).getSectionType());
        assertEquals(false, queryViewModel.getQueries().get(0).isLastPostByInternalUser());
        assertEquals(applicantOrganisationId, queryViewModel.getQueries().get(0).getOrganisationId());
        assertEquals(projectId, queryViewModel.getQueries().get(0).getProjectId());
        assertEquals(1L, queryViewModel.getQueries().get(0).getId().longValue());
        assertEquals(2, queryViewModel.getQueries().get(0).getViewModelPosts().size());
        assertEquals("Question", queryViewModel.getQueries().get(0).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUser.getId(), queryViewModel.getQueries().get(0).getViewModelPosts().get(0).author.getId());
        assertEquals("A Z - Innovate UK (Finance team)", queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getUsername());
        assertTrue(ZonedDateTime.now().plusMinutes(10L).isAfter(queryViewModel.getQueries().get(0).getViewModelPosts().get(0).createdOn));
        assertEquals(1, queryViewModel.getQueries().get(0).getViewModelPosts().get(0).attachments.size());
        assertEquals(23L, queryViewModel.getQueries().get(0).getViewModelPosts().get(0).attachments.get(0).id.longValue());
        assertEquals("file1.txt", queryViewModel.getQueries().get(0).getViewModelPosts().get(0).attachments.get(0).name);
        assertEquals("Response", queryViewModel.getQueries().get(0).getViewModelPosts().get(1).body);
        assertEquals(financeContactUser.getId(), queryViewModel.getQueries().get(0).getViewModelPosts().get(1).author.getId());
        assertEquals("B Z - Org1", queryViewModel.getQueries().get(0).getViewModelPosts().get(1).getUsername());
        assertTrue(ZonedDateTime.now().plusMinutes(20L).isAfter(queryViewModel.getQueries().get(0).getViewModelPosts().get(1).createdOn));
        assertEquals(0, queryViewModel.getQueries().get(0).getViewModelPosts().get(1).attachments.size());
        assertEquals("Query2 title", queryViewModel.getQueries().get(1).getTitle());
        assertEquals(FinanceChecksSectionType.ELIGIBILITY, queryViewModel.getQueries().get(1).getSectionType());
        assertEquals(true, queryViewModel.getQueries().get(1).isLastPostByInternalUser());
        assertEquals(applicantOrganisationId, queryViewModel.getQueries().get(1).getOrganisationId());
        assertEquals(projectId, queryViewModel.getQueries().get(1).getProjectId());
        assertEquals(3L, queryViewModel.getQueries().get(1).getId().longValue());
        assertEquals(1, queryViewModel.getQueries().get(1).getViewModelPosts().size());
        assertEquals("Question2", queryViewModel.getQueries().get(1).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUser.getId(), queryViewModel.getQueries().get(1).getViewModelPosts().get(0).author.getId());
        assertEquals("A Z - Innovate UK (Finance team)", queryViewModel.getQueries().get(1).getViewModelPosts().get(0).getUsername());
        assertTrue(ZonedDateTime.now().plusMinutes(15L).isAfter(queryViewModel.getQueries().get(1).getViewModelPosts().get(0).createdOn));
        assertEquals(0, queryViewModel.getQueries().get(1).getViewModelPosts().get(0).attachments.size());

        assertEquals("Query title3", queryViewModel.getQueries().get(2).getTitle());
        assertEquals(FinanceChecksSectionType.ELIGIBILITY, queryViewModel.getQueries().get(2).getSectionType());
        assertEquals(false, queryViewModel.getQueries().get(2).isLastPostByInternalUser());
        assertEquals(applicantOrganisationId, queryViewModel.getQueries().get(2).getOrganisationId());
        assertEquals(projectId, queryViewModel.getQueries().get(2).getProjectId());
        assertEquals(5L, queryViewModel.getQueries().get(2).getId().longValue());
        assertEquals(2, queryViewModel.getQueries().get(2).getViewModelPosts().size());
        assertEquals("Question3", queryViewModel.getQueries().get(2).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUser.getId(), queryViewModel.getQueries().get(2).getViewModelPosts().get(0).author.getId());
        assertEquals("A Z - Innovate UK (Finance team)", queryViewModel.getQueries().get(2).getViewModelPosts().get(0).getUsername());
        assertTrue(ZonedDateTime.now().isAfter(queryViewModel.getQueries().get(2).getViewModelPosts().get(0).createdOn));
        assertEquals(0, queryViewModel.getQueries().get(2).getViewModelPosts().get(0).attachments.size());
        assertEquals("Response3", queryViewModel.getQueries().get(2).getViewModelPosts().get(1).body);
        assertEquals(financeContactUser.getId(), queryViewModel.getQueries().get(2).getViewModelPosts().get(1).author.getId());
        assertEquals("B Z - Org1", queryViewModel.getQueries().get(2).getViewModelPosts().get(1).getUsername());
        assertTrue(ZonedDateTime.now().plusMinutes(10L).isAfter(queryViewModel.getQueries().get(2).getViewModelPosts().get(1).createdOn));
        assertEquals(0, queryViewModel.getQueries().get(2).getViewModelPosts().get(1).attachments.size());
    }

    @Test
    public void testThreadState() throws Exception {
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();

        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility"))
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        FinanceChecksQueriesViewModel queryViewModel = (FinanceChecksQueriesViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(true, queryViewModel.getQueries().get(0).isLastPostByExternalUser());
        assertEquals(true, queryViewModel.getQueries().get(1).isLastPostByInternalUser());
        assertEquals(true, queryViewModel.getQueries().get(2).isLastPostByExternalUser());

    }

    @Test
    public void testCloseQuery() throws Exception {

        Long queryId = 1L;
        when(financeCheckServiceMock.closeQuery(queryId)).thenReturn(ServiceResult.serviceSuccess());

        mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/{queryId}/close", queryId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query"))
                .andReturn();

        verify(financeCheckServiceMock).closeQuery(queryId);
    }

    @Test
    public void testQueriesPageWhenFCIsProvided() throws Exception {
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility"))
                .andExpect(status().isOk())
                .andReturn();
        FinanceChecksQueriesViewModel queryViewModel = (FinanceChecksQueriesViewModel) result.getModelAndView().getModel().get("model");
        assertTrue(queryViewModel.getFinanceContact().isPresent());
    }

    @Test
    public void testQueriesPageWhenFCIsNotProvided() throws Exception {
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));
        ProjectUserResource projectUsersWithoutFC = newProjectUserResource().withOrganisation(applicantOrganisationId).withUserName("User1").withEmail("e@mail.com").withPhoneNumber("0117").withRole(PROJECT_MANAGER).build();
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(singletonList(projectUsersWithoutFC));
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility"))
                .andExpect(status().isOk())
                .andReturn();
        FinanceChecksQueriesViewModel queryViewModel = (FinanceChecksQueriesViewModel) result.getModelAndView().getModel().get("model");
        assertFalse(queryViewModel.getFinanceContact().isPresent());
    }

    @Test
    public void testDownloadAttachmentFailsNoContent() throws Exception {

        when(financeCheckServiceMock.downloadFile(1L)).thenThrow(new ForbiddenActionException());
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/attachment/1?query_section=Eligibility"))
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
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/attachment/1?query_section=Eligibility"))
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

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        Cookie formCookie;
        FinanceChecksQueriesAddResponseForm form = new FinanceChecksQueriesAddResponseForm();
        form.setResponse("comment");
        formCookie = createFormCookie(form);

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/" + queryId + "/new-response?query_section=Eligibility")
                .cookie(formCookie))
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        FinanceChecksQueriesViewModel responseViewModel = (FinanceChecksQueriesViewModel) result.getModelAndView().getModel().get("model");
        FinanceChecksQueriesAddResponseForm modelForm = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");

        assertEquals("Eligibility", responseViewModel.getQuerySection());
        assertTrue(responseViewModel.getFinanceContact().isPresent());
        assertEquals("e@mail.com", responseViewModel.getFinanceContact().get().getEmail());
        assertEquals("User1", responseViewModel.getFinanceContact().get().getUserName());
        assertEquals("0117", responseViewModel.getFinanceContact().get().getPhoneNumber());
        assertEquals("Org1", responseViewModel.getOrganisationName());
        assertEquals("Project1", responseViewModel.getProjectName());
        assertEquals(applicantOrganisationId, responseViewModel.getOrganisationId());
        assertEquals(projectId, responseViewModel.getProjectId());
        assertEquals(1L, responseViewModel.getQueryId().longValue());
        assertEquals("/project/{projectId}/finance-check/organisation/{organisationId}/query", responseViewModel.getBaseUrl());
        assertEquals(4000, responseViewModel.getMaxQueryCharacters());
        assertEquals(400, responseViewModel.getMaxQueryWords());
        assertTrue(responseViewModel.isLeadPartnerOrganisation());
        assertEquals(0, responseViewModel.getNewAttachmentLinks().size());
        assertEquals("comment", modelForm.getResponse());
    }

    @Test
    public void testViewNewResponseWhenQueryIdDoesNotExist() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(Collections.emptyList()));

        mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/" + queryId + "/new-response?query_section=Eligibility")
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void testSaveNewResponse() throws Exception {

        when(financeCheckServiceMock.saveQueryPost(any(PostResource.class), eq(1L))).thenReturn(ServiceResult.serviceSuccess());

        FinanceChecksQueriesAddResponseForm formIn = new FinanceChecksQueriesAddResponseForm();
        Cookie formCookie = createFormCookie(formIn);

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+ queryId + "/new-response?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", "Query text")
                .cookie(formCookie))
                .andExpect(redirectedUrl("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility"))
                .andReturn();

        verify(financeCheckServiceMock).saveQueryPost(savePostArgumentCaptor.capture(), eq(1L));

        assertEquals("Query text", savePostArgumentCaptor.getAllValues().get(0).body);
        assertEquals(loggedInUser, savePostArgumentCaptor.getAllValues().get(0).author);
        assertEquals(0, savePostArgumentCaptor.getAllValues().get(0).attachments.size());
        assertTrue(ZonedDateTime.now().compareTo(savePostArgumentCaptor.getAllValues().get(0).createdOn) >= 0);

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query text", form.getResponse());
        assertEquals(null, form.getAttachment());

        Optional<Cookie> cookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_queries_new_response_attachments_" + projectId + "_" + applicantOrganisationId + "_" + queryId))
                .findAny();
        assertEquals(true, cookieFound.get().getValue().isEmpty());

        Optional<Cookie> formCookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_queries_new_response_form_" + projectId + "_" + applicantOrganisationId + "_" + queryId))
                .findAny();
        assertEquals(true, formCookieFound.get().getValue().isEmpty());
    }

    @Test
    public void testSaveNewResponseNoFieldsSet() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+ queryId + "/new-response?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", ""))
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
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

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+ queryId + "/new-response?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", tooLong))
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();


        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
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

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+ queryId + "/new-response?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", tooManyWords))
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
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

        when(financeCheckServiceMock.uploadFile(projectId, "application/pdf", 11, "testFile.pdf", "My content!".getBytes()))
                .thenReturn(ServiceResult.serviceSuccess(attachment));
        when(financeCheckServiceMock.getAttachment(1L)).thenReturn(ServiceResult.serviceSuccess(attachment));

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        MvcResult result = mockMvc.perform(
                fileUpload("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/1/new-response").
                        file(uploadedFile).
                        param("uploadAttachment", ""))
                .andExpect(cookie().exists("finance_checks_queries_new_response_attachments_"+projectId+"_"+applicantOrganisationId+"_"+1L))
                .andExpect(redirectedUrl("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/" + queryId + "/new-response"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<>();
        expectedAttachmentIds.add(1L);
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_queries_new_response_attachments_" + projectId + "_" + applicantOrganisationId + "_" + queryId));

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals(uploadedFile, form.getAttachment());

    }

    @Test
    public void testDownloadResponseAttachmentFailsNoContent() throws Exception {
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+ queryId +"/new-response/attachment/1?query_section=Eligibility"))
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
    public void testCancelNewResponse() throws Exception {

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie ck = createAttachmentsCookie(attachmentIds);

        FinanceChecksQueriesAddResponseForm formIn = new FinanceChecksQueriesAddResponseForm();
        Cookie formCookie = createFormCookie(formIn);

        when(financeCheckServiceMock.deleteFile(1L)).thenReturn(ServiceResult.serviceSuccess());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/" + queryId + "/new-response/cancel?query_section=Eligibility")
                    .cookie(ck)
                    .cookie(formCookie))
                .andExpect(redirectedUrl("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility"))
                .andReturn();

        Optional<Cookie> cookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_queries_new_response_attachments_" + projectId + "_" + applicantOrganisationId + "_" + queryId))
                .findAny();
        assertEquals(true, cookieFound.get().getValue().isEmpty());

        Optional<Cookie> formCookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_queries_new_response_form_" + projectId + "_" + applicantOrganisationId + "_" + queryId))
                .findAny();
        assertEquals(true, formCookieFound.get().getValue().isEmpty());

        verify(financeCheckServiceMock).deleteFile(1L);
    }

    @Test
    public void testViewNewResponseWithAttachments() throws Exception {

        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L, null);

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        when(financeCheckServiceMock.getAttachment(1L)).thenReturn(ServiceResult.serviceSuccess(attachment));

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        Cookie cookie = new Cookie("finance_checks_queries_new_response_attachments"+"_"+projectId+"_"+applicantOrganisationId+"_"+1L, encryptedData);
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+ queryId + "/new-response?query_section=Eligibility")
                .cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        FinanceChecksQueriesViewModel queryViewModel = (FinanceChecksQueriesViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Eligibility", queryViewModel.getQuerySection());
        assertTrue(queryViewModel.getFinanceContact().isPresent());
        assertEquals("e@mail.com", queryViewModel.getFinanceContact().get().getEmail());
        assertEquals("User1", queryViewModel.getFinanceContact().get().getUserName());
        assertEquals("0117", queryViewModel.getFinanceContact().get().getPhoneNumber());
        assertEquals("Org1", queryViewModel.getOrganisationName());
        assertEquals("Project1", queryViewModel.getProjectName());
        assertEquals(applicantOrganisationId, queryViewModel.getOrganisationId());
        assertEquals(projectId, queryViewModel.getProjectId());
        assertEquals("/project/{projectId}/finance-check/organisation/{organisationId}/query", queryViewModel.getBaseUrl());
        assertEquals(4000, queryViewModel.getMaxQueryCharacters());
        assertEquals(400, queryViewModel.getMaxQueryWords());
        assertEquals(1L, queryViewModel.getQueryId().longValue());
        assertTrue(queryViewModel.isLeadPartnerOrganisation());
        assertEquals(1, queryViewModel.getNewAttachmentLinks().size());
        assertEquals("name", queryViewModel.getNewAttachmentLinks().get(1L));
    }

    @Test
    public void testRemoveAttachment() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        when(financeCheckServiceMock.deleteFile(1L)).thenReturn(ServiceResult.serviceSuccess());

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie cookie = createAttachmentsCookie(attachmentIds);
        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+ queryId + "/new-response?query_section=Eligibility")
                .param("removeAttachment", "1")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", "Query"))
                .andExpect(cookie().exists("finance_checks_queries_new_response_attachments_" + projectId + "_" + applicantOrganisationId + "_" + queryId))
                .andExpect(cookie().exists("finance_checks_queries_new_response_form_" + projectId + "_" + applicantOrganisationId + "_" + queryId))
                .andExpect(redirectedUrl("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/" + queryId + "/new-response?query_section=Eligibility"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<>();
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_queries_new_response_attachments_" + projectId + "_" + applicantOrganisationId + "_" + queryId));

        FinanceChecksQueriesAddResponseForm expectedForm = new FinanceChecksQueriesAddResponseForm();
        expectedForm.setResponse("Query");
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedForm), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_queries_new_response_form_" + projectId + "_" + applicantOrganisationId + "_" + queryId));

        verify(financeCheckServiceMock).deleteFile(1L);

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query", form.getResponse());
        assertEquals(null, form.getAttachment());
    }

    @Test
    public void testRemoveAttachmentDoesNotRemoveAttachmentNotInCookie() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L, null);

        when(financeCheckServiceMock.getAttachment(1L)).thenReturn(ServiceResult.serviceSuccess(attachment));

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie cookie = createAttachmentsCookie(attachmentIds);
        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+ queryId + "/new-response?query_section=Eligibility")
                .param("removeAttachment", "2")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", "Query"))
                .andExpect(redirectedUrl("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/" + queryId + "/new-response?query_section=Eligibility"))
                .andReturn();

        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(attachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_queries_new_response_attachments_" + projectId + "_" + applicantOrganisationId + "_" + queryId));

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query", form.getResponse());
        assertEquals(null, form.getAttachment());
    }

    @Test
    public void testSaveNewResponseQueryCannotRespondToQuery() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        when(financeCheckServiceMock.saveQueryPost(any(PostResource.class), eq(5L))).thenReturn(ServiceResult.serviceFailure(CommonFailureKeys.GENERAL_FORBIDDEN));
        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/5/new-response?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", "Query"))
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        List<? extends ObjectError> errors = (List<? extends ObjectError>) result.getModelAndView().getModel().get("nonFormErrors");
        assertEquals(1, errors.size());
        assertEquals("validation.notesandqueries.query.response.save.failed", errors.get(0).getCode());
    }

    private Cookie createAttachmentsCookie(List<Long> attachmentIds) throws Exception {
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        return new Cookie("finance_checks_queries_new_response_attachments_" + projectId + "_" + applicantOrganisationId + "_" + queryId, encryptedData);
    }

    private Cookie createFormCookie(FinanceChecksQueriesAddResponseForm form) throws Exception {
        String cookieContent = JsonUtil.getSerializedObject(form);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        return new Cookie("finance_checks_queries_new_response_form_" + projectId + "_" + applicantOrganisationId + "_" + queryId, encryptedData);
    }

    @Override
    protected FinanceChecksQueriesController supplyControllerUnderTest() {
        return new FinanceChecksQueriesController();
    }
}
