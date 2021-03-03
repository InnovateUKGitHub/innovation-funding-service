package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.grantofferletter.GrantOfferLetterService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.grantofferletter.form.GrantOfferLetterForm;
import org.innovateuk.ifs.project.grantofferletter.populator.GrantOfferLetterModelPopulator;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.GrantOfferLetterModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent.*;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState.*;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource.stateInformationForNonPartnersView;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource.stateInformationForPartnersView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class GrantOfferLetterControllerTest extends BaseControllerMockMVCTest<GrantOfferLetterController> {

    @Spy
    @InjectMocks
    @SuppressWarnings("unused")
    private GrantOfferLetterModelPopulator grantOfferLetterViewModelPopulator;

    @Mock
    private GrantOfferLetterService grantOfferLetterService;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void testViewGrantOfferLetterPageWithSignedOfferAsProjectManager() throws Exception {
        long projectId = 123L;
        long userId = 1L;

        ProjectResource project = newProjectResource().withId(projectId).withCompetition(5L).build();

        setupSignedSentGrantOfferLetterExpectations(projectId, userId, project, true, stateInformationForNonPartnersView(READY_TO_APPROVE, GOL_SIGNED));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer", project.getId())).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter")).
                andReturn();

        GrantOfferLetterModel model = (GrantOfferLetterModel) result.getModelAndView().getModel().get("model");

        // test the view model
        assertEquals(project.getId(), model.getProjectId());
        assertEquals(project.getName(), model.getProjectName());
        assertTrue(model.isOfferSigned());
        assertFalse(model.isShowSubmitButton());
        assertTrue(model.isSubmitted());
        assertFalse(model.isGrantOfferLetterApproved());
        assertFalse(model.isGrantOfferLetterRejected());

        verifySignedSentGrantOfferLetterExpectations(projectId, userId);
    }

    @Test
    public void testViewGrantOfferLetterPageWithSignedOfferAsNonProjectManager() throws Exception {
        long projectId = 123L;
        long userId = 1L;

        ProjectResource project = newProjectResource().withId(projectId).build();

        setupSignedSentGrantOfferLetterExpectations(projectId, userId, project, true, stateInformationForPartnersView(READY_TO_APPROVE, GOL_SIGNED));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer", project.getId())).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter")).
                andReturn();

        GrantOfferLetterModel model = (GrantOfferLetterModel) result.getModelAndView().getModel().get("model");

        // test the view model
        assertEquals(project.getId(), model.getProjectId());
        assertEquals(project.getName(), model.getProjectName());
        assertTrue(model.isOfferSigned());
        assertFalse(model.isShowSubmitButton());
        assertTrue(model.isSubmitted());
        assertFalse(model.isGrantOfferLetterApproved());
        assertFalse(model.isGrantOfferLetterRejected());

        verifySignedSentGrantOfferLetterExpectations(projectId, userId);
    }

    @Test
    public void testViewGrantOfferLetterPageWithApprovedOffer() throws Exception {
        long projectId = 123L;
        long userId = 1L;

        ProjectResource project = newProjectResource().withId(projectId).build();

        setupSignedSentGrantOfferLetterExpectations(projectId, userId, project, false, stateInformationForNonPartnersView(APPROVED, SIGNED_GOL_APPROVED));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer", project.getId())).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter")).
                andReturn();

        GrantOfferLetterModel model = (GrantOfferLetterModel) result.getModelAndView().getModel().get("model");

        // test the view model
        assertEquals(project.getId(), model.getProjectId());
        assertEquals(project.getName(), model.getProjectName());
        assertTrue(model.isOfferSigned());
        assertFalse(model.isShowSubmitButton());
        assertTrue(model.isSubmitted());
        assertTrue(model.isGrantOfferLetterApproved());
        assertFalse(model.isGrantOfferLetterRejected());

        verifySignedSentGrantOfferLetterExpectations(projectId, userId);
    }

    @Test
    public void testViewGrantOfferLetterPageWithRejectedOfferAsProjectManager() throws Exception {
        long projectId = 123L;
        long userId = 1L;

        ProjectResource project = newProjectResource().withId(projectId).build();

        setupSignedSentGrantOfferLetterExpectations(projectId, userId, project, true, stateInformationForNonPartnersView(SENT, SIGNED_GOL_REJECTED));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer", project.getId())).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter")).
                andReturn();

        GrantOfferLetterModel model = (GrantOfferLetterModel) result.getModelAndView().getModel().get("model");

        // test the view model
        assertEquals(project.getId(), model.getProjectId());
        assertEquals(project.getName(), model.getProjectName());
        assertTrue(model.isOfferSigned());
        assertTrue(model.isShowSubmitButton());
        assertFalse(model.isSubmitted());
        assertFalse(model.isGrantOfferLetterApproved());
        assertTrue(model.isGrantOfferLetterRejected());

        verifySignedSentGrantOfferLetterExpectations(projectId, userId);
    }

    @Test
    public void testViewGrantOfferLetterPageWithRejectedOfferAsNonProjectManager() throws Exception {
        long projectId = 123L;
        long userId = 1L;

        ProjectResource project = newProjectResource().withId(projectId).withCompetition(5L).build();

        setupSignedSentGrantOfferLetterExpectations(projectId, userId, project, false, stateInformationForPartnersView(SENT, SIGNED_GOL_REJECTED));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer", project.getId())).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter")).
                andReturn();

        GrantOfferLetterModel model = (GrantOfferLetterModel) result.getModelAndView().getModel().get("model");

        // test the view model
        assertEquals(project.getId(), model.getProjectId());
        assertEquals(project.getName(), model.getProjectName());
        assertTrue(model.isOfferSigned());
        assertFalse(model.isShowSubmitButton());
        assertTrue(model.isSubmitted());
        assertFalse(model.isGrantOfferLetterApproved());
        assertFalse(model.isGrantOfferLetterRejected());

        verifySignedSentGrantOfferLetterExpectations(projectId, userId);
    }

    @Test
    public void testDownloadUnsignedGrantOfferLetter() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(grantOfferLetterService.getGrantOfferFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(grantOfferLetterService.getGrantOfferFileDetails(123L)).
                thenReturn(Optional.of(fileDetails));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer/download", 123L)).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("My content!", result.getResponse().getContentAsString());
        assertEquals("inline; filename=\"" + fileDetails.getName() + "\"",
                result.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    public void testDownloadSignedGrantOfferLetterByLead() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(grantOfferLetterService.getSignedGrantOfferLetterFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(123L)).
                thenReturn(Optional.of(fileDetails));

        when(projectService.isUserLeadPartner(123L, 1L)).thenReturn(true);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer/signed-download", 123L)).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("My content!", result.getResponse().getContentAsString());
        assertEquals("inline; filename=\"" + fileDetails.getName() + "\"",
                result.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    @Ignore
    public void testDownloadSignedGrantOfferLetterByNonLead() throws Exception {

        mockMvc.perform(get("/project/{projectId}/offer/signed-grant-offer-letter", 123L)).
                andExpect(status().isInternalServerError());
    }

    @Test
    public void testDownloadAdditionalContract() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(grantOfferLetterService.getAdditionalContractFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(grantOfferLetterService.getAdditionalContractFileDetails(123L)).
                thenReturn(Optional.of(fileDetails));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer/additional-contract", 123L)).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("My content!", result.getResponse().getContentAsString());
        assertEquals("inline; filename=\"" + fileDetails.getName() + "\"",
                result.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    public void testUploadSignedGrantOfferLetter() throws Exception {

        FileEntryResource createdFileDetails = newFileEntryResource().withName("A name").build();

        MockMultipartFile uploadedFile = new MockMultipartFile("signedGrantOfferLetter", "filename.txt", "text/plain", "My content!".getBytes());

        ProjectResource project = newProjectResource().withId(123L).build();

        List<ProjectUserResource> pmUser = newProjectUserResource().
                withRole(ProjectParticipantRole.PROJECT_MANAGER).
                withUser(loggedInUser.getId()).
                build(1);

        when(projectService.getById(123L)).thenReturn(project);
        when(projectService.getProjectUsersForProject(123L)).thenReturn(pmUser);
        when(grantOfferLetterService.getGrantOfferFileDetails(123L)).thenReturn(Optional.of(createdFileDetails));
        when(grantOfferLetterService.addSignedGrantOfferLetter(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceSuccess(createdFileDetails));

        mockMvc.perform(
                fileUpload("/project/123/offer").
                        file(uploadedFile).
                        param("uploadSignedGrantOfferLetterClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/offer"));
    }

    @Test
    public void testUploadSignedGrantOfferLetterGolNotSent() throws Exception {

        FileEntryResource createdFileDetails = newFileEntryResource().withName("A name").build();

        MockMultipartFile uploadedFile = new MockMultipartFile("signedGrantOfferLetter", "filename.txt", "text/plain", "My content!".getBytes());

        ProjectResource project = newProjectResource().withId(123L).withCompetition(5L).build();

        ProjectUserResource pmUser = newProjectUserResource().withRole(ProjectParticipantRole.PROJECT_MANAGER).withUser(loggedInUser.getId()).build();
        List<ProjectUserResource> puRes = new ArrayList<ProjectUserResource>(Arrays.asList(pmUser));

        when(projectService.getById(123L)).thenReturn(project);
        when(projectService.getProjectUsersForProject(123L)).thenReturn(puRes);
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(123L)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferFileDetails(123L)).thenReturn(Optional.of(createdFileDetails));
        when(grantOfferLetterService.getAdditionalContractFileDetails(123L)).thenReturn(Optional.empty());
        when(grantOfferLetterService.addSignedGrantOfferLetter(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_MUST_BE_SENT_BEFORE_UPLOADING_SIGNED_COPY));
        when(grantOfferLetterService.getGrantOfferLetterState(123L)).thenReturn(serviceSuccess(stateInformationForNonPartnersView(READY_TO_APPROVE, GOL_SIGNED)));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(newCompetitionResource().withFundingType(FundingType.GRANT).build()));

        MvcResult mvcResult = mockMvc.perform(
                fileUpload("/project/123/offer").
                        file(uploadedFile).
                        param("uploadSignedGrantOfferLetterClicked", "")).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter")).andReturn();
        GrantOfferLetterForm form = (GrantOfferLetterForm) mvcResult.getModelAndView().getModel().get("form");

        assertEquals(1, form.getObjectErrors().size());
        assertEquals(form.getObjectErrors(), form.getBindingResult().getFieldErrors("signedGrantOfferLetter"));
        assertTrue(form.getObjectErrors().get(0) instanceof FieldError);
    }

    @Test
    public void testUploadSignedGrantOfferLetterGolRejected() throws Exception {

        FileEntryResource createdFileDetails = newFileEntryResource().withName("A name").build();

        MockMultipartFile uploadedFile = new MockMultipartFile("signedGrantOfferLetter", "filename.txt", "text/plain", "My content!".getBytes());

        ProjectResource project = newProjectResource().withId(123L).withCompetition(5L).build();

        List<ProjectUserResource> pmUser = newProjectUserResource().
                withRole(ProjectParticipantRole.PROJECT_MANAGER).
                withUser(loggedInUser.getId()).
                build(1);

        when(projectService.getById(123L)).thenReturn(project);
        when(projectService.getProjectUsersForProject(123L)).thenReturn(pmUser);
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(123L)).thenReturn(Optional.empty());
        when(grantOfferLetterService.getGrantOfferFileDetails(123L)).thenReturn(Optional.of(createdFileDetails));
        when(grantOfferLetterService.getAdditionalContractFileDetails(123L)).thenReturn(Optional.empty());
        when(grantOfferLetterService.addSignedGrantOfferLetter(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_MUST_BE_SENT_BEFORE_UPLOADING_SIGNED_COPY));
        when(grantOfferLetterService.getGrantOfferLetterState(123L)).thenReturn(serviceSuccess(stateInformationForNonPartnersView(SENT, SIGNED_GOL_REJECTED)));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(newCompetitionResource().withFundingType(FundingType.GRANT).build()));

        MvcResult mvcResult = mockMvc.perform(
                fileUpload("/project/123/offer").
                        file(uploadedFile).
                        param("uploadSignedGrantOfferLetterClicked", "")).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter")).andReturn();
        GrantOfferLetterForm form = (GrantOfferLetterForm) mvcResult.getModelAndView().getModel().get("form");

        assertEquals(1, form.getObjectErrors().size());
        assertEquals(form.getObjectErrors(), form.getBindingResult().getFieldErrors("signedGrantOfferLetter"));
        assertTrue(form.getObjectErrors().get(0) instanceof FieldError);
    }

    @Test
    public void testUploadSignedAdditionalContract() throws Exception {

        FileEntryResource createdFileDetails = newFileEntryResource().withName("A name").build();

        MockMultipartFile uploadedFile = new MockMultipartFile("signedAdditionalContract", "filename.txt", "text/plain", "My content!".getBytes());

        ProjectResource project = newProjectResource().withId(123L).build();

        List<ProjectUserResource> pmUser = newProjectUserResource().
                withRole(ProjectParticipantRole.PROJECT_MANAGER).
                withUser(loggedInUser.getId()).
                build(1);

        when(projectService.getById(123L)).thenReturn(project);
        when(projectService.getProjectUsersForProject(123L)).thenReturn(pmUser);
        when(grantOfferLetterService.getGrantOfferFileDetails(123L)).thenReturn(Optional.of(createdFileDetails));
        when(grantOfferLetterService.addSignedAdditionalContract(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceSuccess(createdFileDetails));

        mockMvc.perform(
                fileUpload("/project/123/offer").
                        file(uploadedFile).
                        param("uploadSignedAdditionalContractFileClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/offer"));
    }

    @Test
    public void testConfirmationView() throws Exception {
        ProjectResource project = newProjectResource().withId(123L).withCompetition(5L).build();
        when(projectRestService.getProjectById(123L)).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(newCompetitionResource().withFundingType(FundingType.GRANT).build()));
        mockMvc.perform(get("/project/123/offer/confirmation")).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter-confirmation"));
    }

    @Test
    public void testSubmitOfferLetter() throws Exception {
        long projectId = 123L;

        when(grantOfferLetterService.submitGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/" + projectId + "/offer").
                param("confirmSubmit", "")).
                andExpect(status().is3xxRedirection());

        verify(grantOfferLetterService).submitGrantOfferLetter(projectId);
    }

    @Test
    public void testRemoveSignedGrantOfferLetter() throws Exception {

        when(grantOfferLetterService.removeSignedGrantOfferLetter(123L)).
                thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/123/offer").
                        param("removeSignedGrantOfferLetterClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/offer"));
    }

    @Test
    public void testRemoveSignedAdditionalContract() throws Exception {

        when(grantOfferLetterService.removeSignedAdditionalContract(123L)).
                thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/123/offer").
                        param("removeSignedAdditionalContractFileClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/offer"));
    }

    private void setupSignedSentGrantOfferLetterExpectations(long projectId, long userId, ProjectResource project, boolean projectManager, GrantOfferLetterStateResource state) {
        FileEntryResource grantOfferLetter = newFileEntryResource().build();
        FileEntryResource signedGrantOfferLetter = newFileEntryResource().build();
        FileEntryResource additionalContractFile = newFileEntryResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionRestService.getCompetitionById(project.getCompetition())).thenReturn(restSuccess(newCompetitionResource().withFundingType(FundingType.GRANT).build()));
        when(projectService.isProjectManager(userId, projectId)).thenReturn(projectManager);
        when(projectService.isUserLeadPartner(projectId, userId)).thenReturn(true);
        when(grantOfferLetterService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.of(signedGrantOfferLetter));
        when(grantOfferLetterService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.of(grantOfferLetter));
        when(grantOfferLetterService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.of(additionalContractFile));
        when(grantOfferLetterService.getGrantOfferLetterState(projectId)).thenReturn(serviceSuccess(state));
    }

    private void verifySignedSentGrantOfferLetterExpectations(long projectId, long userId) {

        verify(projectService).getById(projectId);
        verify(projectService).isProjectManager(userId, projectId);
        verify(projectService).isUserLeadPartner(projectId, userId);
        verify(grantOfferLetterService).getSignedGrantOfferLetterFileDetails(projectId);
        verify(grantOfferLetterService).getGrantOfferFileDetails(projectId);
        verify(grantOfferLetterService).getAdditionalContractFileDetails(projectId);
        verify(grantOfferLetterService).getGrantOfferLetterState(projectId);
    }

    @Override
    protected GrantOfferLetterController supplyControllerUnderTest() {
        return new GrantOfferLetterController();
    }
}
