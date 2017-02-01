package org.innovateuk.ifs.project.queries;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;

import org.innovateuk.ifs.notesandqueries.resource.thread.FinanceChecksSectionType;
import org.innovateuk.ifs.project.queries.controller.FinanceChecksQueriesController;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceChecksQueriesControllerTest extends BaseControllerMockMVCTest<FinanceChecksQueriesController> {

    private Long projectId = 3L;
    private Long financeTeamUserId = 18L;
    private Long applicantFinanceContactUserId = 55L;
    private Long innovateOrganisationId = 11L;
    private Long applicantOrganisationId = 22L;

    ApplicationResource applicationResource = newApplicationResource().build();
    ProjectResource projectResource = newProjectResource().withId(projectId).withName("Project1").withApplication(applicationResource).build();

    OrganisationResource innovateOrganisationResource = newOrganisationResource().withName("Innovate").withId(innovateOrganisationId).build();

    OrganisationResource leadOrganisationResource = newOrganisationResource().withName("Org1").withId(applicantOrganisationId).build();

    ProjectUserResource projectUser = newProjectUserResource().withOrganisation(applicantOrganisationId).withUserName("User1").withEmail("e@mail.com").withPhoneNumber("0117").withRoleName(UserRoleType.FINANCE_CONTACT).build();

    RoleResource financeTeamRole = newRoleResource().withType(PROJECT_FINANCE).build();
    UserResource financeTeamUser = newUserResource().withFirstName("A").withLastName("Z").withId(financeTeamUserId).withRolesGlobal(Arrays.asList(financeTeamRole)).build();
    UserResource projectManagerUser = newUserResource().withFirstName("B").withLastName("Z").withId(applicantFinanceContactUserId).build();

    @Before
    public void setup() {
        when(userService.findById(financeTeamUserId)).thenReturn(financeTeamUser);
        when(organisationService.getOrganisationForUser(financeTeamUserId)).thenReturn(innovateOrganisationResource);
        when(userService.findById(applicantFinanceContactUserId)).thenReturn(projectManagerUser);
        when(organisationService.getOrganisationForUser(applicantFinanceContactUserId)).thenReturn(leadOrganisationResource);
        when(userService.findById(applicantFinanceContactUserId)).thenReturn(projectManagerUser);
    }
    @Test
    public void testGetReadOnlyView() throws Exception {

        // populate viewmodel
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(organisationService.getOrganisationById(applicantOrganisationId)).thenReturn(leadOrganisationResource);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisationResource);
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(Arrays.asList(projectUser));

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

        assertEquals(2, queryViewModel.getQueries().size());
        assertEquals("Query title", queryViewModel.getQueries().get(0).getTitle());
        assertEquals(FinanceChecksSectionType.ELIGIBILITY, queryViewModel.getQueries().get(0).getSectionType());
        assertEquals(false, queryViewModel.getQueries().get(0).isAwaitingResponse());
        assertEquals(applicantOrganisationId, queryViewModel.getQueries().get(0).getOrganisationId());
        assertEquals(projectId, queryViewModel.getQueries().get(0).getProjectId());
        assertEquals(1L, queryViewModel.getQueries().get(0).getId().longValue());
        assertEquals(2, queryViewModel.getQueries().get(0).getViewModelPosts().size());
        assertEquals("Question", queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getPostBody());
        assertEquals(financeTeamUserId, queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getUserId());
        assertEquals("A Z - Innovate (Finance team)", queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getUsername());
        assertTrue(LocalDateTime.now().plusMinutes(10L).isAfter(queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getCreatedOn()));
        assertEquals(1, queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getViewModelAttachments().size());
        assertEquals(23L, queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getViewModelAttachments().get(0).getFileEntryId().longValue());
        assertEquals("file0", queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getViewModelAttachments().get(0).getFilename());
        assertEquals("Response", queryViewModel.getQueries().get(0).getViewModelPosts().get(1).getPostBody());
        assertEquals(applicantFinanceContactUserId, queryViewModel.getQueries().get(0).getViewModelPosts().get(1).getUserId());
        assertEquals("B Z - Org1", queryViewModel.getQueries().get(0).getViewModelPosts().get(1).getUsername());
        assertTrue(LocalDateTime.now().plusMinutes(20L).isAfter(queryViewModel.getQueries().get(0).getViewModelPosts().get(1).getCreatedOn()));
        assertEquals(0, queryViewModel.getQueries().get(0).getViewModelPosts().get(1).getViewModelAttachments().size());
        assertEquals("Query2 title", queryViewModel.getQueries().get(1).getTitle());
        assertEquals(FinanceChecksSectionType.ELIGIBILITY, queryViewModel.getQueries().get(1).getSectionType());
        assertEquals(true, queryViewModel.getQueries().get(1).isAwaitingResponse());
        assertEquals(applicantOrganisationId, queryViewModel.getQueries().get(1).getOrganisationId());
        assertEquals(projectId, queryViewModel.getQueries().get(1).getProjectId());
        assertEquals(3L, queryViewModel.getQueries().get(1).getId().longValue());
        assertEquals(1, queryViewModel.getQueries().get(1).getViewModelPosts().size());
        assertEquals("Question2", queryViewModel.getQueries().get(1).getViewModelPosts().get(0).getPostBody());
        assertEquals(financeTeamUserId, queryViewModel.getQueries().get(1).getViewModelPosts().get(0).getUserId());
        assertEquals("A Z - Innovate (Finance team)", queryViewModel.getQueries().get(1).getViewModelPosts().get(0).getUsername());
        assertTrue(LocalDateTime.now().plusMinutes(10L).isAfter(queryViewModel.getQueries().get(1).getViewModelPosts().get(0).getCreatedOn()));
        assertEquals(0, queryViewModel.getQueries().get(1).getViewModelPosts().get(0).getViewModelAttachments().size());
    }

    @Test
    public void testDownloadAttachmentFailsNoContent() throws Exception {
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + applicantOrganisationId + "/query/attachment/1?query_section=Eligibility"))
                .andExpect(status().isNoContent())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Override
    protected FinanceChecksQueriesController supplyControllerUnderTest() {
        return new FinanceChecksQueriesController();
    }
}
