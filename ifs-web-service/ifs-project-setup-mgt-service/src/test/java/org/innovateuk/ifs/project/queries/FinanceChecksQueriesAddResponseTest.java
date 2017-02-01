package org.innovateuk.ifs.project.queries;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.project.queries.controller.FinanceChecksQueriesAddResponseController;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesAddResponseForm;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesAddResponseViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceChecksQueriesAddResponseTest extends BaseControllerMockMVCTest<FinanceChecksQueriesAddResponseController> {

    private Long projectId = 3L;
    private Long applicantOrganisationId = 22L;

    ApplicationResource applicationResource = newApplicationResource().build();

    ProjectResource projectResource = newProjectResource().withId(projectId).withName("Project1").withApplication(applicationResource).build();

    OrganisationResource leadOrganisationResource = newOrganisationResource().withName("Org1").withId(applicantOrganisationId).build();

    ProjectUserResource projectUser = newProjectUserResource().withOrganisation(applicantOrganisationId).withUserName("User1").withEmail("e@mail.com").withPhoneNumber("0117").withRoleName(UserRoleType.FINANCE_CONTACT).build();

    @Before public void setup() {
        super.setUp();
        this.setupCookieUtil();
        // populate viewmodel
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(organisationService.getOrganisationById(applicantOrganisationId)).thenReturn(leadOrganisationResource);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisationResource);
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(Arrays.asList(projectUser));
    }

    @Test
    public void testViewNewResponse() throws Exception {

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/1/new-response?query_section=Eligibility"))
                .andExpect(view().name("project/financecheck/new-response"))
                .andReturn();

        FinanceChecksQueriesAddResponseViewModel responseViewModel = (FinanceChecksQueriesAddResponseViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Eligibility", responseViewModel.getQuerySection());
        assertEquals("e@mail.com", responseViewModel.getFinanceContactEmail());
        assertEquals("User1", responseViewModel.getFinanceContactName());
        assertEquals("0117", responseViewModel.getFinanceContactPhoneNumber());
        assertEquals("Org1", responseViewModel.getOrganisationName());
        assertEquals("Project1", responseViewModel.getProjectName());
        assertEquals(applicantOrganisationId, responseViewModel.getOrganisationId());
        assertEquals(projectId, responseViewModel.getProjectId());
        assertEquals(1L, responseViewModel.getQueryId().longValue());
        assertEquals("/project/{projectId}/finance-check/organisation/{organisationId}/query/{queryId}/new-response", responseViewModel.getBaseUrl());
        assertEquals(4000, responseViewModel.getMaxQueryCharacters());
        assertEquals(400, responseViewModel.getMaxQueryWords());
        assertTrue(responseViewModel.isLeadPartnerOrganisation());
        assertEquals(0, responseViewModel.getNewAttachmentLinks().size());
    }

    @Test
    public void testSaveNewResponse() throws Exception {

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/1/new-response?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("query", "Query text"))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility**"))
                .andReturn();

        // TODO verify data saved
        //verify()

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query text", form.getQuery());
        assertEquals(null, form.getAttachment());
    }

    @Test
    public void testSaveNewResponseNoFieldsSet() throws Exception {

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/1/new-response?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("query", ""))
                .andExpect(view().name("project/financecheck/new-response"))
                .andReturn();

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("", form.getQuery());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("query"));
        assertEquals("The response cannot be empty.", bindingResult.getFieldError("query").getDefaultMessage());
    }

    @Test
    public void testSaveNewResponseFieldsTooLong() throws Exception {

        String tooLong = StringUtils.leftPad("a", 4001, 'a');

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/1/new-response?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("query", tooLong))
                .andExpect(view().name("project/financecheck/new-response"))
                .andReturn();


        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals(tooLong, form.getQuery());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("query"));
        assertEquals("The response is too long, please reduce it to {1} characters.", bindingResult.getFieldError("query").getDefaultMessage());
    }

    @Test
    public void testSaveNewResponseTooManyWords() throws Exception {

        String tooManyWords = StringUtils.leftPad("a ", 802, "a ");

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/1/new-response?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("query", tooManyWords))
                .andExpect(view().name("project/financecheck/new-response"))
                .andReturn();

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals(tooManyWords, form.getQuery());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("query"));
        assertEquals("The response is too long, please reduce it {0} words.", bindingResult.getFieldError("query").getDefaultMessage());
    }

    @Test
    public void testSaveNewResponseAttachment() throws Exception {

        MockMultipartFile uploadedFile = new MockMultipartFile("testFile", "testFile.pdf", "application/pdf", "My content!".getBytes());

        MvcResult result = mockMvc.perform(
                fileUpload("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/1/new-response?query_section=Eligibility").
                        file(uploadedFile).param("uploadAttachment", ""))
                .andExpect(cookie().exists("finance_checks_queries_new_response_attachments_"+projectId+"_"+applicantOrganisationId+"_"+1L))
                .andExpect(view().name("project/financecheck/new-response"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<Long>();
        expectedAttachmentIds.add(0L);
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_queries_new_response_attachments_"+projectId+"_"+applicantOrganisationId+"_"+1L));

        // TODO verify file saved

    }

    @Test
    public void testDownloadAttachmentFailsNoContent() throws Exception {
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/1/new-response/attachment/1?query_section=Eligibility"))
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

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/1/new-response/cancel?query_section=Eligibility"))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility**"))
                .andReturn();

        Optional<Cookie> cookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_queries_new_response_attachments_"+projectId+"_"+applicantOrganisationId+"_"+1L))
                .findAny();
        assertEquals(true, cookieFound.get().getValue().isEmpty());
    }

    @Test
    public void testViewNewResponseWithAttachments() throws Exception {

        List<Long> attachmentIds = new ArrayList<Long>();
        attachmentIds.add(1L);
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        Cookie cookie = new Cookie("finance_checks_queries_new_response_attachments"+"_"+projectId+"_"+applicantOrganisationId+"_"+1L, encryptedData);
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/1/new-response?query_section=Eligibility")
                .cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("project/financecheck/new-response"))
                .andReturn();

        FinanceChecksQueriesAddResponseViewModel queryViewModel = (FinanceChecksQueriesAddResponseViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Eligibility", queryViewModel.getQuerySection());
        assertEquals("e@mail.com", queryViewModel.getFinanceContactEmail());
        assertEquals("User1", queryViewModel.getFinanceContactName());
        assertEquals("0117", queryViewModel.getFinanceContactPhoneNumber());
        assertEquals("Org1", queryViewModel.getOrganisationName());
        assertEquals("Project1", queryViewModel.getProjectName());
        assertEquals(applicantOrganisationId, queryViewModel.getOrganisationId());
        assertEquals(projectId, queryViewModel.getProjectId());
        assertEquals("/project/{projectId}/finance-check/organisation/{organisationId}/query/{queryId}/new-response", queryViewModel.getBaseUrl());
        assertEquals(4000, queryViewModel.getMaxQueryCharacters());
        assertEquals(400, queryViewModel.getMaxQueryWords());
        assertEquals(1L, queryViewModel.getQueryId().longValue());
        assertTrue(queryViewModel.isLeadPartnerOrganisation());
        assertEquals(1, queryViewModel.getNewAttachmentLinks().size());
        assertEquals("file_1", queryViewModel.getNewAttachmentLinks().get(1L));
    }

    @Test
    public void testRemoveAttachment() throws Exception {

        List<Long> attachmentIds = new ArrayList<Long>();
        attachmentIds.add(1L);
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        Cookie cookie = new Cookie("finance_checks_queries_new_response_attachments_"+projectId+"_"+applicantOrganisationId+"_"+1L, encryptedData);
        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/1/new-response?query_section=Eligibility")
                .param("removeAttachment", "1")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("query", "Query"))
                .andExpect(view().name("project/financecheck/new-response"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<>();
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_queries_new_response_attachments_"+projectId+"_"+applicantOrganisationId+"_"+1L));
        // TODO verify file removed

        FinanceChecksQueriesAddResponseForm form = (FinanceChecksQueriesAddResponseForm) result.getModelAndView().getModel().get("form");
        assertEquals("Query", form.getQuery());
        assertEquals(null, form.getAttachment());
    }

    @Override
    protected FinanceChecksQueriesAddResponseController supplyControllerUnderTest() {
        return new FinanceChecksQueriesAddResponseController();
    }
}
