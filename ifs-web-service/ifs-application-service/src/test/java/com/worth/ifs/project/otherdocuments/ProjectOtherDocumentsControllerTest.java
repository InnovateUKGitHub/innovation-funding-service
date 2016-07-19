package com.worth.ifs.project.otherdocuments;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.otherdocuments.controller.ProjectOtherDocumentsController;
import com.worth.ifs.project.otherdocuments.form.ProjectOtherDocumentsForm;
import com.worth.ifs.project.otherdocuments.viewmodel.ProjectOtherDocumentsViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectOtherDocumentsControllerTest extends BaseControllerMockMVCTest<ProjectOtherDocumentsController> {

    @Test
    public void testViewOtherDocumentsPage() throws Exception {

        long projectId = 123L;

        ProjectResource project = newProjectResource().withId(projectId).build();
        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(true);

        MvcResult result = mockMvc.perform(get("/project/123/other-documents")).
                andExpect(view().name("project/other-documents")).
                andReturn();

        ProjectOtherDocumentsViewModel model = (ProjectOtherDocumentsViewModel) result.getModelAndView().getModel().get("model");
        ProjectOtherDocumentsForm form = (ProjectOtherDocumentsForm) result.getModelAndView().getModel().get("form");

        // test the view model
        assertEquals(project.getId(), model.getProjectId());
        assertEquals(project.getName(), model.getProjectName());
        assertNull(model.getCollaborationAgreementFileDetails());
        assertNull(model.getExploitationPlanFileDetails());
        assertEquals(emptyList(), model.getRejectionReasons());
        assertEquals(simpleMap(partnerOrganisations, OrganisationResource::getName),
                model.getPartnerOrganisationNames());

        // test flags that help to drive the page
        assertFalse(model.isReadOnly());
        assertTrue(model.isEditable());
        assertTrue(model.isLeadPartner());
        assertTrue(model.isShowLeadPartnerGuidanceInformation());
        assertFalse(model.isShowApprovedMessage());
        assertFalse(model.isShowDocumentsBeingReviewedMessage());
        assertFalse(model.isShowRejectionMessages());
        assertTrue(model.isShowSubmitDocumentsButton());

        // test the form for the file uploads
        assertNull(form.getCollaborationAgreement());
        assertNull(form.getExploitationPlan());
    }

    @Test
    public void testViewOtherDocumentsPageAsPartner() throws Exception {

        long projectId = 123L;

        ProjectResource project = newProjectResource().withId(projectId).build();
        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(false);

        MvcResult result = mockMvc.perform(get("/project/123/other-documents")).
                andExpect(view().name("project/other-documents")).
                andReturn();

        ProjectOtherDocumentsViewModel model = (ProjectOtherDocumentsViewModel) result.getModelAndView().getModel().get("model");
        ProjectOtherDocumentsForm form = (ProjectOtherDocumentsForm) result.getModelAndView().getModel().get("form");

        // test the view model
        assertEquals(project.getId(), model.getProjectId());
        assertEquals(project.getName(), model.getProjectName());
        assertNull(model.getCollaborationAgreementFileDetails());
        assertNull(model.getExploitationPlanFileDetails());
        assertEquals(emptyList(), model.getRejectionReasons());
        assertEquals(simpleMap(partnerOrganisations, OrganisationResource::getName),
                model.getPartnerOrganisationNames());

        // test flags that help to drive the page
        assertTrue(model.isReadOnly());
        assertFalse(model.isEditable());
        assertFalse(model.isLeadPartner());
        assertFalse(model.isShowLeadPartnerGuidanceInformation());
        assertFalse(model.isShowApprovedMessage());
        assertFalse(model.isShowDocumentsBeingReviewedMessage());
        assertFalse(model.isShowRejectionMessages());
        assertFalse(model.isShowSubmitDocumentsButton());

        // test the form for the file uploads
        assertNull(form.getCollaborationAgreement());
        assertNull(form.getExploitationPlan());
    }

    @Test
    public void testViewOtherDocumentsPageWithExistingDocuments() throws Exception {

        long projectId = 123L;

        ProjectResource project = newProjectResource().withId(projectId).build();
        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        FileEntryResource existingCollaborationAgreement = newFileEntryResource().build();
        FileEntryResource existingExplotationPlan = newFileEntryResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.of(existingCollaborationAgreement));
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.of(existingExplotationPlan));
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(true);

        MvcResult result = mockMvc.perform(get("/project/123/other-documents")).
                andExpect(view().name("project/other-documents")).
                andReturn();

        ProjectOtherDocumentsViewModel model = (ProjectOtherDocumentsViewModel) result.getModelAndView().getModel().get("model");
        ProjectOtherDocumentsForm form = (ProjectOtherDocumentsForm) result.getModelAndView().getModel().get("form");

        FileDetailsViewModel expectedCollaborationAgreement = new FileDetailsViewModel(existingCollaborationAgreement);
        FileDetailsViewModel expectedExploitationPlan = new FileDetailsViewModel(existingExplotationPlan);

        // test the view model
        assertEquals(project.getId(), model.getProjectId());
        assertEquals(project.getName(), model.getProjectName());
        assertEquals(expectedCollaborationAgreement, model.getCollaborationAgreementFileDetails());
        assertEquals(expectedExploitationPlan, model.getExploitationPlanFileDetails());
        assertEquals(emptyList(), model.getRejectionReasons());
        assertEquals(simpleMap(partnerOrganisations, OrganisationResource::getName),
                model.getPartnerOrganisationNames());

        // test flags that help to drive the page
        assertFalse(model.isReadOnly());
        assertTrue(model.isEditable());
        assertTrue(model.isLeadPartner());
        assertTrue(model.isShowLeadPartnerGuidanceInformation());
        assertFalse(model.isShowApprovedMessage());
        assertFalse(model.isShowDocumentsBeingReviewedMessage());
        assertFalse(model.isShowRejectionMessages());
        assertTrue(model.isShowSubmitDocumentsButton());

        // test the form for the file uploads
        assertNull(form.getCollaborationAgreement());
        assertNull(form.getExploitationPlan());
    }

    @Override
    protected ProjectOtherDocumentsController supplyControllerUnderTest() {
        return new ProjectOtherDocumentsController();
    }
}
