package com.worth.ifs.project.grantofferletter.send;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.error.CommonFailureKeys.*;
import com.worth.ifs.commons.error.exception.ObjectNotFoundException;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionStatus;
import com.worth.ifs.file.builder.FileEntryResourceBuilder;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckState;
import com.worth.ifs.project.grantofferletter.send.controller.ProjectGrantOfferLetterSendController;
import com.worth.ifs.project.grantofferletter.send.form.ProjectGrantOfferLetterSendForm;
import com.worth.ifs.project.grantofferletter.send.viewmodel.ProjectGrantOfferLetterSendViewModel;
import com.worth.ifs.project.resource.PartnerOrganisationResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static com.worth.ifs.commons.error.CommonFailureKeys.EMAILS_NOT_SENT_MULTIPLE;
import static com.worth.ifs.commons.error.CommonFailureKeys.FILES_UNABLE_TO_CREATE_FILE;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.project.builder.CostGroupResourceBuilder.newCostGroupResource;
import static com.worth.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.finance.builder.FinanceCheckProcessResourceBuilder.newFinanceCheckProcessResource;
import static com.worth.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class GrantOfferLetterControllerTest extends BaseControllerMockMVCTest<ProjectGrantOfferLetterSendController> {
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
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId)).thenReturn(competitionSummaryResource);

        when(projectService.getGeneratedGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.isGrantOfferLetterAlreadySent(projectId)).thenReturn(serviceSuccess(Boolean.FALSE));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/grant-offer-letter/send")).
                andExpect(view().name("project/grant-offer-letter-send")).
                andReturn();

        ProjectGrantOfferLetterSendViewModel golViewModel = (ProjectGrantOfferLetterSendViewModel) result.getModelAndView().getModel().get("model");

        assertFalse(golViewModel.isSentToProjectTeam());
        assertEquals(null, golViewModel.getGrantOfferLetterFile());
        assertEquals(null, golViewModel.getAdditionalContractFile());
        assertFalse(golViewModel.getAdditionalContractFileContentAvailable());
        assertFalse(golViewModel.getGrantOfferLetterFileContentAvailable());

        ProjectGrantOfferLetterSendForm form = (ProjectGrantOfferLetterSendForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getAnnex(), null);
    }

    @Test
    public void testSendGOLSuccess() throws Exception {
        Long competitionId = 1L;
        Long projectId = 123L;
        Long applicationId = 789L;

        ApplicationResource applicationResource = newApplicationResource().withId(applicationId).build();
        ProjectResource projectResource = newProjectResource().withId(projectId).withApplication(applicationResource).build();

        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId)).thenReturn(competitionSummaryResource);

        when(projectService.getGeneratedGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.isGrantOfferLetterAlreadySent(projectId)).thenReturn(serviceSuccess(Boolean.FALSE));

        when(projectService.sendGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        // re-load model after sending GOL
        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId)).thenReturn(competitionSummaryResource);

        when(projectService.getGeneratedGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.isGrantOfferLetterAlreadySent(projectId)).thenReturn(serviceSuccess(Boolean.TRUE));


        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/grant-offer-letter/send")).
                andExpect(view().name("project/grant-offer-letter-send")).
                andReturn();

        ProjectGrantOfferLetterSendViewModel golViewModel = (ProjectGrantOfferLetterSendViewModel) result.getModelAndView().getModel().get("model");

        assertTrue(golViewModel.isSentToProjectTeam());
        assertEquals(null, golViewModel.getGrantOfferLetterFile());
        assertEquals(null, golViewModel.getAdditionalContractFile());
        assertFalse(golViewModel.getAdditionalContractFileContentAvailable());
        assertFalse(golViewModel.getGrantOfferLetterFileContentAvailable());
        assertEquals(Boolean.TRUE, golViewModel.isSentToProjectTeam());

        ProjectGrantOfferLetterSendForm form = (ProjectGrantOfferLetterSendForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getAnnex(), null);
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
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId)).thenReturn(competitionSummaryResource);

        when(projectService.getGeneratedGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.isGrantOfferLetterAlreadySent(projectId)).thenReturn(serviceSuccess(Boolean.FALSE));

        when(projectService.sendGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        // re-load model after sending GOL
        when(projectService.getById(projectId)).thenReturn(projectResource);

        when(applicationService.getById(applicationId)).thenReturn(newApplicationResource().withId(applicationId).withCompetition(competitionId).build());

        competitionSummaryResource = newCompetitionSummaryResource().withId(competitionId).withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId)).thenReturn(competitionSummaryResource);

        when(projectService.getGeneratedGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.isGrantOfferLetterAlreadySent(projectId)).thenReturn(serviceSuccess(Boolean.FALSE));


        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/grant-offer-letter/send")).
                andExpect(view().name("project/grant-offer-letter-send")).
                andReturn();

        ProjectGrantOfferLetterSendViewModel golViewModel = (ProjectGrantOfferLetterSendViewModel) result.getModelAndView().getModel().get("model");

        assertFalse(golViewModel.isSentToProjectTeam());
        assertEquals(null, golViewModel.getGrantOfferLetterFile());
        assertEquals(null, golViewModel.getAdditionalContractFile());
        assertFalse(golViewModel.getAdditionalContractFileContentAvailable());
        assertFalse(golViewModel.getGrantOfferLetterFileContentAvailable());
        assertEquals(Boolean.FALSE, golViewModel.isSentToProjectTeam());

        ProjectGrantOfferLetterSendForm form = (ProjectGrantOfferLetterSendForm) result.getModelAndView().getModel().get("form");
        assertEquals(form.getAnnex(), null);
    }

    @Test
    public void testDownloadGOLFileEntryNotPresent() throws Exception {

        Long projectId = 1L;

        when(projectService.getGeneratedGrantOfferFile(projectId)).thenReturn(Optional.empty());
        when(projectService.getGeneratedGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());

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

        when(projectService.getGeneratedGrantOfferFile(projectId)).thenReturn(Optional.of(golByteArrayResource));
        when(projectService.getGeneratedGrantOfferFileDetails(projectId)).thenReturn(Optional.of(golFileEntryResource));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/grant-offer-letter/grant-offer-letter"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();
        assertEquals("HelloWorld", response.getContentAsString());
        assertEquals("inline; filename=\"gol-file.pdf\"", response.getHeader("Content-Disposition"));
        assertEquals(10, response.getContentLength());
    }

    @Test
    public void testDownloadAnnexFileEntryNotPresent() throws Exception {

        Long projectId = 1L;

        when(projectService.getAdditionalContractFile(projectId)).thenReturn(Optional.empty());
        when(projectService.getGeneratedGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());

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

        when(projectService.getAdditionalContractFile(projectId)).thenReturn(Optional.of(annexByteArrayResource));
        when(projectService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.of(annexFileEntryResource));

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
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId)).thenReturn(competitionSummaryResource);

        when(projectService.getGeneratedGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getAdditionalContractFileDetails(projectId)).thenReturn(annexFileEntryResource);
        when(projectService.isGrantOfferLetterAlreadySent(projectId)).thenReturn(serviceSuccess(Boolean.FALSE));

        when(projectService.sendGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        MockMultipartFile uploadedFile = new MockMultipartFile("annex", "annex.pdf", "application/pdf", "My content!".getBytes());
        FileEntryResource createdFileDetails = newFileEntryResource().withName("1").withMediaType("application/pdf").withFilesizeBytes(20).build();

        when(projectService.addAdditionalContractFile(123L, "application/pdf", 11, "annex.pdf", "My content!".getBytes())).
                thenReturn(serviceSuccess(createdFileDetails));

        MvcResult result = mockMvc.perform(
                fileUpload("/project/"+ projectId  + "/grant-offer-letter/upload-annex").
                        file(uploadedFile).param("uploadAnnexClicked", "")).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/grant-offer-letter/send")).
                andReturn();

        ProjectGrantOfferLetterSendForm form = (ProjectGrantOfferLetterSendForm) result.getModelAndView().getModel().get("form");
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
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(competitionId)).thenReturn(competitionSummaryResource);

        when(projectService.getGeneratedGrantOfferFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getAdditionalContractFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.isGrantOfferLetterAlreadySent(projectId)).thenReturn(serviceSuccess(Boolean.FALSE));

        when(projectService.sendGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        MockMultipartFile uploadedFile = new MockMultipartFile("annex", "annex.pdf", "application/pdf", "My content!".getBytes());

        when(projectService.addAdditionalContractFile(123L, "application/pdf", 11, "annex.pdf", "My content!".getBytes())).
                thenReturn(ServiceResult.serviceFailure(new com.worth.ifs.commons.error.Error(FILES_UNABLE_TO_CREATE_FILE, INTERNAL_SERVER_ERROR)));

        MvcResult result = mockMvc.perform(
                fileUpload("/project/"+ projectId  + "/grant-offer-letter/upload-annex").
                        file(uploadedFile).param("uploadAnnexClicked", "")).
                andExpect(status().isOk()).
                andExpect(view().name("project/grant-offer-letter-send")).
                andReturn();

        ProjectGrantOfferLetterSendForm form = (ProjectGrantOfferLetterSendForm) result.getModelAndView().getModel().get("form");
        assertEquals(uploadedFile, form.getAnnex());
        assertEquals(Boolean.FALSE, ((ProjectGrantOfferLetterSendViewModel)result.getModelAndView().getModel().get("model")).getAdditionalContractFileContentAvailable());
    }

    @Override
    protected ProjectGrantOfferLetterSendController supplyControllerUnderTest() {
        return new ProjectGrantOfferLetterSendController();
    }
}
