package com.worth.ifs.project.otherdocuments.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.application.builder.ApplicationResourceBuilder;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.otherdocuments.viewmodel.ProjectPartnerDocumentsViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectOtherDocumentsControllerMockMvcTest extends BaseControllerMockMVCTest<ProjectOtherDocumentsController> {

    long projectId = 123L;

    OrganisationResource leadOrganisation = newOrganisationResource().withId(1L).withName("Test Lead Organisation").build();

    private void setupViewOtherDocumentsTestExpectations(ProjectResource project) {

        List<ProjectUserResource> projectUsers = newProjectUserResource().with(id(999L)).withUserName("Dave Smith").withPhoneNumber("01234123123")
                .withEmail("d@d.com")
                .withRoleName(PROJECT_MANAGER.getName()).build(1);

        List<OrganisationResource> partnerOrganisations = newOrganisationResource().withName("Org1", "Org2", "Org3").build(3);

        ApplicationResource applicationResource = ApplicationResourceBuilder.newApplicationResource()
                .withCompetition(1L)
                .build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisation);
        when(applicationService.getById(project.getApplication())).thenReturn(applicationResource);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
    }


    private void assertProjectDetailsPrepopulatedOk(ProjectPartnerDocumentsViewModel model) {

        assertEquals(Long.valueOf(123), model.getProjectId());
        assertEquals("My Project", model.getProjectName());
        assertEquals("Test Lead Organisation", model.getLeadPartnerOrganisationName());
        assertEquals("Dave Smith", model.getProjectManagerName());
        assertEquals("01234123123", model.getProjectManagerTelephone());
        assertEquals("d@d.com", model.getProjectManagerEmail());
        assertEquals(Long.valueOf(1L), model.getCompetitionId());

        List<String> testOrgList= new ArrayList<String>(Arrays.asList("Org1", "Org2", "Org3"));
        assertEquals(asList(testOrgList), asList(model.getPartnerOrganisationNames()));
    }

    @Test
    public void testViewOtherDocumentsPage() throws Exception {

        ProjectResource project = newProjectResource().withId(projectId).withName("My Project").build();

        setupViewOtherDocumentsTestExpectations (project);

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents")).
                andExpect(view().name("project/other-documents")).
                andReturn();

        ProjectPartnerDocumentsViewModel model = (ProjectPartnerDocumentsViewModel) result.getModelAndView().getModel().get("model");

        assertProjectDetailsPrepopulatedOk(model);

    }

    @Test
    public void testViewOtherDocumentsPageWithExistingDocuments() throws Exception {

        ProjectResource project = newProjectResource().withId(projectId).withName("My Project").build();

        setupViewOtherDocumentsTestExpectations (project);

        FileEntryResource existingCollaborationAgreement = newFileEntryResource().build();
        FileEntryResource existingExplotationPlan = newFileEntryResource().build();

        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.of(existingCollaborationAgreement));
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.of(existingExplotationPlan));

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents")).
                andExpect(view().name("project/other-documents")).
                andReturn();

        ProjectPartnerDocumentsViewModel model = (ProjectPartnerDocumentsViewModel) result.getModelAndView().getModel().get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);
   }

    @Test
    public void acceptOrRejectOtherDocuments() throws Exception {

        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("My Project")
                .withOtherDocumentsApproved(true)
                .build();
        boolean approved = true;

        setupViewOtherDocumentsTestExpectations(project);

        when(projectService.acceptOrRejectOtherDocuments(projectId, approved)).thenReturn(ServiceResult.serviceSuccess());

        MvcResult result = mockMvc.perform(post("/project/123/partner/documents")
                .param("approved", String.valueOf(approved)))
                .andExpect(view().name("project/other-documents"))
                .andReturn();

        ProjectPartnerDocumentsViewModel model = (ProjectPartnerDocumentsViewModel) result.getModelAndView().getModel().get("model");

        assertProjectDetailsPrepopulatedOk(model);
        assertEquals(true, model.isApproved());

    }

    @Test
    public void testDownloadCollaborationAgreementButFileDoesntExist() throws Exception {

        when(projectService.getExploitationPlanFile(123L)).
                thenReturn(Optional.empty());

        when(projectService.getExploitationPlanFileDetails(123L)).
                thenReturn(Optional.empty());

        mockMvc.perform(get("/project/123/partner/documents/exploitation-plan")).
                andExpect(status().isNotFound()).
                andExpect(view().name("404"));
    }


    @Test
    public void testDownloadExploitationPlanButFileDoesntExist() throws Exception {

        when(projectService.getExploitationPlanFile(123L)).
                thenReturn(Optional.empty());

        when(projectService.getExploitationPlanFileDetails(123L)).
                thenReturn(Optional.empty());

        mockMvc.perform(get("/project/123/partner/documents/exploitation-plan")).
                andExpect(status().isNotFound()).
                andExpect(view().name("404"));
    }

    @Override
    protected ProjectOtherDocumentsController supplyControllerUnderTest() {
        return new ProjectOtherDocumentsController();
    }
}