package org.innovateuk.ifs.project.queries;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;

import org.innovateuk.ifs.project.queries.controller.FinanceChecksQueriesController;
import org.innovateuk.ifs.project.queries.viewmodel.FinanceChecksQueriesViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class FinanceChecksQueriesControllerTest extends BaseControllerMockMVCTest<FinanceChecksQueriesController> {

    @Test
    public void testView() throws Exception {
        Long projectId = 123L;
        Long organisationId = 789L;

        ApplicationResource applicationResource = newApplicationResource().build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withName("Project1").withApplication(applicationResource).build();

        OrganisationResource organisationResource = newOrganisationResource().withName("Org1").withId(organisationId).build();

        OrganisationResource leadOrganisationResource = newOrganisationResource().withId(organisationId).build();

        ProjectUserResource projectUser = newProjectUserResource().withOrganisation(organisationId).withUserName("User1").withEmail("e@mail.com").withPhoneNumber("0117").withRoleName(UserRoleType.FINANCE_CONTACT).build();

        // load query model
        UserResource user = newUserResource().withFirstName("A").withLastName("Z").build();
        UserResource user2 = newUserResource().withFirstName("B").withLastName("Z").build();

        // populate viewmodel
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(organisationService.getOrganisationById(organisationId)).thenReturn(organisationResource);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisationResource);
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(Arrays.asList(projectUser));

        //load query model
        when(userService.findById(18L)).thenReturn(user);
        when(organisationService.getOrganisationForUser(18L)).thenReturn(organisationResource);
        when(userService.findById(55L)).thenReturn(user2);
        when(organisationService.getOrganisationForUser(55L)).thenReturn(organisationResource);

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/finance-check/organisation/" + organisationId +"/query?query_section=Eligibility")).
                andExpect(view().name("project/financecheck/queries")).
                andReturn();

        FinanceChecksQueriesViewModel queryViewModel = (FinanceChecksQueriesViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Eligibility", queryViewModel.getQuerySection());
        assertEquals("e@mail.com", queryViewModel.getFinanceContactEmail());
        assertEquals("User1", queryViewModel.getFinanceContactName());
        assertEquals("0117", queryViewModel.getFinanceContactPhoneNumber());
        assertEquals(0, queryViewModel.getNewAttachmentLinks().size());
        assertEquals("Org1", queryViewModel.getOrganisationName());
        assertEquals("Project1", queryViewModel.getProjectName());
        assertEquals(organisationId, queryViewModel.getOrganisationId());
        assertEquals(projectId, queryViewModel.getProjectId());
        assertEquals(null, queryViewModel.getNewPostQueryId());
        assertEquals(2, queryViewModel.getQueries().size());

        //FinanceChecksQueriesForm form = (FinanceChecksQueriesForm) result.getModelAndView().getModel().get("form");
        //assertEquals(form.getAnnex(), null);
    }

    @Override
    protected FinanceChecksQueriesController supplyControllerUnderTest() {
        return new FinanceChecksQueriesController();
    }
}
