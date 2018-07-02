package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.file.builder.FileEntryResourceBuilder;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.grantofferletter.GrantOfferLetterService;
import org.innovateuk.ifs.project.grantofferletter.form.GrantOfferLetterLetterForm;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.GrantOfferLetterModel;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonErrors.unsupportedMediaTypeError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_UNABLE_TO_CREATE_FILE;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.PENDING;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource.stateInformationForNonPartnersView;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_ATOM_XML;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GrantOfferLetterControllerTest extends BaseControllerMockMVCTest<GrantOfferLetterController> {
    @Mock
    private ProjectService projectService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Mock
    private GrantOfferLetterService grantOfferLetterService;

    @Test
    public void testView() throws Exception {
        Long competitionId = 1L;
        Long projectId = 123L;
        Long applicationId = 789L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withApplication(applicationResource).build();

        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummaryResource));

        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferLetterState(projectId)).thenReturn(golState(PENDING));
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/grant-offer-letter/send")).
                andExpect(view().name("project/grant-offer-letter-send")).
                andReturn();

        GrantOfferLetterModel golViewModel = (GrantOfferLetterModel) result.getModelAndView().getModel().get("model");

        assertFalse(golViewModel.isSentToProjectTeam());
        assertEquals(null, golViewModel.getGrantOfferLetterFile());
        assertEquals(null, golViewModel.getAdditionalContractFile());
        assertEquals(null, golViewModel.getSignedGrantOfferLetterFile());
        assertFalse(golViewModel.getAdditionalContractFileContentAvailable());
        assertFalse(golViewModel.getGrantOfferLetterFileContentAvailable());
        assertFalse(golViewModel.getSignedGrantOfferLetterRejected());

        GrantOfferLetterLetterForm form = (GrantOfferLetterLetterForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getAnnex(), null);
    }

    @Test
    public void testSendGOLSuccess() throws Exception {
        Long projectId = 123L;

        when(grantOfferLetterService.sendGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/" + projectId + "/grant-offer-letter/send")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/grant-offer-letter/send"));

        verify(grantOfferLetterService).sendGrantOfferLetter(projectId);
    }

    @Test
    public void testSendGOLFailure() throws Exception {
        Long competitionId = 1L;
        Long projectId = 123L;
        Long applicationId = 789L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withApplication(applicationResource).build();

        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummaryResource));

        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferLetterState(projectId)).thenReturn(golState(PENDING));
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.empty());

        List<Error> errors = asList(notFoundError(String.class), notFoundError(Long.class));

        when(grantOfferLetterService.sendGrantOfferLetter(projectId)).thenReturn(serviceFailure(errors));

        // re-load model after sending GOL
        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummaryResource));

        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferLetterState(projectId)).thenReturn(golState(PENDING));
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/project/" + projectId + "/grant-offer-letter/send")).
                andExpect(view().name("project/grant-offer-letter-send")).
                andExpect(model().errorCount(errors.size())).
                andReturn();

        verify(grantOfferLetterService).sendGrantOfferLetter(projectId);
    }

    @Test
    public void testDownloadGOLFileEntryNotPresent() throws Exception {

        Long projectId = 1L;

        when(grantOfferLetterService.getGrantOfferFile(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/grant-offer-letter/grant-offer-letter"))
                .andExpect(status().isNoContent())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Test
    public void testDownloadGOLFileSuccess() throws Exception {

        Long projectId = 1L;

        FileEntryResource golFileEntryResource = FileEntryResourceBuilder.newFileEntryResource()
                .withName("gol-file.pdf")
                .build();
        byte[] content = "HelloWorld".getBytes();
        ByteArrayResource golByteArrayResource = new ByteArrayResource(content);

        when(grantOfferLetterService.getGrantOfferFile(projectId)).thenReturn(Optional.of(golByteArrayResource));
        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.of(golFileEntryResource));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/grant-offer-letter/grant-offer-letter"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals("HelloWorld", response.getContentAsString());
        assertEquals("inline; filename=\"gol-file.pdf\"", response.getHeader("Content-Disposition"));
        assertEquals(10, response.getContentLength());
    }

    @Test
    public void uploadGrantOfferLetterFile() throws Exception {

        Long projectId = 123L;

        FileEntryResource createdFileDetails = newFileEntryResource().withName("1").withMediaType("application/pdf").withFilesizeBytes(11).build();

        when(grantOfferLetterService.addGrantOfferLetter(123L, "application/pdf", 11, "grantOfferLetter.pdf", "My content!".getBytes())).
                thenReturn(serviceSuccess(createdFileDetails));

        MockMultipartFile uploadedFile = new MockMultipartFile("grantOfferLetter", "grantOfferLetter.pdf", "application/pdf", "My content!".getBytes());

        mockMvc.perform(
                fileUpload("/project/"+ projectId  + "/grant-offer-letter/grant-offer-letter").
                        file(uploadedFile).param("uploadGrantOfferLetterClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/grant-offer-letter/send"));

        verify(grantOfferLetterService).addGrantOfferLetter(123L, "application/pdf", 11, "grantOfferLetter.pdf", "My content!".getBytes());
    }

    @Test
    public void uploadGrantOfferLetterFileFails() throws Exception {

        Long projectId = 123L;
        Long competitionId = 1L;
        Long applicationId = 789L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withApplication(applicationResource).build();
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();

        // when the model is re-loaded after uploading
        when(projectService.getById(projectId)).thenReturn(projectResource);
        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummaryResource));

        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferLetterState(projectId)).thenReturn(golState(PENDING));
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.empty());

        MockMultipartFile uploadedFile = new MockMultipartFile("grantOfferLetter", "grantOfferLetter.txt", "text/plain", "My content!".getBytes());

        when(grantOfferLetterService.addGrantOfferLetter(123L, "text/plain", 11, "grantOfferLetter.txt", "My content!".getBytes())).
                thenReturn(serviceFailure(asList(
                        unsupportedMediaTypeError(singletonList(APPLICATION_ATOM_XML)),
                        unsupportedMediaTypeError(singletonList(APPLICATION_JSON)))));

        MvcResult result = mockMvc.perform(
                fileUpload("/project/" + projectId + "/grant-offer-letter/grant-offer-letter").
                        file(uploadedFile).
                        param("uploadGrantOfferLetterClicked", "")).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter-send")).
                andReturn();

        GrantOfferLetterLetterForm form = (GrantOfferLetterLetterForm) result.getModelAndView().getModel().get("form");
        assertEquals(2, form.getObjectErrors().size());
        assertEquals(form.getObjectErrors(), form.getBindingResult().getFieldErrors("grantOfferLetter"));
    }

    @Test
    public void removeGrantOfferLetterFile() throws Exception {

        Long projectId = 123L;

        when(grantOfferLetterService.removeGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        MockMultipartFile fileToDelete = new MockMultipartFile("grantOfferLetter", "grantOfferLetter.pdf", "application/pdf", "My content!".getBytes());

        mockMvc.perform(
                fileUpload("/project/"+ projectId  + "/grant-offer-letter/grant-offer-letter").
                        file(fileToDelete).param("removeGrantOfferLetterClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/grant-offer-letter/send"));

        verify(grantOfferLetterService).removeGrantOfferLetter(projectId);
    }

    @Test
    public void testDownloadAnnexFileEntryNotPresent() throws Exception {

        Long projectId = 1L;

        when(grantOfferLetterService.getAdditionalContractFile(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/grant-offer-letter/additional-contract"))
                .andExpect(status().isNoContent())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    @Test
    public void testDownloadAnnexFileSuccess() throws Exception {

        Long projectId = 1L;

        FileEntryResource annexFileEntryResource = FileEntryResourceBuilder.newFileEntryResource()
                .withName("annex-file.pdf")
                .build();
        byte[] content = "HelloWorld".getBytes();
        ByteArrayResource annexByteArrayResource = new ByteArrayResource(content);

        when(grantOfferLetterService.getAdditionalContractFile(projectId)).thenReturn(Optional.of(annexByteArrayResource));
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.of(annexFileEntryResource));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/grant-offer-letter/additional-contract"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals("HelloWorld", response.getContentAsString());
        assertEquals("inline; filename=\"annex-file.pdf\"", response.getHeader("Content-Disposition"));
        assertEquals(10, response.getContentLength());
    }

    @Test
    public void uploadAnnexFile() throws Exception {
        Long competitionId = 1L;
        Long projectId = 123L;
        Long applicationId = 789L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withApplication(applicationResource).build();
        Optional<FileEntryResource> annexFileEntryResource = Optional.of(FileEntryResourceBuilder.newFileEntryResource().withName("1").withMediaType("application/pdf").withFilesizeBytes(20).build());

        // when the model is re-loaded after uploading
        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummaryResource));

        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(annexFileEntryResource);
        when(grantOfferLetterService.getGrantOfferLetterState(projectId)).thenReturn(golState(PENDING));
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.empty());

        when(grantOfferLetterService.sendGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        MockMultipartFile uploadedFile = new MockMultipartFile("annex", "annex.pdf", "application/pdf", "My content!".getBytes());
        FileEntryResource createdFileDetails = newFileEntryResource().withName("1").withMediaType("application/pdf").withFilesizeBytes(20).build();

        when(grantOfferLetterService.addAdditionalContractFile(123L, "application/pdf", 11, "annex.pdf", "My content!".getBytes())).
                thenReturn(serviceSuccess(createdFileDetails));

        MvcResult result = mockMvc.perform(
                fileUpload("/project/"+ projectId  + "/grant-offer-letter/upload-annex").
                        file(uploadedFile).param("uploadAnnexClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/grant-offer-letter/send")).
                andReturn();

        GrantOfferLetterLetterForm form = (GrantOfferLetterLetterForm) result.getModelAndView().getModel().get("form");
        assertEquals(uploadedFile, form.getAnnex());
    }

    @Test
    public void uploadAnnexFileFails() throws Exception {
        Long competitionId = 1L;
        Long projectId = 123L;
        Long applicationId = 789L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withApplication(applicationResource).build();

        // when the model is re-loaded after uploading
        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(competitionSummaryResource));

        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferLetterState(projectId)).thenReturn(golState(PENDING));
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.empty());

        when(grantOfferLetterService.sendGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        MockMultipartFile uploadedFile = new MockMultipartFile("annex", "annex.pdf", "application/pdf", "My content!".getBytes());

        when(grantOfferLetterService.addAdditionalContractFile(123L, "application/pdf", 11, "annex.pdf", "My content!".getBytes())).
                thenReturn(ServiceResult.serviceFailure(new Error(FILES_UNABLE_TO_CREATE_FILE, INTERNAL_SERVER_ERROR)));

        MvcResult result = mockMvc.perform(
                fileUpload("/project/"+ projectId  + "/grant-offer-letter/upload-annex").
                        file(uploadedFile).param("uploadAnnexClicked", "")).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter-send")).
                andReturn();

        GrantOfferLetterLetterForm form = (GrantOfferLetterLetterForm) result.getModelAndView().getModel().get("form");
        assertEquals(uploadedFile, form.getAnnex());
        assertEquals(Boolean.FALSE, ((GrantOfferLetterModel)result.getModelAndView().getModel().get("model")).getAdditionalContractFileContentAvailable());
    }

    @Test
    public void signedGrantOfferLetterApprovalSuccess() throws Exception {
        Long projectId = 123L;

        GrantOfferLetterApprovalResource approvalResource = new GrantOfferLetterApprovalResource(ApprovalType.APPROVED, null);
        when(grantOfferLetterService.approveOrRejectSignedGrantOfferLetter(projectId,
                approvalResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/" + projectId + "/grant-offer-letter/signed")
                        .param("approvalType", "APPROVED")
                       ).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/grant-offer-letter/send")).
                andReturn();

        verify(grantOfferLetterService).approveOrRejectSignedGrantOfferLetter(projectId, approvalResource);
    }

    @Test
    public void signedGrantOfferLetterRejectionSuccess() throws Exception {

        Long projectId = 123L;

        String rejectionReason = "No signature";
        GrantOfferLetterApprovalResource approvalResource = new GrantOfferLetterApprovalResource(ApprovalType.REJECTED, rejectionReason);

        when(grantOfferLetterService.approveOrRejectSignedGrantOfferLetter(projectId, approvalResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/" + projectId + "/grant-offer-letter/signed")
                .param("approvalType", "REJECTED")
                .param("rejectionReason", rejectionReason)
        ).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/grant-offer-letter/send")).
                andReturn();

        verify(grantOfferLetterService).approveOrRejectSignedGrantOfferLetter(projectId, approvalResource);
    }

    @Test
    public void signedGrantOfferLetterWhenRejectedButRejectedReasonAllWhiteSpaces() throws Exception {

        Long projectId = 123L;

        mockMvc.perform(post("/project/" + projectId + "/grant-offer-letter/signed")
                .param("approvalType", "REJECTED")
                .param("rejectionReason", "       ")
        ).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/grant-offer-letter/send")).
                andReturn();

        verify(grantOfferLetterService, never()).approveOrRejectSignedGrantOfferLetter(any(), any());
    }

    @Test
    public void signedGrantOfferLetterWhenRejectedButRejectedReasonEmpty() throws Exception {

        Long projectId = 123L;

        mockMvc.perform(post("/project/" + projectId + "/grant-offer-letter/signed")
                .param("approvalType", "REJECTED")
                .param("rejectionReason", "")
        ).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/grant-offer-letter/send")).
                andReturn();

        verify(grantOfferLetterService, never()).approveOrRejectSignedGrantOfferLetter(any(), any());
    }

    @Test
    public void signedGrantOfferLetterWhenRejectedButNoRejectedReason() throws Exception {

        Long projectId = 123L;

        mockMvc.perform(post("/project/" + projectId + "/grant-offer-letter/signed")
                .param("approvalType", "REJECTED")
        ).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/grant-offer-letter/send")).
                andReturn();

        verify(grantOfferLetterService, never()).approveOrRejectSignedGrantOfferLetter(any(), any());
    }

    @Test
    public void signedGrantOfferLetterWhenNeitherApprovedNorRejected() throws Exception {

        Long projectId = 123L;

        mockMvc.perform(post("/project/" + projectId + "/grant-offer-letter/signed")
                        ).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/grant-offer-letter/send")).
                andReturn();

        verify(grantOfferLetterService, never()).approveOrRejectSignedGrantOfferLetter(any(), any());
    }

    @Test
    public void testDownloadSignedGrantOfferLetterSuccess() throws Exception {

        Long projectId = 1L;

        FileEntryResource annexFileEntryResource = FileEntryResourceBuilder.newFileEntryResource()
                .withName("annex-file.pdf")
                .build();
        byte[] content = "HelloWorld".getBytes();
        ByteArrayResource annexByteArrayResource = new ByteArrayResource(content);

        when(grantOfferLetterService.getSignedGrantOfferLetterFile(projectId)).thenReturn(Optional.of(annexByteArrayResource));
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.of(annexFileEntryResource));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/grant-offer-letter/signed-grant-offer-letter"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals("HelloWorld", response.getContentAsString());
        assertEquals("inline; filename=\"annex-file.pdf\"", response.getHeader("Content-Disposition"));
        assertEquals(10, response.getContentLength());
    }

    @Test
    public void testDownloadSignedGrantOfferLetterFileEntryNotPresent() throws Exception {

        Long projectId = 1L;

        when(grantOfferLetterService.getSignedGrantOfferLetterFile(projectId)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/grant-offer-letter/signed-grant-offer-letter"))
                .andExpect(status().isNoContent())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        // Assert that there is no content
        assertEquals("", response.getContentAsString());
        assertEquals(null, response.getHeader("Content-Disposition"));
        assertEquals(0, response.getContentLength());
    }

    private ServiceResult<GrantOfferLetterStateResource> golState(GrantOfferLetterState state) {
        return serviceSuccess(stateInformationForNonPartnersView(state, GrantOfferLetterEvent.SIGNED_GOL_APPROVED));
    }

    @Override
    protected GrantOfferLetterController supplyControllerUnderTest() {
        return new GrantOfferLetterController();
    }
}
