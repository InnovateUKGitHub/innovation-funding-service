package org.innovateuk.ifs.project.queries;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;

import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.innovateuk.threads.resource.QueryResource;
import org.innovateuk.ifs.project.queries.controller.FinanceChecksQueriesController;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesAddResponseForm;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.util.JsonUtil;
import org.innovateuk.threads.resource.PostResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import java.util.*;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceChecksQueriesControllerTest extends BaseControllerMockMVCTest<FinanceChecksQueriesController> {

    private Long projectId = 3L;
    private Long financeTeamUserId = 18L;
    private Long applicantFinanceContactUserId = 55L;
    private Long innovateOrganisationId = 11L;
    private Long applicantOrganisationId = 22L;
    private Long projectFinanceId = 45L;
    private Long queryId = 1L;

    ApplicationResource applicationResource = newApplicationResource().build();
    ProjectResource projectResource = newProjectResource().withId(projectId).withName("Project1").withApplication(applicationResource).build();

    OrganisationResource innovateOrganisationResource = newOrganisationResource().withName("Innovate").withId(innovateOrganisationId).build();

    OrganisationResource leadOrganisationResource = newOrganisationResource().withName("Org1").withId(applicantOrganisationId).build();

    ProjectUserResource projectUser = newProjectUserResource().withOrganisation(applicantOrganisationId).withUserName("User1").withEmail("e@mail.com").withPhoneNumber("0117").withRoleName(UserRoleType.FINANCE_CONTACT).build();

    RoleResource financeTeamRole = newRoleResource().withType(PROJECT_FINANCE).build();
    UserResource financeTeamUser = newUserResource().withFirstName("A").withLastName("Z").withId(financeTeamUserId).withRolesGlobal(Arrays.asList(financeTeamRole)).build();
    UserResource projectManagerUser = newUserResource().withFirstName("B").withLastName("Z").withId(applicantFinanceContactUserId).build();


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
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(organisationService.getOrganisationById(applicantOrganisationId)).thenReturn(leadOrganisationResource);
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
    public void testGetReadOnlyView() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility"))
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        FinanceChecksQueriesViewModel queryViewModel = (FinanceChecksQueriesViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Eligibility", queryViewModel.getQuerySection());
        assertEquals("e@mail.com", queryViewModel.getFinanceContactEmail());
        assertEquals("User1", queryViewModel.getFinanceContactName());
        assertEquals("0117", queryViewModel.getFinanceContactPhoneNumber());
        assertEquals("Org1", queryViewModel.getOrganisationName());
        assertEquals("Project1", queryViewModel.getProjectName());
        assertEquals(applicantOrganisationId, queryViewModel.getOrganisationId());
        assertEquals(projectId, queryViewModel.getProjectId());

        assertEquals(3, queryViewModel.getQueries().size());
        assertEquals("Query title", queryViewModel.getQueries().get(0).getTitle());
        assertEquals(FinanceChecksSectionType.ELIGIBILITY, queryViewModel.getQueries().get(0).getSectionType());
        assertEquals(false, queryViewModel.getQueries().get(0).isAwaitingResponse());
        assertEquals(applicantOrganisationId, queryViewModel.getQueries().get(0).getOrganisationId());
        assertEquals(projectId, queryViewModel.getQueries().get(0).getProjectId());
        assertEquals(1L, queryViewModel.getQueries().get(0).getId().longValue());
        assertEquals(2, queryViewModel.getQueries().get(0).getViewModelPosts().size());
        assertEquals("Question", queryViewModel.getQueries().get(0).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUserId, queryViewModel.getQueries().get(0).getViewModelPosts().get(0).author.getId());
        assertEquals("A Z - Innovate (Finance team)", queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getUsername());
        assertTrue(LocalDateTime.now().plusMinutes(10L).isAfter(queryViewModel.getQueries().get(0).getViewModelPosts().get(0).createdOn));
        assertEquals(1, queryViewModel.getQueries().get(0).getViewModelPosts().get(0).attachments.size());
        assertEquals(23L, queryViewModel.getQueries().get(0).getViewModelPosts().get(0).attachments.get(0).id.longValue());
        assertEquals("file1.txt", queryViewModel.getQueries().get(0).getViewModelPosts().get(0).attachments.get(0).name);
        assertEquals("Response", queryViewModel.getQueries().get(0).getViewModelPosts().get(1).body);
        assertEquals(applicantFinanceContactUserId, queryViewModel.getQueries().get(0).getViewModelPosts().get(1).author.getId());
        assertEquals("B Z - Org1", queryViewModel.getQueries().get(0).getViewModelPosts().get(1).getUsername());
        assertTrue(LocalDateTime.now().plusMinutes(20L).isAfter(queryViewModel.getQueries().get(0).getViewModelPosts().get(1).createdOn));
        assertEquals(0, queryViewModel.getQueries().get(0).getViewModelPosts().get(1).attachments.size());
        assertEquals("Query2 title", queryViewModel.getQueries().get(1).getTitle());
        assertEquals(FinanceChecksSectionType.ELIGIBILITY, queryViewModel.getQueries().get(1).getSectionType());
        assertEquals(true, queryViewModel.getQueries().get(1).isAwaitingResponse());
        assertEquals(applicantOrganisationId, queryViewModel.getQueries().get(1).getOrganisationId());
        assertEquals(projectId, queryViewModel.getQueries().get(1).getProjectId());
        assertEquals(3L, queryViewModel.getQueries().get(1).getId().longValue());
        assertEquals(1, queryViewModel.getQueries().get(1).getViewModelPosts().size());
        assertEquals("Question2", queryViewModel.getQueries().get(1).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUserId, queryViewModel.getQueries().get(1).getViewModelPosts().get(0).author.getId());
        assertEquals("A Z - Innovate (Finance team)", queryViewModel.getQueries().get(1).getViewModelPosts().get(0).getUsername());
        assertTrue(LocalDateTime.now().plusMinutes(15L).isAfter(queryViewModel.getQueries().get(1).getViewModelPosts().get(0).createdOn));
        assertEquals(0, queryViewModel.getQueries().get(1).getViewModelPosts().get(0).attachments.size());

        assertEquals("Query title3", queryViewModel.getQueries().get(2).getTitle());
        assertEquals(FinanceChecksSectionType.ELIGIBILITY, queryViewModel.getQueries().get(2).getSectionType());
        assertEquals(false, queryViewModel.getQueries().get(2).isAwaitingResponse());
        assertEquals(applicantOrganisationId, queryViewModel.getQueries().get(2).getOrganisationId());
        assertEquals(projectId, queryViewModel.getQueries().get(2).getProjectId());
        assertEquals(5L, queryViewModel.getQueries().get(2).getId().longValue());
        assertEquals(2, queryViewModel.getQueries().get(2).getViewModelPosts().size());
        assertEquals("Question3", queryViewModel.getQueries().get(2).getViewModelPosts().get(0).body);
        assertEquals(financeTeamUserId, queryViewModel.getQueries().get(2).getViewModelPosts().get(0).author.getId());
        assertEquals("A Z - Innovate (Finance team)", queryViewModel.getQueries().get(2).getViewModelPosts().get(0).getUsername());
        assertTrue(LocalDateTime.now().isAfter(queryViewModel.getQueries().get(2).getViewModelPosts().get(0).createdOn));
        assertEquals(0, queryViewModel.getQueries().get(2).getViewModelPosts().get(0).attachments.size());
        assertEquals("Response3", queryViewModel.getQueries().get(2).getViewModelPosts().get(1).body);
        assertEquals(applicantFinanceContactUserId, queryViewModel.getQueries().get(2).getViewModelPosts().get(1).author.getId());
        assertEquals("B Z - Org1", queryViewModel.getQueries().get(2).getViewModelPosts().get(1).getUsername());
        assertTrue(LocalDateTime.now().plusMinutes(10L).isAfter(queryViewModel.getQueries().get(2).getViewModelPosts().get(1).createdOn));
        assertEquals(0, queryViewModel.getQueries().get(2).getViewModelPosts().get(1).attachments.size());
    }

    @Test
    public void testDownloadAttachmentFailsNoContent() throws Exception {

        FileEntryResource fileEntry = new FileEntryResource(1L, "name", "mediaType", 2L);

        when(financeCheckServiceMock.downloadFile(1L)).thenReturn(ServiceResult.serviceFailure(CommonFailureKeys.GENERAL_NOT_FOUND));
        when(financeCheckServiceMock.getAttachmentInfo(1L)).thenReturn(ServiceResult.serviceSuccess(fileEntry));
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/attachment/1?query_section=Eligibility"))
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
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/attachment/1?query_section=Eligibility"))
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

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+queryId+"/new-response?query_section=Eligibility"))
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        FinanceChecksQueriesViewModel responseViewModel = (FinanceChecksQueriesViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Eligibility", responseViewModel.getQuerySection());
        assertEquals("e@mail.com", responseViewModel.getFinanceContactEmail());
        assertEquals("User1", responseViewModel.getFinanceContactName());
        assertEquals("0117", responseViewModel.getFinanceContactPhoneNumber());
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
    }

    @Test
    public void testSaveNewResponse() throws Exception {

        when(financeCheckServiceMock.saveQueryPost(any(PostResource.class), eq(1L))).thenReturn(ServiceResult.serviceSuccess());

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+queryId+"/new-response?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", "Query text"))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility**"))
                .andReturn();

        verify(financeCheckServiceMock).saveQueryPost(savePostArgumentCaptor.capture(), eq(1L));

        assertEquals("Query text", savePostArgumentCaptor.getAllValues().get(0).body);
        assertEquals(loggedInUser, savePostArgumentCaptor.getAllValues().get(0).author);
        assertEquals(0, savePostArgumentCaptor.getAllValues().get(0).attachments.size());
        assertTrue(LocalDateTime.now().compareTo(savePostArgumentCaptor.getAllValues().get(0).createdOn) >= 0);

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query text", form.getResponse());
        assertEquals(null, form.getAttachment());
    }

    @Test
    public void testSaveNewResponseNoFieldsSet() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+queryId+"/new-response?query_section=Eligibility")
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

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+queryId+"/new-response?query_section=Eligibility")
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

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+queryId+"/new-response?query_section=Eligibility")
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
        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L);

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
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<>();
        expectedAttachmentIds.add(1L);
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_queries_new_response_attachments_"+projectId+"_"+applicantOrganisationId+"_"+1L));

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals(uploadedFile, form.getAttachment());

    }

    @Test
    public void testDownloadResponseAttachmentFailsNoContent() throws Exception {
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+queryId+"/new-response/attachment/1?query_section=Eligibility"))
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

        when(financeCheckServiceMock.deleteFile(1L)).thenReturn(ServiceResult.serviceSuccess());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+queryId+"/new-response/cancel?query_section=Eligibility")
                    .cookie(ck))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility**"))
                .andReturn();

        Optional<Cookie> cookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_queries_new_response_attachments_"+projectId+"_"+applicantOrganisationId+"_"+1L))
                .findAny();
        assertEquals(true, cookieFound.get().getValue().isEmpty());

        verify(financeCheckServiceMock).deleteFile(1L);
    }

    @Test
    public void testViewNewResponseWithAttachments() throws Exception {

        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L);

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        when(financeCheckServiceMock.getAttachment(1L)).thenReturn(ServiceResult.serviceSuccess(attachment));

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        Cookie cookie = new Cookie("finance_checks_queries_new_response_attachments"+"_"+projectId+"_"+applicantOrganisationId+"_"+1L, encryptedData);
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+queryId+"/new-response?query_section=Eligibility")
                .cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        FinanceChecksQueriesViewModel queryViewModel = (FinanceChecksQueriesViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Eligibility", queryViewModel.getQuerySection());
        assertEquals("e@mail.com", queryViewModel.getFinanceContactEmail());
        assertEquals("User1", queryViewModel.getFinanceContactName());
        assertEquals("0117", queryViewModel.getFinanceContactPhoneNumber());
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
        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+queryId+"/new-response?query_section=Eligibility")
                .param("removeAttachment", "1")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", "Query"))
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<>();
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_queries_new_response_attachments_"+projectId+"_"+applicantOrganisationId+"_"+queryId));

        verify(financeCheckServiceMock).deleteFile(1L);

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query", form.getResponse());
        assertEquals(null, form.getAttachment());

        FinanceChecksQueriesViewModel queryViewModel = (FinanceChecksQueriesViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(0, queryViewModel.getNewAttachmentLinks().size());
    }

    @Test
    public void testRemoveAttachmentDoesNotRemoveAttachmentNotInCookie() throws Exception {

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withProject(projectId).withOrganisation(applicantOrganisationId).withId(projectFinanceId).build();
        when(projectFinanceService.getProjectFinance(projectId, applicantOrganisationId)).thenReturn(projectFinanceResource);
        when(financeCheckServiceMock.getQueries(projectFinanceId)).thenReturn(ServiceResult.serviceSuccess(queries));

        AttachmentResource attachment = new AttachmentResource(1L, "name", "mediaType", 2L);

        when(financeCheckServiceMock.getAttachment(1L)).thenReturn(ServiceResult.serviceSuccess(attachment));

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(1L);
        Cookie cookie = createAttachmentsCookie(attachmentIds);
        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/"+queryId+"/new-response?query_section=Eligibility")
                .param("removeAttachment", "2")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("response", "Query"))
                .andExpect(view().name("project/financecheck/queries"))
                .andReturn();

        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(attachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_queries_new_response_attachments_"+projectId+"_"+applicantOrganisationId+"_"+queryId));

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query", form.getResponse());
        assertEquals(null, form.getAttachment());

        FinanceChecksQueriesViewModel queryViewModel = (FinanceChecksQueriesViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(1, queryViewModel.getNewAttachmentLinks().size());
        assertEquals("name", queryViewModel.getNewAttachmentLinks().get(1L));
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

    private Cookie createAttachmentsCookie(List<Long> attachmentIds) throws Exception{
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        return new Cookie("finance_checks_queries_new_response_attachments_"+projectId+"_"+applicantOrganisationId+"_"+queryId, encryptedData);
    }

    @Override
    protected FinanceChecksQueriesController supplyControllerUnderTest() {
        return new FinanceChecksQueriesController();
    }
}
