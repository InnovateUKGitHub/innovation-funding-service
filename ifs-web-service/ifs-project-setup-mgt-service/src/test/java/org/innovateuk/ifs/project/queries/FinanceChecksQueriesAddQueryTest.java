package org.innovateuk.ifs.project.queries;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.notesandqueries.resource.thread.FinanceChecksSectionType;
import org.innovateuk.ifs.project.queries.controller.FinanceChecksQueriesAddQueryController;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesAddQueryForm;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesAddQueryViewModel;
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

public class FinanceChecksQueriesAddQueryTest extends BaseControllerMockMVCTest<FinanceChecksQueriesAddQueryController> {

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
    public void testViewNewQuery() throws Exception {

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query?query_section=Eligibility"))
                .andExpect(view().name("project/financecheck/new-query"))
                .andReturn();

        FinanceChecksQueriesAddQueryViewModel queryViewModel = (FinanceChecksQueriesAddQueryViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Eligibility", queryViewModel.getQuerySection());
        assertEquals("e@mail.com", queryViewModel.getFinanceContactEmail());
        assertEquals("User1", queryViewModel.getFinanceContactName());
        assertEquals("0117", queryViewModel.getFinanceContactPhoneNumber());
        assertEquals("Org1", queryViewModel.getOrganisationName());
        assertEquals("Project1", queryViewModel.getProjectName());
        assertEquals(applicantOrganisationId, queryViewModel.getOrganisationId());
        assertEquals(projectId, queryViewModel.getProjectId());
        assertEquals("/project/{projectId}/finance-check/organisation/{organisationId}/query/new-query", queryViewModel.getBaseUrl());
        assertEquals(4000, queryViewModel.getMaxQueryCharacters());
        assertEquals(400, queryViewModel.getMaxQueryWords());
        assertEquals(255, queryViewModel.getMaxTitleCharacters());
        assertTrue(queryViewModel.isLeadPartnerOrganisation());
        assertEquals(0, queryViewModel.getNewAttachmentLinks().size());
    }

    @Test
    public void testSaveNewQuery() throws Exception {

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("queryTitle", "Title")
                .param("query", "Query text")
                .param("section", FinanceChecksSectionType.ELIGIBILITY.name()))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility**"))
                .andReturn();

        // TODO verify data saved
        //verify()

        FinanceChecksQueriesAddQueryForm form = (FinanceChecksQueriesAddQueryForm) result.getModelAndView().getModel().get("form");
        assertEquals("Title", form.getQueryTitle());
        assertEquals("Query text", form.getQuery());
        assertEquals(FinanceChecksSectionType.ELIGIBILITY.name(), form.getSection().toUpperCase());
        assertEquals(null, form.getAttachment());
    }

    @Test
    public void testSaveNewQueryNoFieldsSet() throws Exception {

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("queryTitle", "")
                .param("query", "")
                .param("section", ""))
                .andExpect(view().name("project/financecheck/new-query"))
                .andReturn();


        FinanceChecksQueriesAddQueryForm form = (FinanceChecksQueriesAddQueryForm) result.getModelAndView().getModel().get("form");
        assertEquals("", form.getQueryTitle());
        assertEquals("", form.getQuery());
        assertEquals("", form.getSection().toUpperCase());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(3, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("queryTitle"));
        assertEquals("The title cannot be empty.", bindingResult.getFieldError("queryTitle").getDefaultMessage());
        assertTrue(bindingResult.hasFieldErrors("query"));
        assertEquals("The query cannot be empty.", bindingResult.getFieldError("query").getDefaultMessage());
        assertTrue(bindingResult.hasFieldErrors("section"));
        assertEquals("The section is not recognised, please select a valid section.", bindingResult.getFieldError("section").getDefaultMessage());
    }

    @Test
    public void testSaveNewQueryFieldsTooLong() throws Exception {

        String tooLong = StringUtils.leftPad("a", 4001, 'a');

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("queryTitle", tooLong)
                .param("query", tooLong)
                .param("section", FinanceChecksSectionType.VIABILITY.name()))
                .andExpect(view().name("project/financecheck/new-query"))
                .andReturn();


        FinanceChecksQueriesAddQueryForm form = (FinanceChecksQueriesAddQueryForm) result.getModelAndView().getModel().get("form");
        assertEquals(tooLong, form.getQueryTitle());
        assertEquals(tooLong, form.getQuery());
        assertEquals(FinanceChecksSectionType.VIABILITY.name(), form.getSection().toUpperCase());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(2, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("queryTitle"));
        assertEquals("The title is too long, please reduce it to {1} characters.", bindingResult.getFieldError("queryTitle").getDefaultMessage());
        assertTrue(bindingResult.hasFieldErrors("query"));
        assertEquals("The query is too long, please reduce it to {1} characters.", bindingResult.getFieldError("query").getDefaultMessage());
    }

    @Test
    public void testSaveNewQueryTooManyWords() throws Exception {

        String tooManyWords = StringUtils.leftPad("a ", 802, "a ");

        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query?query_section=Eligibility")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("queryTitle", "Title")
                .param("query", tooManyWords)
                .param("section", FinanceChecksSectionType.VIABILITY.name()))
                .andExpect(view().name("project/financecheck/new-query"))
                .andReturn();


        FinanceChecksQueriesAddQueryForm form = (FinanceChecksQueriesAddQueryForm) result.getModelAndView().getModel().get("form");
        assertEquals("Title", form.getQueryTitle());
        assertEquals(tooManyWords, form.getQuery());
        assertEquals(FinanceChecksSectionType.VIABILITY.name(), form.getSection().toUpperCase());
        assertEquals(null, form.getAttachment());

        BindingResult bindingResult = form.getBindingResult();

        assertTrue(bindingResult.hasErrors());
        assertEquals(0, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getFieldErrorCount());
        assertTrue(bindingResult.hasFieldErrors("query"));
        assertEquals("The query is too long, please reduce it {0} words.", bindingResult.getFieldError("query").getDefaultMessage());
    }

    @Test
    public void testSaveNewQueryAttachment() throws Exception {

        MockMultipartFile uploadedFile = new MockMultipartFile("testFile", "testFile.pdf", "application/pdf", "My content!".getBytes());

        MvcResult result = mockMvc.perform(
                fileUpload("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query?query_section=Eligibility").
                        file(uploadedFile).param("uploadAttachment", ""))
                .andExpect(cookie().exists("finance_checks_queries_new_query_attachments_"+projectId+"_"+applicantOrganisationId))
                .andExpect(view().name("project/financecheck/new-query"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<Long>();
        expectedAttachmentIds.add(0L);
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_queries_new_query_attachments_"+projectId+"_"+applicantOrganisationId));

        // TODO verify file saved

    }

    @Test
    public void testDownloadAttachmentFailsNoContent() throws Exception {
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query/attachment/1?query_section=Eligibility"))
                .andExpect(status().isNoContent())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Test
    public void testCancelNewQuery() throws Exception {

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query/cancel?query_section=Eligibility"))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility**"))
                .andReturn();

        Optional<Cookie> cookieFound = Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals("finance_checks_queries_new_query_attachments_"+projectId+"_"+applicantOrganisationId))
                .findAny();
        assertEquals(true, cookieFound.get().getValue().isEmpty());
    }

    @Test
    public void testViewNewQueryWithAttachments() throws Exception {

        List<Long> attachmentIds = new ArrayList<Long>();
        attachmentIds.add(1L);
        String cookieContent = JsonUtil.getSerializedObject(attachmentIds);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        Cookie cookie = new Cookie("finance_checks_queries_new_query_attachments"+"_"+projectId+"_"+applicantOrganisationId, encryptedData);
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query?query_section=Eligibility")
                .cookie(cookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("project/financecheck/new-query"))
                .andReturn();

        FinanceChecksQueriesAddQueryViewModel queryViewModel = (FinanceChecksQueriesAddQueryViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Eligibility", queryViewModel.getQuerySection());
        assertEquals("e@mail.com", queryViewModel.getFinanceContactEmail());
        assertEquals("User1", queryViewModel.getFinanceContactName());
        assertEquals("0117", queryViewModel.getFinanceContactPhoneNumber());
        assertEquals("Org1", queryViewModel.getOrganisationName());
        assertEquals("Project1", queryViewModel.getProjectName());
        assertEquals(applicantOrganisationId, queryViewModel.getOrganisationId());
        assertEquals(projectId, queryViewModel.getProjectId());
        assertEquals("/project/{projectId}/finance-check/organisation/{organisationId}/query/new-query", queryViewModel.getBaseUrl());
        assertEquals(4000, queryViewModel.getMaxQueryCharacters());
        assertEquals(400, queryViewModel.getMaxQueryWords());
        assertEquals(255, queryViewModel.getMaxTitleCharacters());
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
        Cookie cookie = new Cookie("finance_checks_queries_new_query_attachments"+"_"+projectId+"_"+applicantOrganisationId, encryptedData);
        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query?query_section=Eligibility")
                .param("removeAttachment", "1")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("queryTitle", "Title")
                .param("query", "Query")
                .param("section", FinanceChecksSectionType.VIABILITY.name()))
                .andExpect(view().name("project/financecheck/new-query"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<>();
        assertEquals(URLEncoder.encode(JsonUtil.getSerializedObject(expectedAttachmentIds), CharEncoding.UTF_8),
                getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_queries_new_query_attachments_"+projectId+"_"+applicantOrganisationId));
        // TODO verify file removed

        FinanceChecksQueriesAddQueryForm form = (FinanceChecksQueriesAddQueryForm) result.getModelAndView().getModel().get("form");
        assertEquals("Title", form.getQueryTitle());
        assertEquals("Query", form.getQuery());
        assertEquals(FinanceChecksSectionType.VIABILITY.name(), form.getSection().toUpperCase());
        assertEquals(null, form.getAttachment());
    }

    @Override
    protected FinanceChecksQueriesAddQueryController supplyControllerUnderTest() {
        return new FinanceChecksQueriesAddQueryController();
    }
}
