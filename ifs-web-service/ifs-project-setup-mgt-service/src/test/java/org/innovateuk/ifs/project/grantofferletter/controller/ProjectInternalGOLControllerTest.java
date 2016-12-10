package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProjectInternalGOLViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 *
 **/
public class ProjectInternalGOLControllerTest extends BaseControllerMockMVCTest<ProjectInternalGOLController> {

    @Test
    public void testViewInternalPageWhenOfferGenerated() throws Exception {
        long projectId = 123L;
        long userId = 1L;

        ProjectResource project = newProjectResource().withId(projectId).build();

        FileEntryResource grantOfferLetter = newFileEntryResource().build();
        FileEntryResource additionalContractFile = newFileEntryResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.isUserLeadPartner(projectId, userId)).thenReturn(true);
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

        ProjectInternalGOLViewModel model = (ProjectInternalGOLViewModel) result.getModelAndView().getModel().get("model");

        // test the view model
        assertEquals(project.getId(), model.getProjectId());
        assertEquals(project.getName(), model.getProjectName());
    }

    @Test
    public void testUploadAnnexFile() throws Exception {
        FileEntryResource createdFileDetails = newFileEntryResource().withName("A name").build();

        MockMultipartFile uploadedFile = new MockMultipartFile("additionalContract", "filename.txt", "text/plain", "My content!".getBytes());

        when(projectService.addAdditionalContractFile(123L, "text/plain", 11, "filename.txt", "My content!".getBytes())).
                thenReturn(serviceSuccess(createdFileDetails));

        mockMvc.perform(
                fileUpload("/project/123/offer").
                        file(uploadedFile).
                        param("uploadAnnexFileClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/123/offer"));
    }

    @Test
    public void downloadGeneratedGrantOfferLetterFile() throws Exception {
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
    public void testDownloadAnnexFile() throws Exception {
        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(projectService.getAdditionalContractFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(projectService.getAdditionalContractFileDetails(123L)).
                thenReturn(Optional.of(fileDetails));

        MvcResult result = mockMvc.perform(get("/project/{projectId}/offer/annex-file", 123L)).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("My content!", result.getResponse().getContentAsString());
        assertEquals("inline; filename=\"" + fileDetails.getName() + "\"",
                result.getResponse().getHeader("Content-Disposition"));
    }

    @Override
    protected ProjectInternalGOLController supplyControllerUnderTest() {
        return new ProjectInternalGOLController();
    }
}
