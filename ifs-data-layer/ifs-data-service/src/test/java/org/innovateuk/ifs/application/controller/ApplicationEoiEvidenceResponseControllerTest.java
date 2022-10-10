package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationEoiEvidenceProcess;
import org.innovateuk.ifs.application.domain.ApplicationEoiEvidenceResponse;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState;
import org.innovateuk.ifs.application.transactional.ApplicationEoiEvidenceResponseService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionEoiEvidenceConfig;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceState.NOT_SUBMITTED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ApplicationEoiEvidenceResponseControllerTest extends BaseControllerMockMVCTest<ApplicationEoiEvidenceResponseController> {

    @Mock
    private ApplicationEoiEvidenceResponseService applicationEoiEvidenceResponseService;

    @Mock
    private BaseUserService baseUserService;

    long applicationId = 100L;
    long competitionId = 1L;
    long organisationId = 8L;
    long userId = 3L;
    long applicationEoiEvidenceResponseId = 1L;
    long fileEntryId = 88L;

    private ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource;
    private ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse;
    private Application application;
    private Competition competition;
    private FileEntry fileEntry;
    private UserResource userResource;
    private Organisation organisation;
    private ApplicationEoiEvidenceProcess applicationEoiEvidenceProcess;
    private static final long maxFileSize = 1234L;

    @Before
    public void setup() {
        userResource = newUserResource().withId(userId).build();
        organisation = newOrganisation().withId(organisationId).build();
        application = newApplication().withId(applicationId).withCompetition(competition)
                .withProcessRole(newProcessRole()
                        .withRole(ProcessRoleType.LEADAPPLICANT)
                        .withOrganisation(organisation)
                        .withUser(newUser()
                                .withId(userId)
                                .build())
                        .build())
                .build();

        competition = newCompetition().withId(competitionId).withCompetitionEoiEvidenceConfig(CompetitionEoiEvidenceConfig.builder().evidenceRequired(true).build()).build();

        fileEntry = newFileEntry().withId(fileEntryId).withFilesizeBytes(1234L).withMediaType("PDF").build();
        applicationEoiEvidenceResponseResource = ApplicationEoiEvidenceResponseResource.builder()
                .id(applicationEoiEvidenceResponseId)
                .applicationId(applicationId)
                .organisationId(organisationId)
                .fileEntryId(fileEntryId)
                .build();
        applicationEoiEvidenceResponse = ApplicationEoiEvidenceResponse.builder()
                .id(applicationEoiEvidenceResponseId)
                .application(application)
                .organisation(organisation)
                .fileEntry(fileEntry)
                .build();

    }

    @Test
    public void removeEoiEvidence() {

        when(applicationEoiEvidenceResponseService.findOneByApplicationId(applicationId)).thenReturn(serviceSuccess(Optional.of(applicationEoiEvidenceResponseResource)));
        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(userResource));
        when(applicationEoiEvidenceResponseService.remove(applicationEoiEvidenceResponseResource, userResource)).thenReturn(serviceSuccess(ApplicationEoiEvidenceResponseResource.builder()
                .id(32L)
                .applicationId(applicationId)
                .organisationId(organisationId).build()));

        RestResult<ApplicationEoiEvidenceResponseResource> result = controller.remove(applicationId, userId);

        assertTrue(result.isSuccess());
        assertNull(result.getSuccess().getFileEntryId());
        assertEquals(Long.valueOf(applicationId), result.getSuccess().getApplicationId());
        assertEquals(Long.valueOf(organisationId), result.getSuccess().getOrganisationId());
    }

    @Test
    public void submitEoiEvidence() {
        applicationEoiEvidenceProcess = ApplicationEoiEvidenceProcess.builder().processState(NOT_SUBMITTED).target(applicationEoiEvidenceResponse).build();

        when(applicationEoiEvidenceResponseService.findOneByApplicationId(applicationId)).thenReturn(serviceSuccess(Optional.of(applicationEoiEvidenceResponseResource)));
        when(baseUserService.getUserById(userId)).thenReturn(serviceSuccess(userResource));
        when(applicationEoiEvidenceResponseService.submit(applicationEoiEvidenceResponseResource, userResource)).thenReturn(serviceSuccess());

        RestResult<Void> result = controller.submitEoiEvidence(applicationId, userId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void getEvidenceDetailsByApplication() throws Exception {

        FileEntryResource fileEntryResource = newFileEntryResource().withId(fileEntryId).build();
        when(applicationEoiEvidenceResponseService.getEvidenceFileEntryDetails(applicationId)).thenReturn(serviceSuccess(fileEntryResource));

        RestResult<FileEntryResource> result = controller.getEvidenceDetailsByApplication(applicationId);

        assertTrue(result.isSuccess());
        assertEquals(Long.valueOf(fileEntryId), result.getSuccess().getId());
    }

    @Test
    public void findOneByApplicationId() throws Exception {

        FileEntryResource fileEntryResource = newFileEntryResource().withId(fileEntryId).build();
        when(applicationEoiEvidenceResponseService.findOneByApplicationId(applicationId)).thenReturn(serviceSuccess(Optional.of(applicationEoiEvidenceResponseResource)));

        Optional<ApplicationEoiEvidenceResponseResource> result = controller.findOneByApplicationId(applicationId).getSuccess();

        assertEquals(Long.valueOf(applicationId), result.get().getApplicationId());
        assertEquals(Long.valueOf(fileEntryId), result.get().getFileEntryId());
        assertEquals(Long.valueOf(organisationId), result.get().getOrganisationId());
    }

    @Test
    public void getApplicationEoiEvidenceState() throws Exception {

        applicationEoiEvidenceProcess = ApplicationEoiEvidenceProcess.builder().processState(NOT_SUBMITTED).target(applicationEoiEvidenceResponse).build();
        when(applicationEoiEvidenceResponseService.getApplicationEoiEvidenceState(applicationId)).thenReturn(serviceSuccess(Optional.of(NOT_SUBMITTED)));

        Optional<ApplicationEoiEvidenceState> result = controller.getApplicationEoiEvidenceState(applicationId).getSuccess();

        assertEquals(applicationEoiEvidenceProcess.getProcessState().getStateName(), result.get().getStateName());
    }

    @Override
    protected ApplicationEoiEvidenceResponseController supplyControllerUnderTest() {
        ApplicationEoiEvidenceResponseController controller = new ApplicationEoiEvidenceResponseController();
        ReflectionTestUtils.setField(controller, "maxFilesizeBytesForApplicationEoiEvidenceResponse", maxFileSize);
        return controller;
    }
}