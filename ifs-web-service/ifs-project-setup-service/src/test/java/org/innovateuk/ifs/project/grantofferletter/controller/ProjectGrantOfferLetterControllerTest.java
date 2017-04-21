package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.grantofferletter.form.ProjectGrantOfferLetterForm;
import org.innovateuk.ifs.project.grantofferletter.populator.ProjectGrantOfferLetterViewModelPopulator;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProjectGrantOfferLetterViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectGrantOfferLetterControllerTest extends BaseControllerMockMVCTest<ProjectGrantOfferLetterController> {

    @Spy
    @InjectMocks
    private ProjectGrantOfferLetterViewModelPopulator grantOfferLetterViewModelPopulator;

    @Test
    public void testViewGrantOfferLetterPageWithSignedOffer() throws Exception {
        long projectId = 123L;
        long userId = 1L;

        ProjectResource project = newProjectResource().withId(projectId).build();

        FileEntryResource grantOfferLetter = newFileEntryResource().build();
        FileEntryResource signedGrantOfferLetter = newFileEntryResource().build();
        FileEntryResource additionalContractFile = newFileEntryResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.isProjectManager(userId, projectId)).thenReturn(true);
        when(projectService.isUserLeadPartner(projectId, userId)).thenReturn(true);
        when(projectService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.of(signedGrantOfferLetter));
        when(projectService.getGrantOfferFileDetails(projectId)).thenReturn(Optional.of(grantOfferLetter));
        when(projectService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.of(additionalContractFile));
        when(grantOfferLetterService.isGrantOfferLetterAlreadySent(projectId)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(grantOfferLetterService.isSignedGrantOfferLetterApproved(projectId)).thenReturn(serviceSuccess(Boolean.FALSE));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer", project.getId())).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter")).
                andReturn();

        ProjectGrantOfferLetterViewModel model = (ProjectGrantOfferLetterViewModel) result.getModelAndView().getModel().get("model");

        // test the view model
        assertEquals(project.getId(), model.getProjectId());
        assertEquals(project.getName(), model.getProjectName());
        assertTrue(model.isOfferSigned());
        assertNull(model.getSubmitDate());
        assertTrue(model.isShowSubmitButton());
        assertNull(model.getSubmitDate());
        assertFalse(model.isSubmitted());
        assertFalse(model.isOfferApproved());
    }

    @Test
    public void testDownloadUnsignedGrantOfferLetter() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(projectService.getGrantOfferFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(projectService.getGrantOfferFileDetails(123L)).
                thenReturn(Optional.of(fileDetails));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer/grant-offer-letter", 123L)).
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

        when(projectService.getSignedGrantOfferLetterFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(projectService.getSignedGrantOfferLetterFileDetails(123L)).
                thenReturn(Optional.of(fileDetails));

        when(projectService.isUserLeadPartner(123L, 1L)).thenReturn(true);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer/signed-grant-offer-letter", 123L)).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("My content!", result.getResponse().getContentAsString());
        assertEquals("inline; filename=\"" + fileDetails.getName() + "\"",
                result.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    public void testDownloadSignedGrantOfferLetterByNonLead() throws Exception {

        mockMvc.perform(get("/project/{projectId}/offer/signed-grant-offer-letter", 123L)).
                andExpect(status().isInternalServerError());
    }

    @Test
    public void testDownloadAdditionalContract() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(projectService.getAdditionalContractFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(projectService.getAdditionalContractFileDetails(123L)).
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

        ProjectUserResource pmUser = newProjectUserResource().withRoleName(UserRoleType.PROJECT_MANAGER).withUser(loggedInUser.getId()).build();
        List<ProjectUserResource> puRes = new ArrayList<ProjectUserResource>(Arrays.asList(pmUser));

        when(projectService.getById(123L)).thenReturn(project);
        when(projectService.getProjectUsersForProject(123L)).thenReturn(puRes);
        when(projectService.getGrantOfferFileDetails(123L)).thenReturn(Optional.of(createdFileDetails));
        when(projectService.addSignedGrantOfferLetter(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
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

        ProjectResource project = newProjectResource().withId(123L).build();

        ProjectUserResource pmUser = newProjectUserResource().withRoleName(UserRoleType.PROJECT_MANAGER).withUser(loggedInUser.getId()).build();
        List<ProjectUserResource> puRes = new ArrayList<ProjectUserResource>(Arrays.asList(pmUser));

        when(projectService.getById(123L)).thenReturn(project);
        when(projectService.getProjectUsersForProject(123L)).thenReturn(puRes);
        when(projectService.getSignedGrantOfferLetterFileDetails(123L)).thenReturn(Optional.empty());
        when(projectService.getGrantOfferFileDetails(123L)).thenReturn(Optional.of(createdFileDetails));
        when(projectService.getAdditionalContractFileDetails(123L)).thenReturn(Optional.empty());
        when(projectService.addSignedGrantOfferLetter(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceFailure(CommonFailureKeys.GRANT_OFFER_LETTER_MUST_BE_SENT_BEFORE_UPLOADING_SIGNED_COPY));
        when(grantOfferLetterService.isGrantOfferLetterAlreadySent(123L)).thenReturn(serviceSuccess(Boolean.TRUE));
        when(grantOfferLetterService.isSignedGrantOfferLetterApproved(project.getId())).thenReturn(serviceSuccess(Boolean.FALSE));

        MvcResult mvcResult = mockMvc.perform(
                fileUpload("/project/123/offer").
                        file(uploadedFile).
                        param("uploadSignedGrantOfferLetterClicked", "")).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter")).andReturn();
        ProjectGrantOfferLetterForm form =  (ProjectGrantOfferLetterForm)mvcResult.getModelAndView().getModel().get("form");

        assertEquals(1, form.getObjectErrors().size());
        assertEquals(form.getObjectErrors(), form.getBindingResult().getFieldErrors("signedGrantOfferLetter"));
        assertTrue(form.getObjectErrors().get(0) instanceof FieldError);
    }

    @Test
    public void testConfirmationView() throws Exception {
        mockMvc.perform(get("/project/123/offer/confirmation")).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter-confirmation"));
    }

    @Test
    public void testSubmitOfferLetter() throws Exception {
        long projectId = 123L;

        when(projectService.submitGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/" + projectId + "/offer").
                param("confirmSubmit", "")).
                andExpect(status().is3xxRedirection());

        verify(projectService).submitGrantOfferLetter(projectId);
    }

    @Test
    public void testRemoveSignedGrantOfferLetter() throws Exception {

        when(projectService.removeSignedGrantOfferLetter(123L)).
                thenReturn(serviceSuccess());

        mockMvc.perform(
                post("/project/123/offer").
                        param("removeSignedGrantOfferLetterClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/offer"));
    }

    @Override
    protected ProjectGrantOfferLetterController supplyControllerUnderTest() {
        return new ProjectGrantOfferLetterController();
    }
}
