package com.worth.ifs.project.grantofferletter.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.grantofferletter.viewmodel.ProjectGrantOfferLetterViewModel;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static junit.framework.TestCase.assertFalse;
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

    @Test
    public void testViewGrantOfferLetterPageWithSignedOffer() throws Exception {
        long projectId = 123L;
        long userId = 1L;

        ProjectResource project = newProjectResource().withId(projectId).build();
        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        FileEntryResource grantOfferLetter = newFileEntryResource().build();
        FileEntryResource signedGrantOfferLetter = newFileEntryResource().build();
        FileEntryResource additionalContractFile = newFileEntryResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.isUserLeadPartner(projectId, userId)).thenReturn(true);
        when(projectService.getSignedGrantOfferLetterFileDetails(projectId)).thenReturn(Optional.of(signedGrantOfferLetter));
        when(projectService.getGeneratedGrantOfferFileDetails(projectId)).thenReturn(Optional.of(grantOfferLetter));
        when(projectService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.of(additionalContractFile));
        when(projectService.getProjectUsersForProject(projectId)).thenReturn(newProjectUserResource()
                .withRoleName(UserRoleType.PROJECT_MANAGER)
                .withUser(userId)
                .build(1));

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
    }

    @Test
    public void testDownloadUnsignedGrantOfferLetter() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(projectService.getGeneratedGrantOfferFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(projectService.getGeneratedGrantOfferFileDetails(123L)).
                thenReturn(Optional.of(fileDetails));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer/grant-offer-letter", 123L)).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("My content!", result.getResponse().getContentAsString());
        assertEquals("inline; filename=\"" + fileDetails.getName() + "\"",
                result.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    public void testDownloadSignedGrantOfferLetter() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(projectService.getSignedGrantOfferLetterFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(projectService.getSignedGrantOfferLetterFileDetails(123L)).
                thenReturn(Optional.of(fileDetails));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer/signed-grant-offer-letter", 123L)).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("My content!", result.getResponse().getContentAsString());
        assertEquals("inline; filename=\"" + fileDetails.getName() + "\"",
                result.getResponse().getHeader("Content-Disposition"));
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
    public void testUploadGrantOfferLetter() throws Exception {

        FileEntryResource createdFileDetails = newFileEntryResource().withName("A name").build();

        MockMultipartFile uploadedFile = new MockMultipartFile("grantOfferLetter", "filename.txt", "text/plain", "My content!".getBytes());

        when(projectService.addGeneratedGrantOfferLetter(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceSuccess(createdFileDetails));

        mockMvc.perform(
                fileUpload("/project/123/offer").
                        file(uploadedFile).
                        param("uploadGeneratedOfferLetterClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/offer"));
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

    @Override
    protected ProjectGrantOfferLetterController supplyControllerUnderTest() {
        return new ProjectGrantOfferLetterController();
    }
}