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
import org.mockito.cglib.core.Local;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.error.CommonErrors.unsupportedMediaTypeError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_ATOM_XML;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        when(projectService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(false);

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents")).
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
        Long userId = 1L;

        ProjectResource project = newProjectResource().withId(projectId).build();
        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(false);
        when(projectService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(false);

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents")).
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
        long userId = 1L;

        ProjectResource project = newProjectResource().withId(projectId).build();
        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        FileEntryResource existingCollaborationAgreement = newFileEntryResource().build();
        FileEntryResource existingExplotationPlan = newFileEntryResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.of(existingCollaborationAgreement));
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.of(existingExplotationPlan));
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(true);
        when(projectService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(false);

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents")).
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


    @Test
    public void testViewOtherDocumentsPageWithSubmittedDocuments() throws Exception {

        long projectId = 123L;

        ProjectResource project = newProjectResource().withId(projectId)
                .withDocumentsSubmittedDate(LocalDateTime.now()).build();
        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(true);
        when(projectService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(false);

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents")).
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
        assertTrue(model.isLeadPartner());
        assertFalse(model.isShowLeadPartnerGuidanceInformation());
        assertFalse(model.isShowApprovedMessage());
        assertTrue(model.isShowDocumentsBeingReviewedMessage());
        assertFalse(model.isShowRejectionMessages());
        assertFalse(model.isShowSubmitDocumentsButton());

        // test the form for the file uploads
        assertNull(form.getCollaborationAgreement());
        assertNull(form.getExploitationPlan());
    }


    @Test
    public void testDownloadCollaborationAgreement() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(projectService.getCollaborationAgreementFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(projectService.getCollaborationAgreementFileDetails(123L)).
                thenReturn(Optional.of(fileDetails));

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents/collaboration-agreement")).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("My content!", result.getResponse().getContentAsString());
        assertEquals("inline; filename=\"" + fileDetails.getName() + "\"",
                result.getResponse().getHeader("Content-Disposition"));
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
    public void testUploadCollaborationAgreement() throws Exception {

        FileEntryResource createdFileDetails = newFileEntryResource().withName("A name").build();

        MockMultipartFile uploadedFile = new MockMultipartFile("collaborationAgreement", "filename.txt", "text/plain", "My content!".getBytes());

        when(projectService.addCollaborationAgreementDocument(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceSuccess(createdFileDetails));

        mockMvc.perform(
                fileUpload("/project/123/partner/documents").
                        file(uploadedFile).
                        param("uploadCollaborationAgreementClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/partner/documents"));
    }


    @Test
    public void testUploadCollaborationAgreementButApiErrorsOccur() throws Exception {

        long projectId = 123L;
        long userId = 1L;
        ProjectResource project = newProjectResource().withId(projectId).build();

        MockMultipartFile uploadedFile = new MockMultipartFile("collaborationAgreement", "filename.txt", "text/plain", "My content!".getBytes());

        when(projectService.addCollaborationAgreementDocument(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceFailure(asList(
                        unsupportedMediaTypeError(singletonList(APPLICATION_ATOM_XML)),
                        unsupportedMediaTypeError(singletonList(APPLICATION_JSON)))));


        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(emptyList());
        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(true);
        when(projectService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(false);

        MvcResult result = mockMvc.perform(
                fileUpload("/project/123/partner/documents").
                        file(uploadedFile).
                        param("uploadCollaborationAgreementClicked", "")).
                andExpect(status().isOk()).
                andExpect(view().name("project/other-documents")).
                andReturn();

        ProjectOtherDocumentsForm form = (ProjectOtherDocumentsForm) result.getModelAndView().getModel().get("form");
        assertEquals(2, form.getObjectErrors().size());
        assertEquals(form.getObjectErrors(), form.getBindingResult().getFieldErrors("collaborationAgreement"));

    }

    @Test
    public void testRemoveCollaborationAgreement() throws Exception {

        when(projectService.removeCollaborationAgreementDocument(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/123/partner/documents").
                        param("removeCollaborationAgreementClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/partner/documents"));

        verify(projectService).removeCollaborationAgreementDocument(123L);
    }

    @Test
    public void testDownloadExploitationPlan() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(projectService.getExploitationPlanFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(projectService.getExploitationPlanFileDetails(123L)).
                thenReturn(Optional.of(fileDetails));

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents/exploitation-plan")).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("My content!", result.getResponse().getContentAsString());
        assertEquals("inline; filename=\"" + fileDetails.getName() + "\"",
                result.getResponse().getHeader("Content-Disposition"));
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

    @Test
    public void testUploadExploitationPlan() throws Exception {

        FileEntryResource createdFileDetails = newFileEntryResource().withName("A name").build();

        MockMultipartFile uploadedFile = new MockMultipartFile("exploitationPlan", "filename.txt", "text/plain", "My content!".getBytes());

        when(projectService.addExploitationPlanDocument(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceSuccess(createdFileDetails));

        mockMvc.perform(
                fileUpload("/project/123/partner/documents").
                        file(uploadedFile).
                        param("uploadExploitationPlanClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/partner/documents"));
    }

    @Test
    public void testRemoveExploitationPlan() throws Exception {

        when(projectService.removeExploitationPlanDocument(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/123/partner/documents").
                        param("removeExploitationPlanClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/partner/documents"));

        verify(projectService).removeExploitationPlanDocument(123L);
    }

    @Test
    public void testOtherDocumentsSubmitAllowedWhenAllFilesUploaded() throws Exception {
        long projectId = 123L;
        long userId = 1L;
        ProjectResource project = newProjectResource().withId(projectId).build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(emptyList());
        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(true);
        when(projectService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(true);

        MvcResult result = mockMvc.perform(
                get("/project/123/partner/documents")).
                andExpect(status().isOk()).
                andExpect(view().name("project/other-documents")).
                andReturn();

        verify(projectService).isOtherDocumentSubmitAllowed(123L);

        ProjectOtherDocumentsViewModel model = (ProjectOtherDocumentsViewModel) result.getModelAndView().getModel().get("model");
        ProjectOtherDocumentsForm form = (ProjectOtherDocumentsForm) result.getModelAndView().getModel().get("form");

        // test flags that help to drive the page
        assertFalse(model.isReadOnly());
        assertTrue(model.isEditable());
        assertTrue(model.isLeadPartner());
        assertTrue(model.isShowLeadPartnerGuidanceInformation());
        assertFalse(model.isShowApprovedMessage());
        assertFalse(model.isShowDocumentsBeingReviewedMessage());
        assertFalse(model.isShowRejectionMessages());
        assertTrue(model.isShowSubmitDocumentsButton());
        assertTrue(model.isSubmitAllowed());

        // test the form for the file uploads
        assertNull(form.getCollaborationAgreement());
        assertNull(form.getExploitationPlan());
    }

    @Test
    public void testSubmitPartnerDocuments() throws Exception {
        when(projectService.setPartnerDocumentsSubmitted(1L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/partner/documents/submit", 1L)).
                andExpect(redirectedUrl("/project/1/partner/documents"));
    }

    @Test
    public void testViewConfirmDocuemntsPage() throws Exception {

        long projectId = 123L;
        long userId = 1L;
        ProjectResource project = newProjectResource().withId(projectId).build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(emptyList());
        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(true);
        when(projectService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(true);

        mockMvc.perform(get("/project/{id}/partner/documents/confirm", projectId)).
                andExpect(status().isOk()).
                andExpect(view().name("project/other-documents-confirm")).
                andReturn();
    }

    @Override
    protected ProjectOtherDocumentsController supplyControllerUnderTest() {
        return new ProjectOtherDocumentsController();
    }
}
