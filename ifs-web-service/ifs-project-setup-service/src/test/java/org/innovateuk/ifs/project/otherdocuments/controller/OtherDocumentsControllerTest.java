package org.innovateuk.ifs.project.otherdocuments.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.otherdocuments.form.OtherDocumentsForm;
import org.innovateuk.ifs.project.otherdocuments.populator.OtherDocumentsViewModelPopulator;
import org.innovateuk.ifs.project.otherdocuments.viewmodel.OtherDocumentsViewModel;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.FALSE;
import static java.lang.String.format;
import static org.innovateuk.ifs.commons.error.CommonErrors.unsupportedMediaTypeError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
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

public class OtherDocumentsControllerTest extends BaseControllerMockMVCTest<OtherDocumentsController> {

    @Spy
    @InjectMocks
    OtherDocumentsViewModelPopulator populator;

    @Test
    public void testViewOtherDocumentsPage() throws Exception {

        long projectId = 123L;

        ProjectResource project = newProjectResource().withId(projectId).build();
        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        when(projectService.getById(projectId)).thenReturn(project);
        when(otherDocumentsService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(otherDocumentsService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(otherDocumentsService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(false);

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents")).
                andExpect(view().name("project/other-documents")).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andReturn();

        OtherDocumentsViewModel model = (OtherDocumentsViewModel) result.getModelAndView().getModel().get("model");
        OtherDocumentsForm form = (OtherDocumentsForm) result.getModelAndView().getModel().get("form");

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
        assertTrue(model.isShowGuidanceInformation());
        assertFalse(model.isShowApprovedMessage());
        assertFalse(model.isShowDocumentsBeingReviewedMessage());
        assertFalse(model.isShowRejectionMessages());
        assertFalse(model.isSubmitAllowed());
        assertFalse(model.isShowSubmitDocumentsButton());
        assertFalse(model.isShowDisabledSubmitDocumentsButton());

        // test the form for the file uploads
        assertNull(form.getCollaborationAgreement());
        assertNull(form.getExploitationPlan());
    }

    @Test
    public void testViewOtherDocumentsPageReadOnly() throws Exception {

        long projectId = 123L;

        ProjectResource project = newProjectResource()
                .withDocumentsSubmittedDate(ZonedDateTime.now())
                .withOtherDocumentsApproved(ApprovalType.APPROVED)
                .withId(projectId)
                .build();
        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        when(projectService.getById(projectId)).thenReturn(project);
        when(otherDocumentsService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(otherDocumentsService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(otherDocumentsService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(false);

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents/readonly")).
                andExpect(view().name("project/other-documents")).
                andExpect(model().attributeExists("readOnlyView")).
                andExpect(model().attribute("readOnlyView", true)).
                andReturn();

        OtherDocumentsViewModel model = (OtherDocumentsViewModel) result.getModelAndView().getModel().get("model");
        Boolean readOnlyView = (Boolean) result.getModelAndView().getModel().get("readOnlyView");

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
        assertFalse(model.isShowGuidanceInformation());
        assertTrue(model.isShowApprovedMessage());
        assertFalse(model.isShowDocumentsBeingReviewedMessage());
        assertFalse(model.isShowRejectionMessages());
        assertFalse(model.isSubmitAllowed());
        assertFalse(model.isShowSubmitDocumentsButton());
        assertFalse(model.isShowDisabledSubmitDocumentsButton());

        assertTrue(readOnlyView);
    }

    @Test
    public void testViewOtherDocumentsPageReadOnly_asProjectManager() throws Exception {

        long projectId = 123L;

        ProjectResource project = newProjectResource()
                .withDocumentsSubmittedDate(ZonedDateTime.now())
                .withOtherDocumentsApproved(ApprovalType.APPROVED)
                .withId(projectId)
                .build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.isProjectManager(loggedInUser.getId(), projectId)).thenReturn(TRUE);

        mockMvc.perform(get(format("/project/%s/partner/documents/readonly", projectId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/project/%s/partner/documents", projectId)));
    }

        @Test
    public void testViewOtherDocumentsPageAsPartner() throws Exception {

        long projectId = 123L;

        ProjectResource project = newProjectResource().withId(projectId).build();
        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        when(projectService.getById(projectId)).thenReturn(project);
        when(otherDocumentsService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(otherDocumentsService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(otherDocumentsService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(false);

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents")).
                andExpect(view().name("project/other-documents")).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andReturn();

        OtherDocumentsViewModel model = (OtherDocumentsViewModel) result.getModelAndView().getModel().get("model");
        OtherDocumentsForm form = (OtherDocumentsForm) result.getModelAndView().getModel().get("form");

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
        assertTrue(model.isShowGuidanceInformation());
        assertFalse(model.isShowApprovedMessage());
        assertFalse(model.isShowDocumentsBeingReviewedMessage());
        assertFalse(model.isShowRejectionMessages());
        assertFalse(model.isSubmitAllowed());
        assertFalse(model.isShowSubmitDocumentsButton());
        assertFalse(model.isShowDisabledSubmitDocumentsButton());

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
        when(otherDocumentsService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.of(existingCollaborationAgreement));
        when(otherDocumentsService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.of(existingExplotationPlan));
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(otherDocumentsService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(false);

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents")).
                andExpect(view().name("project/other-documents")).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andReturn();

        OtherDocumentsViewModel model = (OtherDocumentsViewModel) result.getModelAndView().getModel().get("model");
        OtherDocumentsForm form = (OtherDocumentsForm) result.getModelAndView().getModel().get("form");

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
        assertTrue(model.isReadOnly());
        assertFalse(model.isEditable());
        assertTrue(model.isShowGuidanceInformation());
        assertFalse(model.isShowApprovedMessage());
        assertFalse(model.isShowDocumentsBeingReviewedMessage());
        assertFalse(model.isShowRejectionMessages());
        assertFalse(model.isSubmitAllowed());
        assertFalse(model.isShowSubmitDocumentsButton());
        assertFalse(model.isShowDisabledSubmitDocumentsButton());

        // test the form for the file uploads
        assertNull(form.getCollaborationAgreement());
        assertNull(form.getExploitationPlan());
    }


    @Test
    public void testViewOtherDocumentsPageWithSubmittedDocuments() throws Exception {

        long projectId = 123L;

        ProjectResource project = newProjectResource().withId(projectId)
                .withDocumentsSubmittedDate(ZonedDateTime.now()).withOtherDocumentsApproved(ApprovalType.UNSET).build();
        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        when(projectService.getById(projectId)).thenReturn(project);
        when(otherDocumentsService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(otherDocumentsService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(otherDocumentsService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(false);

        MvcResult result = mockMvc.perform(get("/project/123/partner/documents")).
                andExpect(view().name("project/other-documents")).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andReturn();

        OtherDocumentsViewModel model = (OtherDocumentsViewModel) result.getModelAndView().getModel().get("model");
        OtherDocumentsForm form = (OtherDocumentsForm) result.getModelAndView().getModel().get("form");

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
        assertFalse(model.isShowGuidanceInformation());
        assertFalse(model.isShowApprovedMessage());
        assertTrue(model.isShowDocumentsBeingReviewedMessage());
        assertFalse(model.isShowRejectionMessages());
        assertFalse(model.isSubmitAllowed());
        assertFalse(model.isShowSubmitDocumentsButton());
        assertFalse(model.isShowDisabledSubmitDocumentsButton());

        // test the form for the file uploads
        assertNull(form.getCollaborationAgreement());
        assertNull(form.getExploitationPlan());
    }


    @Test
    public void testDownloadCollaborationAgreement() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(otherDocumentsService.getCollaborationAgreementFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(otherDocumentsService.getCollaborationAgreementFileDetails(123L)).
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

        when(otherDocumentsService.getExploitationPlanFile(123L)).
                thenReturn(Optional.empty());

        when(otherDocumentsService.getExploitationPlanFileDetails(123L)).
                thenReturn(Optional.empty());

        mockMvc.perform(get("/project/123/partner/documents/exploitation-plan")).
                andExpect(status().isNotFound()).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andExpect(view().name("404"));
    }

    @Test
    public void testUploadCollaborationAgreement() throws Exception {

        FileEntryResource createdFileDetails = newFileEntryResource().withName("A name").build();

        MockMultipartFile uploadedFile = new MockMultipartFile("collaborationAgreement", "filename.txt", "text/plain", "My content!".getBytes());

        when(otherDocumentsService.addCollaborationAgreementDocument(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceSuccess(createdFileDetails));

        mockMvc.perform(
                fileUpload("/project/123/partner/documents").
                        file(uploadedFile).
                        param("uploadCollaborationAgreementClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andExpect(view().name("redirect:/project/123/partner/documents"));
    }


    @Test
    public void testUploadCollaborationAgreementButApiErrorsOccur() throws Exception {

        long projectId = 123L;

        ProjectResource project = newProjectResource().withId(projectId).build();

        OrganisationResource partnerOrganisation = newOrganisationResource().build();
        OrganisationResource leadOrganisation = newOrganisationResource().build();

        MockMultipartFile uploadedFile = new MockMultipartFile("collaborationAgreement", "filename.txt", "text/plain", "My content!".getBytes());

        when(otherDocumentsService.addCollaborationAgreementDocument(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceFailure(asList(
                        unsupportedMediaTypeError(singletonList(APPLICATION_ATOM_XML)),
                        unsupportedMediaTypeError(singletonList(APPLICATION_JSON)))));


        when(projectService.getById(projectId)).thenReturn(project);
        when(otherDocumentsService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(otherDocumentsService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(asList(partnerOrganisation, leadOrganisation));
        when(otherDocumentsService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(false);

        MvcResult result = mockMvc.perform(
                fileUpload("/project/123/partner/documents").
                        file(uploadedFile).
                        param("uploadCollaborationAgreementClicked", "")).
                andExpect(status().isOk()).
                andExpect(view().name("project/other-documents")).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andReturn();

        OtherDocumentsForm form = (OtherDocumentsForm) result.getModelAndView().getModel().get("form");
        assertEquals(2, form.getObjectErrors().size());
        assertEquals(form.getObjectErrors(), form.getBindingResult().getFieldErrors("collaborationAgreement"));

    }

    @Test
    public void testRemoveCollaborationAgreement() throws Exception {

        when(otherDocumentsService.removeCollaborationAgreementDocument(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/123/partner/documents").
                        param("removeCollaborationAgreementClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andExpect(view().name("redirect:/project/123/partner/documents"));

        verify(otherDocumentsService).removeCollaborationAgreementDocument(123L);
    }

    @Test
    public void testDownloadExploitationPlan() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(otherDocumentsService.getExploitationPlanFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(otherDocumentsService.getExploitationPlanFileDetails(123L)).
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

        when(otherDocumentsService.getExploitationPlanFile(123L)).
                thenReturn(Optional.empty());

        when(otherDocumentsService.getExploitationPlanFileDetails(123L)).
                thenReturn(Optional.empty());

        mockMvc.perform(get("/project/123/partner/documents/exploitation-plan")).
                andExpect(status().isNotFound()).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andExpect(view().name("404"));
    }

    @Test
    public void testUploadExploitationPlan() throws Exception {

        FileEntryResource createdFileDetails = newFileEntryResource().withName("A name").build();

        MockMultipartFile uploadedFile = new MockMultipartFile("exploitationPlan", "filename.txt", "text/plain", "My content!".getBytes());

        when(otherDocumentsService.addExploitationPlanDocument(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceSuccess(createdFileDetails));

        mockMvc.perform(
                fileUpload("/project/123/partner/documents").
                        file(uploadedFile).
                        param("uploadExploitationPlanClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andExpect(view().name("redirect:/project/123/partner/documents"));
    }

    @Test
    public void testRemoveExploitationPlan() throws Exception {

        when(otherDocumentsService.removeExploitationPlanDocument(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/123/partner/documents").
                        param("removeExploitationPlanClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/partner/documents"));

        verify(otherDocumentsService).removeExploitationPlanDocument(123L);
    }

    @Test
    public void testOtherDocumentsSubmitAllowedWhenAllFilesUploaded() throws Exception {
        long projectId = 123L;
        ProjectResource project = newProjectResource().withId(projectId).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().build();
        OrganisationResource leadOrganisation = newOrganisationResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.isProjectManager(loggedInUser.getId(), projectId)).thenReturn(true);
        when(otherDocumentsService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(otherDocumentsService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(asList(partnerOrganisation, leadOrganisation));
        when(otherDocumentsService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(true);

        MvcResult result = mockMvc.perform(
                get("/project/123/partner/documents")).
                andExpect(status().isOk()).
                andExpect(view().name("project/other-documents")).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andReturn();

        verify(otherDocumentsService).isOtherDocumentSubmitAllowed(123L);

        OtherDocumentsViewModel model = (OtherDocumentsViewModel) result.getModelAndView().getModel().get("model");
        OtherDocumentsForm form = (OtherDocumentsForm) result.getModelAndView().getModel().get("form");

        // test flags that help to drive the page
        assertFalse(model.isReadOnly());
        assertTrue(model.isEditable());
        assertTrue(model.isShowGuidanceInformation());
        assertFalse(model.isShowApprovedMessage());
        assertFalse(model.isShowDocumentsBeingReviewedMessage());
        assertFalse(model.isShowRejectionMessages());
        assertTrue(model.isShowSubmitDocumentsButton());
        assertTrue(model.isSubmitAllowed());
        assertFalse(model.isShowDisabledSubmitDocumentsButton());

        // test the form for the file uploads
        assertNull(form.getCollaborationAgreement());
        assertNull(form.getExploitationPlan());
    }

    @Test
    public void testSubmitShownWhenFromLeadOrgAndProjectManager() throws Exception {
        long projectId = 123L;
        ProjectResource project = newProjectResource().withId(projectId).withOtherDocumentsApproved(ApprovalType.REJECTED).withDocumentsSubmittedDate(ZonedDateTime.now()).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().build();
        OrganisationResource leadOrganisation = newOrganisationResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.isProjectManager(loggedInUser.getId(), projectId)).thenReturn(true);
        when(otherDocumentsService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(otherDocumentsService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(asList(partnerOrganisation, leadOrganisation));
        when(otherDocumentsService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(true);

        MvcResult result = mockMvc.perform(
                get("/project/123/partner/documents")).
                andExpect(status().isOk()).
                andExpect(view().name("project/other-documents")).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andReturn();

        OtherDocumentsViewModel model = (OtherDocumentsViewModel) result.getModelAndView().getModel().get("model");
        assertTrue(model.isShowDisabledSubmitDocumentsButton());
    }

    @Test
    public void testSubmitNotShownWhenFromLeadOrgButNotProjectManager() throws Exception {
        long projectId = 123L;
        ProjectResource project = newProjectResource().withId(projectId).withOtherDocumentsApproved(ApprovalType.REJECTED).withDocumentsSubmittedDate(ZonedDateTime.now()).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().build();
        OrganisationResource leadOrganisation = newOrganisationResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.isProjectManager(loggedInUser.getId(), projectId)).thenReturn(false);
        when(otherDocumentsService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(otherDocumentsService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(asList(partnerOrganisation, leadOrganisation));
        when(otherDocumentsService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(true);

        MvcResult result = mockMvc.perform(
                get("/project/123/partner/documents")).
                andExpect(status().isOk()).
                andExpect(view().name("project/other-documents")).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andReturn();

        OtherDocumentsViewModel model = (OtherDocumentsViewModel) result.getModelAndView().getModel().get("model");
        assertFalse(model.isShowDisabledSubmitDocumentsButton());
    }

    @Test
    public void testSubmitPartnerDocuments() throws Exception {
        when(otherDocumentsService.setPartnerDocumentsSubmitted(1L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/partner/documents/submit", 1L)).
                andExpect(redirectedUrl("/project/1/partner/documents"));
    }

    @Test
    public void testSubmitPartnerDocuments_submitNotAllowed() throws Exception {
        when(otherDocumentsService.isOtherDocumentSubmitAllowed(1L)).thenReturn(FALSE);

        mockMvc.perform(post("/project/{id}/partner/documents/submit", 1L)).
                andExpect(redirectedUrl("/project/1/partner/documents"));
    }

    @Test
    public void testViewConfirmDocumentsPage() throws Exception {

        long projectId = 123L;

        ProjectResource project = newProjectResource().withId(projectId).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().build();
        OrganisationResource leadOrganisation = newOrganisationResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(otherDocumentsService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(otherDocumentsService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(asList(partnerOrganisation, leadOrganisation));
        when(otherDocumentsService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(true);

        mockMvc.perform(get("/project/{id}/partner/documents/confirm", projectId)).
                andExpect(status().isOk()).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andExpect(view().name("project/other-documents-confirm")).
                andReturn();
    }

    @Test
    public void testViewConfirmDocumentsPageSinglePartner() throws Exception {

        long projectId = 123L;

        ProjectResource project = newProjectResource().withId(projectId).build();
        OrganisationResource partnerOrganisation = newOrganisationResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(otherDocumentsService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(otherDocumentsService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(asList(partnerOrganisation));
        when(otherDocumentsService.isOtherDocumentSubmitAllowed(projectId)).thenReturn(true);

        mockMvc.perform(get("/project/{id}/partner/documents/confirm", projectId)).
                andExpect(status().isOk()).
                andExpect(model().attributeDoesNotExist("readOnlyView")).
                andExpect(view().name("project/other-documents-confirm")).
                andReturn();
    }

    @Override
    protected OtherDocumentsController supplyControllerUnderTest() {
        return new OtherDocumentsController();
    }
}
