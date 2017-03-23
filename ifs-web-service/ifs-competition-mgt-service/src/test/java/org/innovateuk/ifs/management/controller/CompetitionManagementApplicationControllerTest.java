package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.populator.ApplicationModelPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.file.controller.viewmodel.OptionalFileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
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

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.service.Futures.settable;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.ASSESSOR_FEEDBACK;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.FUNDERS_PANEL;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementApplicationControllerTest extends BaseControllerMockMVCTest<CompetitionManagementApplicationController> {

    @Spy
    @InjectMocks
    private ApplicationModelPopulator applicationModelPopulator;

    @Spy
    @InjectMocks
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;


    @Test
    public void displayApplicationOverview() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        assertApplicationOverviewExpectations(OptionalFileDetailsViewModel.withNoFile(true));
    }

    @Test
    public void displayApplicationOverview_backUrlPreservesQueryParams() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        String expectedBackUrl = "/competition/" + competitionResource.getId() + "/applications/all?param1=abc&param2=def%26ghi";

        mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId())
                .param("param1", "abc")
                .param("param2", "def&ghi"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("backUrl", expectedBackUrl));
    }

    @Test
    public void displayApplicationOverview_backUrlEncodesReservedChars() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        String expectedBackUrl = "/competition/" + competitionResource.getId() + "/applications/all?p1=%26&p2=%3D&p3=%25&p4=%20";

        mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId())
                .param("p1", "&")
                .param("p2", "=")
                .param("p3", "%")
                .param("p4", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("backUrl", expectedBackUrl));
    }

    @Test
    public void displayApplicationOverview_submittedApplicationsOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId())
                .param("origin", "SUBMITTED_APPLICATIONS"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("backUrl", "/competition/" + competitionResource.getId() + "/applications/submitted"));
    }

    @Test
    public void displayApplicationOverview_manageApplicationsOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId())
                .param("origin", "MANAGE_APPLICATIONS"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("backUrl", "/assessment/competition/" + competitionResource.getId()));
    }

    @Test
    public void displayApplicationOverview_applicationProgressOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        long competitionId = competitionResource.getId();
        long applicationId = applications.get(0).getId();

        mockMvc.perform(get("/competition/" + competitionId + "/application/" + applicationId)
                .param("origin", "APPLICATION_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("backUrl", "/competition/" + competitionId + "/application/" + applicationId + "/assessors"));
    }

    @Test
    public void displayApplicationOverview_fundingApplicationsOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        long competitionId = competitionResource.getId();
        long applicationId = applications.get(0).getId();

        mockMvc.perform(get("/competition/" + competitionId + "/application/" + applicationId)
                .param("origin", "FUNDING_APPLICATIONS"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition-mgt-application-overview"))
                .andExpect(model().attribute("backUrl", "/competition/" + competitionId + "/funding"));
    }

    @Test
    public void displayApplicationOverview_invalidOrigin() throws Exception {
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId())
                .param("origin", "NOT_A_VALID_ORIGIN"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void displayApplicationForCompetitionAdministratorWithCorrectAssessorFeedbackReadonly() throws Exception {

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
    public void displayApplicationForCompetitionAdministratorWithCorrectAssessorFeedbackFileEntry() throws Exception {

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
    public void downloadAssessorFeedbackFile() throws Exception {

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.loginDefaultUser();
        this.setupInvites();
        this.setupOrganisationTypes();

        ByteArrayResource fileContents = new ByteArrayResource("The returned file data".getBytes());
        FileEntryResource fileEntry = newFileEntryResource().withMediaType("text/hello").withFilesizeBytes(1234L).build();

        when(assessorFeedbackRestService.getAssessorFeedbackFile(applications.get(0).getId())).thenReturn(restSuccess(fileContents));
        when(assessorFeedbackRestService.getAssessorFeedbackFileDetails(applications.get(0).getId())).thenReturn(restSuccess(fileEntry));

        mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId() + "/assessorFeedback"))
                .andExpect(status().isOk())
                .andExpect(content().string("The returned file data"))
                .andExpect(header().string("Content-Type", "text/hello"))
                .andExpect(header().longValue("Content-Length", "The returned file data".length()))
        ;
    }

    @Test
    public void uploadAssessorFeedbackFile() throws Exception {

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
    public void removeAssessorFeedbackFile() throws Exception {

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

        when(financeHandler.getFinanceModelManager(OrganisationTypeEnum.BUSINESS.getOrganisationTypeId())).thenReturn(defaultFinanceModelManager);
        when(questionService.getMarkedAsComplete(anyLong(), anyLong())).thenReturn(settable(new HashSet<>()));

        ProcessRoleResource userApplicationRole = newProcessRoleResource().withApplication(applications.get(0).getId()).withOrganisation(organisations.get(0).getId()).build();
        when(userRestServiceMock.findProcessRole(loggedInUser.getId(), applications.get(0).getId())).thenReturn(restSuccess(userApplicationRole));

        try {
            mockMvc.perform(get("/competition/" + competitionResource.getId() + "/application/" + applications.get(0).getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("competition-mgt-application-overview"))
                    .andExpect(model().attribute("applicationReadyForSubmit", false))
                    .andExpect(model().attribute("isCompManagementDownload", true))
                    .andExpect(model().attribute("responses", mappedFormInputResponsesToFormInput))
                    .andExpect(model().attribute("assessorFeedback", expectedAssessorFeedback))
                    .andExpect(model().attribute("backUrl", "/competition/" + competitionResource.getId() + "/applications/all"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected CompetitionManagementApplicationController supplyControllerUnderTest() {
        return new CompetitionManagementApplicationController();
    }
}
