package org.innovateuk.ifs.project.queries;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.notesandqueries.resource.thread.FinanceChecksSectionType;
import org.innovateuk.ifs.project.queries.controller.FinanceChecksQueriesAddQueryController;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesNewQueryForm;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesAddQueryViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
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
    private Long financeTeamUserId = 18L;
    private Long applicantOrganisationId = 22L;
    private Long innovateOrganisationId = 11L;
    private Long applicantFinanceContactUserId = 55L;

    ApplicationResource applicationResource = newApplicationResource().build();
    ProjectResource projectResource = newProjectResource().withId(projectId).withName("Project1").withApplication(applicationResource).build();

    OrganisationResource innovateOrganisationResource = newOrganisationResource().withName("Innovate").withId(innovateOrganisationId).build();

    OrganisationResource leadOrganisationResource = newOrganisationResource().withName("Org1").withId(applicantOrganisationId).build();

    ProjectUserResource projectUser = newProjectUserResource().withOrganisation(applicantOrganisationId).withUserName("User1").withEmail("e@mail.com").withPhoneNumber("0117").withRoleName(UserRoleType.FINANCE_CONTACT).build();

    RoleResource financeTeamRole = newRoleResource().withType(PROJECT_FINANCE).build();
    UserResource financeTeamUser = newUserResource().withFirstName("A").withLastName("Z").withId(financeTeamUserId).withRolesGlobal(Arrays.asList(financeTeamRole)).build();
    UserResource projectManagerUser = newUserResource().withFirstName("B").withLastName("Z").withId(applicantFinanceContactUserId).build();


    @Before public void setup() {
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
                .param("title", "Title")
                .param("query", "Query text")
                .param("section", FinanceChecksSectionType.ELIGIBILITY.name()))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility**"))
                .andReturn();

        // verify data saved
        //verify()

        FinanceChecksQueriesNewQueryForm form = (FinanceChecksQueriesNewQueryForm) result.getModelAndView().getModel().get("form");
        assertEquals("Title", form.getTitle());
        assertEquals("Query text", form.getQuery());
        assertEquals(FinanceChecksSectionType.ELIGIBILITY.name(), form.getSection().toUpperCase());
        assertEquals(null, form.getAttachment());
    }

    @Test
    public void testSaveNewQueryAttachment() throws Exception {

        MockMultipartFile uploadedFile = new MockMultipartFile("testFile", "testFile.pdf", "application/pdf", "My content!".getBytes());

        MvcResult result = mockMvc.perform(
                fileUpload("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query?query_section=Eligibility").
                        file(uploadedFile).param("uploadAttachment", ""))
                .andExpect(cookie().exists("finance_checks_queries_new_query_attachments_"+projectId+"_"+applicantOrganisationId))
                .andExpect(view().name("redirect:/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query?query_section=Eligibility"))
                .andReturn();

        List<Long> expectedAttachmentIds = new ArrayList<Long>();
        expectedAttachmentIds.add(1L);
        assertEquals(JsonUtil.getSerializedObject(expectedAttachmentIds), getDecryptedCookieValue(result.getResponse().getCookies(), "finance_checks_queries_new_query_attachments_"+projectId+"_"+applicantOrganisationId));

        FinanceChecksQueriesNewQueryForm form = (FinanceChecksQueriesNewQueryForm) result.getModelAndView().getModel().get("form");
        assertEquals(uploadedFile, form.getAttachment());

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

        FinanceChecksQueriesNewQueryForm expectedQueryForm = new FinanceChecksQueriesNewQueryForm();

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/new-query/cancel?query_section=Eligibility"))
                .andExpect(cookie().doesNotExist("finance_checks_queries_new_query_attachments"+"_"+projectId+"_"+applicantOrganisationId))
                .andExpect(redirectedUrlPattern("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query?query_section=Eligibility**"))
                .andReturn();
    }

    @Override
    protected FinanceChecksQueriesAddQueryController supplyControllerUnderTest() {
        return new FinanceChecksQueriesAddQueryController();
    }
}
