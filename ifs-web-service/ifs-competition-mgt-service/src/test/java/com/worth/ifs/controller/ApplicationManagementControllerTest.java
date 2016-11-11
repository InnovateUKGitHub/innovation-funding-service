package com.worth.ifs.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.model.ApplicationModelPopulator;
import com.worth.ifs.application.model.ApplicationSectionAndQuestionModelPopulator;
import com.worth.ifs.competition.controller.ApplicationManagementController;
import com.worth.ifs.competition.resource.CompetitionStatus;
import com.worth.ifs.file.controller.viewmodel.OptionalFileDetailsViewModel;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.form.resource.FormInputResponseResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.resource.CompetitionStatus.ASSESSOR_FEEDBACK;
import static com.worth.ifs.competition.resource.CompetitionStatus.FUNDERS_PANEL;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationManagementControllerTest extends BaseControllerMockMVCTest<ApplicationManagementController> {

    @Spy
    @InjectMocks
    private ApplicationModelPopulator applicationModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Test
    public void testDisplayApplicationForCompetitionAdministrator() throws Exception {

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        assertApplicationOverviewExpectations(OptionalFileDetailsViewModel.withNoFile(true));
    }

    @Test
    public void testDisplayApplicationForCompetitionAdministratorWithCorrectAssessorFeedbackReadonly() throws Exception {

        asList(CompetitionStatus.values()).forEach(status -> {

            this.setupCompetition();
            this.setupApplicationWithRoles();
            this.loginDefaultUser();
            this.setupInvites();
            this.setupOrganisationTypes();

            competitionResource.setCompetitionStatus(status);

            boolean expectedReadonlyState = !asList(FUNDERS_PANEL, ASSESSOR_FEEDBACK).contains(status);

            assertApplicationOverviewExpectations(OptionalFileDetailsViewModel.withNoFile(expectedReadonlyState));
        });
    }

    @Test
    public void testDisplayApplicationForCompetitionAdministratorWithCorrectAssessorFeedbackFileEntry() throws Exception {

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        applications.get(0).setAssessorFeedbackFileEntry(123L);

        FileEntryResource existingFileEntry = newFileEntryResource().withName("myfile").withFilesizeBytes(1000).build();

        when(assessorFeedbackRestService.getAssessorFeedbackFileDetails(applications.get(0).getId())).thenReturn(restSuccess(existingFileEntry));
        assertApplicationOverviewExpectations(OptionalFileDetailsViewModel.withExistingFile("myfile", 1000, true));
    }

    @Test
    public void testDownloadAssessorFeedbackFile() throws Exception {

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        ByteArrayResource fileContents = new ByteArrayResource("The returned file data".getBytes());
        FileEntryResource fileEntry = newFileEntryResource().withMediaType("text/hello").withFilesizeBytes(1234L).build();

        when(assessorFeedbackRestService.getAssessorFeedbackFile(applications.get(0).getId())).thenReturn(restSuccess(fileContents));
        when(assessorFeedbackRestService.getAssessorFeedbackFileDetails(applications.get(0).getId())).thenReturn(restSuccess(fileEntry));

        mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId() + "/assessorFeedback") )
                .andExpect(status().isOk())
                .andExpect(content().string("The returned file data"))
                .andExpect(header().string("Content-Type", "text/hello"))
                .andExpect(header().longValue("Content-Length", "The returned file data".length()))
        ;
    }

    @Test
    public void testUploadAssessorFeedbackFile() throws Exception {

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        MockMultipartFile uploadedFile = new MockMultipartFile("assessorFeedback", "filename.txt", "text/plain", "Content to upload".getBytes());

        FileEntryResource successfulCreationResult = newFileEntryResource().build();

        when(assessorFeedbackRestService.addAssessorFeedbackDocument(
                applications.get(0).getId(), "text/plain", 17L, "filename.txt", "Content to upload".getBytes())).
                thenReturn(restSuccess(successfulCreationResult));

        mockMvc.perform(fileUpload("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId()).
                    file(uploadedFile).
                    param("uploadAssessorFeedback", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId()));

        verify(assessorFeedbackRestService).addAssessorFeedbackDocument(
                applications.get(0).getId(), "text/plain", 17L, "filename.txt", "Content to upload".getBytes());
    }

    @Test
    public void testRemoveAssessorFeedbackFile() throws Exception {

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        when(assessorFeedbackRestService.removeAssessorFeedbackDocument(applications.get(0).getId())).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId()).
                    param("removeAssessorFeedback", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId()));

        verify(assessorFeedbackRestService).removeAssessorFeedbackDocument(applications.get(0).getId());
    }

    private void assertApplicationOverviewExpectations(OptionalFileDetailsViewModel expectedAssessorFeedback) {
        Map<Long, FormInputResponseResource> mappedFormInputResponsesToFormInput = new HashMap<>();

        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        try {
            mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId()) )
                    .andExpect(status().isOk())
                    .andExpect(view().name("competition-mgt-application-overview"))
                    .andExpect(model().attribute("applicationReadyForSubmit", false))
                    .andExpect(model().attribute("isCompManagementDownload", true))
                    .andExpect(model().attribute("responses", mappedFormInputResponsesToFormInput))
                    .andExpect(model().attribute("assessorFeedback", expectedAssessorFeedback));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected ApplicationManagementController supplyControllerUnderTest() {
        return new ApplicationManagementController();
    }
}
