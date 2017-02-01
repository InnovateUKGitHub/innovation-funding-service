package org.innovateuk.ifs.project.queries;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;

import org.innovateuk.ifs.notesandqueries.resource.thread.FinanceChecksSectionType;
import org.innovateuk.ifs.project.queries.controller.FinanceChecksQueriesController;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesNewQueryForm;
import org.innovateuk.ifs.project.queries.form.FinanceChecksQueriesAddResponseForm;
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
import org.springframework.validation.MapBindingResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

        assertTrue(queryDataLoadedCorrectly(queryViewModel));
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

    private boolean queryDataLoadedCorrectly(FinanceChecksQueriesViewModel queryViewModel) {
        boolean result = true;
        result &= 2 == queryViewModel.getQueries().size();
        result &= "Query title".equals(queryViewModel.getQueries().get(0).getTitle());
        result &= FinanceChecksSectionType.ELIGIBILITY.equals(queryViewModel.getQueries().get(0).getSectionType());
        result &= false == queryViewModel.getQueries().get(0).isAwaitingResponse();
        result &= applicantOrganisationId == queryViewModel.getQueries().get(0).getOrganisationId();
        result &= projectId == queryViewModel.getQueries().get(0).getProjectId();
        result &= 1L == queryViewModel.getQueries().get(0).getId();
        result &= 2 == queryViewModel.getQueries().get(0).getViewModelPosts().size();
        result &= "Question".equals(queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getPostBody());
        result &= financeTeamUserId == queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getUserId();
        result &= "A Z - Innovate (Finance team)".equals(queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getUsername());
        result &= LocalDateTime.now().plusMinutes(10L).isAfter(queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getCreatedOn());
        result &= 1 == queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getViewModelAttachments().size();
        result &= 23L == queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getViewModelAttachments().get(0).getFileEntryId();
        result &= "file0".equals(queryViewModel.getQueries().get(0).getViewModelPosts().get(0).getViewModelAttachments().get(0).getFilename());
        result &= "Response".equals(queryViewModel.getQueries().get(0).getViewModelPosts().get(1).getPostBody());
        result &= applicantFinanceContactUserId == queryViewModel.getQueries().get(0).getViewModelPosts().get(1).getUserId();
        result &= "B Z - Org1".equals(queryViewModel.getQueries().get(0).getViewModelPosts().get(1).getUsername());
        result &= LocalDateTime.now().plusMinutes(20L).isAfter(queryViewModel.getQueries().get(0).getViewModelPosts().get(1).getCreatedOn());
        result &= 0 == queryViewModel.getQueries().get(0).getViewModelPosts().get(1).getViewModelAttachments().size();
        result &= "Query2 title".equals(queryViewModel.getQueries().get(1).getTitle());
        result &= FinanceChecksSectionType.ELIGIBILITY.equals(queryViewModel.getQueries().get(1).getSectionType());
        result &= true == (queryViewModel.getQueries().get(1).isAwaitingResponse());
        result &= applicantOrganisationId.equals(queryViewModel.getQueries().get(1).getOrganisationId());
        result &= projectId.equals(queryViewModel.getQueries().get(1).getProjectId());
        result &= 3L == queryViewModel.getQueries().get(1).getId();
        result &= 1 == queryViewModel.getQueries().get(1).getViewModelPosts().size();
        result &= "Question2".equals(queryViewModel.getQueries().get(1).getViewModelPosts().get(0).getPostBody());
        result &= financeTeamUserId.equals(queryViewModel.getQueries().get(1).getViewModelPosts().get(0).getUserId());
        result &= "A Z - Innovate (Finance team)".equals(queryViewModel.getQueries().get(1).getViewModelPosts().get(0).getUsername());
        result &= LocalDateTime.now().plusMinutes(10L).isAfter(queryViewModel.getQueries().get(1).getViewModelPosts().get(0).getCreatedOn());
        result &= 0 == queryViewModel.getQueries().get(1).getViewModelPosts().get(0).getViewModelAttachments().size();

        return result;
    }

    @Override
    protected FinanceChecksQueriesController supplyControllerUnderTest() {
        return new FinanceChecksQueriesController();
    }
}
