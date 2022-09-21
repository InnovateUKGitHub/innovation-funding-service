package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationEoiEvidenceResponse;
import org.innovateuk.ifs.application.domain.ApplicationExpressionOfInterestConfig;
import org.innovateuk.ifs.application.mapper.ApplicationEoiEvidenceResponseMapper;
import org.innovateuk.ifs.application.repository.ApplicationEoiEvidenceResponseRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationEoiEvidenceWorkflowHandler;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.CompetitionEoiEvidenceConfig;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ApplicationEoiEvidenceResponseServiceImplTest extends BaseServiceUnitTest<ApplicationEoiEvidenceResponseServiceImpl> {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationEoiEvidenceResponseRepository applicationEoiEvidenceResponseRepository;

    @Mock
    private ApplicationEoiEvidenceResponseMapper applicationEoiEvidenceResponseMapper;

    @Mock
    private ApplicationEoiEvidenceWorkflowHandler applicationEoiEvidenceWorkflowHandler;

    @Mock
    private UserMapper userMapper;

    private long applicationId;
    private long organisationId;
    private long fileEntryId;
    private ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource;
    private User user;
    private ProcessRole processRole;
    private UserResource userResource;
    private Organisation organisation;

    @Override
    protected ApplicationEoiEvidenceResponseServiceImpl supplyServiceUnderTest() {
        return new ApplicationEoiEvidenceResponseServiceImpl();
    }

    @Before
    public void setup() {
        applicationId = 1L;
        organisationId = 2L;
        fileEntryId = 3L;

        applicationEoiEvidenceResponseResource = ApplicationEoiEvidenceResponseResource.builder()
                .applicationId(applicationId)
                .organisationId(organisationId)
                .fileEntryId(fileEntryId)
                .build();

        user = newUser().build();

        processRole = newProcessRole()
                .withRole(ProcessRoleType.LEADAPPLICANT)
                .withUser(user).build();

        userResource = newUserResource().build();

        organisation = newOrganisation().build();
    }

    @Test
    public void upload() {

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(newCompetition()
                        .withEnabledForExpressionOfInterest(true)
                        .withCompetitionEoiEvidenceConfig(CompetitionEoiEvidenceConfig.builder()
                                .evidenceRequired(true)
                                .build())
                        .build())
                .withActivityState(ApplicationState.SUBMITTED)
                .withApplicationExpressionOfInterestConfig(ApplicationExpressionOfInterestConfig.builder()
                        .enabledForExpressionOfInterest(true)
                        .build())
                .withProcessRole(processRole)
                .build();
        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = ApplicationEoiEvidenceResponse.builder()
                .application(newApplication().build())
                .organisation(newOrganisation().build())
                .fileEntry(newFileEntry().build())
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationEoiEvidenceResponseMapper.mapToDomain(applicationEoiEvidenceResponseResource)).thenReturn(applicationEoiEvidenceResponse);
        when(applicationEoiEvidenceResponseRepository.save(applicationEoiEvidenceResponse)).thenReturn(applicationEoiEvidenceResponse);
        when(applicationEoiEvidenceWorkflowHandler.documentUploaded(applicationEoiEvidenceResponse, processRole, user)).thenReturn(true);
        when(applicationEoiEvidenceResponseMapper.mapToResource(applicationEoiEvidenceResponse)).thenReturn(applicationEoiEvidenceResponseResource);
        when(userMapper.mapToDomain(userResource)).thenReturn(user);

        ServiceResult<ApplicationEoiEvidenceResponseResource> result = service.upload(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void uploadThrowsApplicationNotFound() {

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        ServiceResult<ApplicationEoiEvidenceResponseResource> result = service.upload(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.NOT_FOUND, result.getErrors().get(0).getStatusCode());
        assertEquals("GENERAL_NOT_FOUND", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void uploadThrowsApplicationNotSubmitted() {

        Application application = newApplication()
                .withId(applicationId)
                .withActivityState(ApplicationState.OPENED)
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        ServiceResult<ApplicationEoiEvidenceResponseResource> result = service.upload(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.CONFLICT, result.getErrors().get(0).getStatusCode());
        assertEquals("APPLICATION_UNABLE_TO_UPLOAD_EOI_EVIDENCE_AS_APPLICATION_NOT_YET_SUBMITTED", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void uploadThrowsEvidenceNotRequired() {

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(newCompetition()
                        .withEnabledForExpressionOfInterest(true)
                        .build())
                .withActivityState(ApplicationState.SUBMITTED)
                .withApplicationExpressionOfInterestConfig(ApplicationExpressionOfInterestConfig.builder()
                        .enabledForExpressionOfInterest(true)
                        .build())
                .build();
        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = ApplicationEoiEvidenceResponse.builder()
                .application(newApplication().build())
                .organisation(newOrganisation().build())
                .fileEntry(newFileEntry().build())
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationEoiEvidenceResponseMapper.mapToDomain(applicationEoiEvidenceResponseResource)).thenReturn(applicationEoiEvidenceResponse);
        when(applicationEoiEvidenceResponseRepository.save(applicationEoiEvidenceResponse)).thenReturn(applicationEoiEvidenceResponse);

        ServiceResult<ApplicationEoiEvidenceResponseResource> result = service.upload(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getErrors().get(0).getStatusCode());
        assertEquals("APPLICATION_NOT_ENABLED_FOR_EOI_EVIDENCE_UPLOAD", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void uploadThrowsCompetitionNotEoiEnabled() {

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(newCompetition()
                        .withEnabledForExpressionOfInterest(false)
                        .withCompetitionEoiEvidenceConfig(CompetitionEoiEvidenceConfig.builder()
                                .evidenceRequired(true)
                                .build())
                        .build())
                .withActivityState(ApplicationState.SUBMITTED)
                .withApplicationExpressionOfInterestConfig(ApplicationExpressionOfInterestConfig.builder()
                        .enabledForExpressionOfInterest(true)
                        .build())
                .build();
        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = ApplicationEoiEvidenceResponse.builder()
                .application(newApplication().build())
                .organisation(newOrganisation().build())
                .fileEntry(newFileEntry().build())
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationEoiEvidenceResponseMapper.mapToDomain(applicationEoiEvidenceResponseResource)).thenReturn(applicationEoiEvidenceResponse);
        when(applicationEoiEvidenceResponseRepository.save(applicationEoiEvidenceResponse)).thenReturn(applicationEoiEvidenceResponse);

        ServiceResult<ApplicationEoiEvidenceResponseResource> result = service.upload(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getErrors().get(0).getStatusCode());
        assertEquals("APPLICATION_NOT_ENABLED_FOR_EOI_EVIDENCE_UPLOAD", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void uploadThrowsApplicationNotEoiEnabled() {

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(newCompetition()
                        .withEnabledForExpressionOfInterest(true)
                        .withCompetitionEoiEvidenceConfig(CompetitionEoiEvidenceConfig.builder()
                                .evidenceRequired(true)
                                .build())
                        .build())
                .withActivityState(ApplicationState.SUBMITTED)
                .build();
        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = ApplicationEoiEvidenceResponse.builder()
                .application(newApplication().build())
                .organisation(newOrganisation().build())
                .fileEntry(newFileEntry().build())
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationEoiEvidenceResponseMapper.mapToDomain(applicationEoiEvidenceResponseResource)).thenReturn(applicationEoiEvidenceResponse);
        when(applicationEoiEvidenceResponseRepository.save(applicationEoiEvidenceResponse)).thenReturn(applicationEoiEvidenceResponse);

        ServiceResult<ApplicationEoiEvidenceResponseResource> result = service.upload(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getErrors().get(0).getStatusCode());
        assertEquals("APPLICATION_NOT_ENABLED_FOR_EOI_EVIDENCE_UPLOAD", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void uploadThrowsUnableToInitialiseWorkflow() {

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(newCompetition()
                        .withEnabledForExpressionOfInterest(true)
                        .withCompetitionEoiEvidenceConfig(CompetitionEoiEvidenceConfig.builder()
                                .evidenceRequired(true)
                                .build())
                        .build())
                .withActivityState(ApplicationState.SUBMITTED)
                .withApplicationExpressionOfInterestConfig(ApplicationExpressionOfInterestConfig.builder()
                        .enabledForExpressionOfInterest(true)
                        .build())
                .withProcessRole(processRole)
                .build();
        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = ApplicationEoiEvidenceResponse.builder()
                .application(newApplication().build())
                .organisation(newOrganisation().build())
                .fileEntry(newFileEntry().build())
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationEoiEvidenceResponseMapper.mapToDomain(applicationEoiEvidenceResponseResource)).thenReturn(applicationEoiEvidenceResponse);
        when(applicationEoiEvidenceResponseRepository.save(applicationEoiEvidenceResponse)).thenReturn(applicationEoiEvidenceResponse);
        when(applicationEoiEvidenceWorkflowHandler.documentUploaded(applicationEoiEvidenceResponse, processRole, user)).thenReturn(false);

        ServiceResult<ApplicationEoiEvidenceResponseResource> result = service.upload(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getErrors().get(0).getStatusCode());
        assertEquals("APPLICATION_UNABLE_TO_INITIALISE_EOI_EVIDENCE_UPLOAD", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void remove() {

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(newCompetition()
                        .withEnabledForExpressionOfInterest(true)
                        .withCompetitionEoiEvidenceConfig(CompetitionEoiEvidenceConfig.builder()
                                .evidenceRequired(true)
                                .build())
                        .build())
                .withActivityState(ApplicationState.SUBMITTED)
                .withApplicationExpressionOfInterestConfig(ApplicationExpressionOfInterestConfig.builder()
                        .enabledForExpressionOfInterest(true)
                        .build())
                .withProcessRole(processRole)
                .build();
        ApplicationEoiEvidenceResponse uploadedEoiEvidenceResponse = ApplicationEoiEvidenceResponse.builder()
                .application(application)
                .organisation(organisation)
                .fileEntry(newFileEntry().build())
                .build();
        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = uploadedEoiEvidenceResponse;
        applicationEoiEvidenceResponse.setFileEntry(null);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationEoiEvidenceResponseRepository.findOneByApplicationId (applicationId)).thenReturn(Optional.of(uploadedEoiEvidenceResponse));
        when(applicationEoiEvidenceResponseRepository.save(applicationEoiEvidenceResponse)).thenReturn(applicationEoiEvidenceResponse);
        when(applicationEoiEvidenceWorkflowHandler.documentRemoved(applicationEoiEvidenceResponse, processRole, user)).thenReturn(true);
        when(applicationEoiEvidenceResponseMapper.mapToResource(applicationEoiEvidenceResponse)).thenReturn(applicationEoiEvidenceResponseResource);
        when(userMapper.mapToDomain(userResource)).thenReturn(user);

        ServiceResult<ApplicationEoiEvidenceResponseResource> result = service.remove(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void removeThrowsApplicationNotFound() {

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        ServiceResult<ApplicationEoiEvidenceResponseResource> result = service.remove(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.NOT_FOUND, result.getErrors().get(0).getStatusCode());
        assertEquals("GENERAL_NOT_FOUND", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void removeThrowsUnableToFindUploadedEvidence() {

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(newCompetition()
                        .withEnabledForExpressionOfInterest(true)
                        .withCompetitionEoiEvidenceConfig(CompetitionEoiEvidenceConfig.builder()
                                .evidenceRequired(true)
                                .build())
                        .build())
                .withActivityState(ApplicationState.SUBMITTED)
                .withApplicationExpressionOfInterestConfig(ApplicationExpressionOfInterestConfig.builder()
                        .enabledForExpressionOfInterest(true)
                        .build())
                .withProcessRole(processRole)
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationEoiEvidenceResponseRepository.findOneByApplicationId (applicationId)).thenReturn(Optional.empty());

        ServiceResult<ApplicationEoiEvidenceResponseResource> result = service.remove(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.NOT_FOUND, result.getErrors().get(0).getStatusCode());
        assertEquals("APPLICATION_UNABLE_TO_FIND_UPLOADED_EOI_EVIDENCE", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void removeThrowsUnableToRemoveEoiEvidenceUpload() {

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(newCompetition()
                        .withEnabledForExpressionOfInterest(true)
                        .withCompetitionEoiEvidenceConfig(CompetitionEoiEvidenceConfig.builder()
                                .evidenceRequired(true)
                                .build())
                        .build())
                .withActivityState(ApplicationState.SUBMITTED)
                .withApplicationExpressionOfInterestConfig(ApplicationExpressionOfInterestConfig.builder()
                        .enabledForExpressionOfInterest(true)
                        .build())
                .withProcessRole(processRole)
                .build();
        ApplicationEoiEvidenceResponse uploadedEoiEvidenceResponse = ApplicationEoiEvidenceResponse.builder()
                .application(application)
                .organisation(organisation)
                .fileEntry(newFileEntry().build())
                .build();
        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = uploadedEoiEvidenceResponse;
        applicationEoiEvidenceResponse.setFileEntry(null);

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationEoiEvidenceResponseRepository.findOneByApplicationId (applicationId)).thenReturn(Optional.of(uploadedEoiEvidenceResponse));
        when(applicationEoiEvidenceResponseRepository.save(applicationEoiEvidenceResponse)).thenReturn(applicationEoiEvidenceResponse);
        when(applicationEoiEvidenceWorkflowHandler.documentUploaded(applicationEoiEvidenceResponse, processRole, user)).thenReturn(false);
        when(userMapper.mapToDomain(userResource)).thenReturn(user);

        ServiceResult<ApplicationEoiEvidenceResponseResource> result = service.remove(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getErrors().get(0).getStatusCode());
        assertEquals("APPLICATION_UNABLE_TO_REMOVE_EOI_EVIDENCE_UPLOAD", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void submit() {

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(newCompetition()
                        .withEnabledForExpressionOfInterest(true)
                        .withCompetitionEoiEvidenceConfig(CompetitionEoiEvidenceConfig.builder()
                                .evidenceRequired(true)
                                .build())
                        .build())
                .withActivityState(ApplicationState.SUBMITTED)
                .withApplicationExpressionOfInterestConfig(ApplicationExpressionOfInterestConfig.builder()
                        .enabledForExpressionOfInterest(true)
                        .build())
                .withProcessRole(processRole)
                .build();
        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = ApplicationEoiEvidenceResponse.builder()
                .application(newApplication().build())
                .organisation(newOrganisation().build())
                .fileEntry(newFileEntry().build())
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationEoiEvidenceResponseRepository.findOneByApplicationId (applicationId)).thenReturn(Optional.of(applicationEoiEvidenceResponse));
        when(userMapper.mapToDomain(userResource)).thenReturn(user);
        when(applicationEoiEvidenceWorkflowHandler.submit(applicationEoiEvidenceResponse, processRole, user)).thenReturn(true);

        ServiceResult<Void> result = service.submit(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void submitThrowsApplicationNotFound() {

        UserResource userResource = newUserResource().build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        ServiceResult<Void> result = service.submit(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.NOT_FOUND, result.getErrors().get(0).getStatusCode());
        assertEquals("GENERAL_NOT_FOUND", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void submitThrowsUnableToFindUploadedEvidence() {

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(newCompetition()
                        .withEnabledForExpressionOfInterest(true)
                        .withCompetitionEoiEvidenceConfig(CompetitionEoiEvidenceConfig.builder()
                                .evidenceRequired(true)
                                .build())
                        .build())
                .withActivityState(ApplicationState.SUBMITTED)
                .withApplicationExpressionOfInterestConfig(ApplicationExpressionOfInterestConfig.builder()
                        .enabledForExpressionOfInterest(true)
                        .build())
                .withProcessRole(processRole)
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationEoiEvidenceResponseRepository.findOneByApplicationId (applicationId)).thenReturn(Optional.empty());

        ServiceResult<Void> result = service.submit(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.NOT_FOUND, result.getErrors().get(0).getStatusCode());
        assertEquals("APPLICATION_UNABLE_TO_FIND_UPLOADED_EOI_EVIDENCE", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void submitThrowsUnableToSubmitWorkflow() {

        Application application = newApplication()
                .withId(applicationId)
                .withCompetition(newCompetition()
                        .withEnabledForExpressionOfInterest(true)
                        .withCompetitionEoiEvidenceConfig(CompetitionEoiEvidenceConfig.builder()
                                .evidenceRequired(true)
                                .build())
                        .build())
                .withActivityState(ApplicationState.SUBMITTED)
                .withApplicationExpressionOfInterestConfig(ApplicationExpressionOfInterestConfig.builder()
                        .enabledForExpressionOfInterest(true)
                        .build())
                .withProcessRole(processRole)
                .build();
        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = ApplicationEoiEvidenceResponse.builder()
                .application(newApplication().build())
                .organisation(newOrganisation().build())
                .fileEntry(newFileEntry().build())
                .build();

        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
        when(applicationEoiEvidenceResponseRepository.findOneByApplicationId (applicationId)).thenReturn(Optional.of(applicationEoiEvidenceResponse));
        when(userMapper.mapToDomain(userResource)).thenReturn(user);
        when(applicationEoiEvidenceWorkflowHandler.submit(applicationEoiEvidenceResponse, processRole, user)).thenReturn(false);

        ServiceResult<Void> result = service.submit(applicationEoiEvidenceResponseResource, userResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getErrors().get(0).getStatusCode());
        assertEquals("APPLICATION_UNABLE_TO_SUBMIT_EOI_EVIDENCE_UPLOAD", result.getErrors().get(0).getErrorKey());
    }
}
