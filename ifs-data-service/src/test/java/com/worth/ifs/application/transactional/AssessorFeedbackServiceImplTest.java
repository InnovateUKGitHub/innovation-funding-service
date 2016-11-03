package com.worth.ifs.application.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.file.domain.FileEntry;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.file.service.FileAndContents;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.notifications.resource.UserNotificationTarget;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.constant.ApplicationStatusConstants.APPROVED;
import static com.worth.ifs.application.constant.ApplicationStatusConstants.REJECTED;
import static com.worth.ifs.application.transactional.ApplicationFundingServiceImplMockTest.createFullNotificationExpectations;
import static com.worth.ifs.application.transactional.ApplicationFundingServiceImplMockTest.createSimpleNotificationExpectations;
import static com.worth.ifs.application.transactional.ApplicationSummaryServiceImpl.FUNDING_DECISIONS_MADE_STATUS_IDS;
import static com.worth.ifs.application.transactional.AssessorFeedbackServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED;
import static com.worth.ifs.application.transactional.AssessorFeedbackServiceImpl.Notifications.APPLICATION_NOT_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.file.domain.builders.FileEntryBuilder.newFileEntry;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AssessorFeedbackServiceImplTest extends BaseServiceUnitTest<AssessorFeedbackServiceImpl> {

    private static final String webBaseUrl = "http://ifs-local-dev";

    @Test
    public void testCreateAssessorFeedbackFileEntry() {

        FileEntryResource fileEntryToCreate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        Application application = newApplication().withId(123L).build();
        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);

        FileEntry createdFileEntry = newFileEntry().build();
        ServiceResult<Pair<File, FileEntry>> successfulFileCreationResult = serviceSuccess(Pair.of(new File("createdfile"), createdFileEntry));
        when(fileServiceMock.createFile(fileEntryToCreate, inputStreamSupplier)).thenReturn(successfulFileCreationResult);

        FileEntryResource createdFileEntryResource = newFileEntryResource().build();
        when(fileEntryMapperMock.mapToResource(createdFileEntry)).thenReturn(createdFileEntryResource);

        //
        // Call the method under test
        //
        ServiceResult<FileEntryResource> result = service.createAssessorFeedbackFileEntry(application.getId(), fileEntryToCreate, inputStreamSupplier);

        //
        // Assert that the result of our service call was successful and contains the resource returned from the mapper
        //
        assertTrue(result.isSuccess());
        assertEquals(createdFileEntryResource, result.getSuccessObject());

        // assert that the application entity got its Assessor Feedback file entry updated to match the FileEntry returned by
        // the FileService
        assertEquals(createdFileEntry, application.getAssessorFeedbackFileEntry());

        verify(applicationRepositoryMock).findOne(application.getId());
        verifyNoMoreInteractions(addressRepositoryMock);
    }

    @Test
    public void testCreateAssessorFeedbackFileEntryButApplicationDoesntExist() {

        FileEntryResource fileEntryToCreate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        when(applicationRepositoryMock.findOne(123L)).thenReturn(null);

        //
        // Call the method under test
        //
        ServiceResult<FileEntryResource> result = service.createAssessorFeedbackFileEntry(123L, fileEntryToCreate, inputStreamSupplier);

        //
        // Assert that the result of our service call was successful and contains the resource returned from the mapper
        //
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Application.class, 123L)));
    }

    @Test
    public void testGetAssessorFeedbackFileEntryDetails() {

        FileEntry existingFileEntry = newFileEntry().build();
        Application application = newApplication().withId(123L).withAssessorFeedbackFileEntry(existingFileEntry).build();
        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);

        FileEntryResource retrievedFileEntryResource = newFileEntryResource().build();
        when(fileEntryMapperMock.mapToResource(existingFileEntry)).thenReturn(retrievedFileEntryResource);

        //
        // Call the method under test
        //
        ServiceResult<FileEntryResource> result = service.getAssessorFeedbackFileEntryDetails(application.getId());

        //
        // Assert that the result of our service call was successful and contains the resource returned from the mapper
        //
        assertTrue(result.isSuccess());
        assertEquals(retrievedFileEntryResource, result.getSuccessObject());

        verify(applicationRepositoryMock).findOne(application.getId());
        verifyNoMoreInteractions(addressRepositoryMock);
    }

    @Test
    public void testGetAssessorFeedbackFileEntryContents() {

        FileEntry existingFileEntry = newFileEntry().build();
        Application application = newApplication().withId(123L).withAssessorFeedbackFileEntry(existingFileEntry).build();
        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);

        FileEntryResource retrievedFileEntryResource = newFileEntryResource().build();
        when(fileEntryMapperMock.mapToResource(existingFileEntry)).thenReturn(retrievedFileEntryResource);

        Supplier<InputStream> inputStreamSupplier = () -> null;
        when(fileServiceMock.getFileByFileEntryId(existingFileEntry.getId())).thenReturn(serviceSuccess(inputStreamSupplier));

        //
        // Call the method under test
        //
        ServiceResult<FileAndContents> result = service.getAssessorFeedbackFileEntryContents(application.getId());

        //
        // Assert that the result of our service call was successful and contains the resource returned from the mapper
        //
        assertTrue(result.isSuccess());
        assertEquals(retrievedFileEntryResource, result.getSuccessObject().getFileEntry());
        assertEquals(inputStreamSupplier, result.getSuccessObject().getContentsSupplier());

        verify(applicationRepositoryMock).findOne(application.getId());
        verifyNoMoreInteractions(addressRepositoryMock);
    }

    @Test
    public void testUpdateAssessorFeedbackFileEntry() {

        FileEntryResource fileEntryToUpdate = newFileEntryResource().build();
        Supplier<InputStream> inputStreamSupplier = () -> null;

        Application application = newApplication().withId(123L).build();
        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);

        FileEntry updatedFileEntry = newFileEntry().build();
        ServiceResult<Pair<File, FileEntry>> successfulFileUpdateResult = serviceSuccess(Pair.of(new File("updatedfile"), updatedFileEntry));
        when(fileServiceMock.updateFile(fileEntryToUpdate, inputStreamSupplier)).thenReturn(successfulFileUpdateResult);

        FileEntryResource updatedFileEntryResource = newFileEntryResource().build();
        when(fileEntryMapperMock.mapToResource(updatedFileEntry)).thenReturn(updatedFileEntryResource);

        //
        // Call the method under test
        //
        ServiceResult<Void> result = service.updateAssessorFeedbackFileEntry(application.getId(), fileEntryToUpdate, inputStreamSupplier);

        //
        // Assert that the result of our service call was successful and contains the resource returned from the mapper
        //
        assertTrue(result.isSuccess());

        // assert that the application entity got its Assessor Feedback file entry updated to match the FileEntry returned by
        // the FileService
        assertEquals(updatedFileEntry, application.getAssessorFeedbackFileEntry());

        verify(applicationRepositoryMock).findOne(application.getId());
        verifyNoMoreInteractions(addressRepositoryMock);
    }

    @Test
    public void testDeleteAssessorFeedbackFileEntry() {

        FileEntry fileEntryToDelete = newFileEntry().build();

        Application application = newApplication().withId(123L).withAssessorFeedbackFileEntry(fileEntryToDelete).build();
        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);

        when(fileServiceMock.deleteFile(fileEntryToDelete.getId())).thenReturn(serviceSuccess(fileEntryToDelete));

        //
        // Call the method under test
        //
        ServiceResult<Void> result = service.deleteAssessorFeedbackFileEntry(application.getId());

        //
        // Assert that the result of our service call was successful
        //
        assertTrue(result.isSuccess());

        // assert that the application entity got its Assessor Feedback file entry deleted
        assertNull(application.getAssessorFeedbackFileEntry());

        verify(applicationRepositoryMock).findOne(application.getId());
        verifyNoMoreInteractions(addressRepositoryMock);
    }
    
    @Test
    public void testFeedbackUploadedNotUploaded() {
    	
    	when(applicationRepositoryMock.countByCompetitionIdAndApplicationStatusIdInAndAssessorFeedbackFileEntryIsNull(123L, Arrays.asList(3L, 4L, 2L))).thenReturn(5L);
    	
    	ServiceResult<Boolean> result = service.assessorFeedbackUploaded(123L);
    	
    	assertTrue(result.isSuccess());
    	assertFalse(result.getSuccessObject());
    }
    
    @Test
    public void testFeedbackUploadedIsUploaded() {
    	
    	when(applicationRepositoryMock.countByCompetitionIdAndApplicationStatusIdInAndAssessorFeedbackFileEntryIsNull(123L, Arrays.asList(3L, 4L, 2L))).thenReturn(0L);
    	
    	ServiceResult<Boolean> result = service.assessorFeedbackUploaded(123L);
    	
    	assertTrue(result.isSuccess());
    	assertTrue(result.getSuccessObject());
    }
    
    @Test
    public void testSubmitAssessorFeedback() {
    	
    	Competition competition = newCompetition().withId(123L).build();
    	when(competitionRepositoryMock.findOne(123L)).thenReturn(competition);
    	
    	ServiceResult<Void> result = service.submitAssessorFeedback(123L);
    	
    	assertTrue(result.isSuccess());
    	assertNotNull(competition.getAssessorFeedbackDate());
    	assertEquals("assessor feedback date is set to the start of the current second", 0, competition.getAssessorFeedbackDate().get(ChronoField.MILLI_OF_SECOND));
    }

    @Test
    public void testNotifyLeadApplicantsOfAssessorFeedback() {

        Competition competition = newCompetition().withId(111L).withAssessorFeedbackDate(LocalDateTime.of(2017, 5, 3, 0, 0)).build();

        Application fundedApplication1 = newApplication().withApplicationStatus(APPROVED).build();
        Application unfundedApplication2 = newApplication().withApplicationStatus(REJECTED).build();
        Application fundedApplication3 = newApplication().withApplicationStatus(APPROVED).build();

        User fundedApplication1LeadApplicant = newUser().build();
        User unfundedApplication2LeadApplicant = newUser().build();
        User fundedApplication3LeadApplicant = newUser().build();

        Role leadApplicantRole = newRole().with(id(456L)).build();

        List<ProcessRole> leadApplicantProcessRoles = newProcessRole().
                withUser(fundedApplication1LeadApplicant, unfundedApplication2LeadApplicant, fundedApplication3LeadApplicant).
                withApplication(fundedApplication1, unfundedApplication2, fundedApplication3).
                withRole(leadApplicantRole, leadApplicantRole, leadApplicantRole).
                build(3);

        UserNotificationTarget fundedApplication1LeadApplicantTarget = new UserNotificationTarget(fundedApplication1LeadApplicant);
        UserNotificationTarget fundedApplication3LeadApplicantTarget = new UserNotificationTarget(fundedApplication3LeadApplicant);

        Map<String, Object> expectedGlobalNotificationArguments = asMap(
                "competitionName", competition.getName(),
                "dashboardUrl", webBaseUrl,
                "feedbackDate", competition.getAssessorFeedbackDate());

        List<NotificationTarget> expectedFundedLeadApplicants = asList(fundedApplication1LeadApplicantTarget, fundedApplication3LeadApplicantTarget);

        Map<NotificationTarget, Map<String, Object>> expectedFundedNotificationTargetSpecificArguments = asMap(
                fundedApplication1LeadApplicantTarget, asMap(
                        "applicationName", fundedApplication1.getName(),
                        "applicationNumber", fundedApplication1.getFormattedId()
                ),
                fundedApplication3LeadApplicantTarget, asMap(
                        "applicationName", fundedApplication3.getName(),
                        "applicationNumber", fundedApplication3.getFormattedId())
        );

        Notification expectedFundedNotification = new Notification(systemNotificationSourceMock, expectedFundedLeadApplicants, APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                expectedGlobalNotificationArguments, expectedFundedNotificationTargetSpecificArguments);

        UserNotificationTarget unfundedApplication2LeadApplicantTarget = new UserNotificationTarget(unfundedApplication2LeadApplicant);
        List<NotificationTarget> expectedUnfundedLeadApplicants = singletonList(unfundedApplication2LeadApplicantTarget);

        Map<NotificationTarget, Map<String, Object>> expectedUnfundedNotificationTargetSpecificArguments = asMap(
                unfundedApplication2LeadApplicantTarget, asMap(
                        "applicationName", unfundedApplication2.getName(),
                        "applicationNumber", unfundedApplication2.getFormattedId())
        );

        Notification expectedUnfundedNotification = new Notification(systemNotificationSourceMock, expectedUnfundedLeadApplicants, APPLICATION_NOT_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED,
                expectedGlobalNotificationArguments, expectedUnfundedNotificationTargetSpecificArguments);

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationStatusIdIn(competition.getId(), FUNDING_DECISIONS_MADE_STATUS_IDS)).
                thenReturn(asList(fundedApplication1, unfundedApplication2, fundedApplication3));

        when(roleRepositoryMock.findOneByName(LEADAPPLICANT.getName())).thenReturn(leadApplicantRole);

        leadApplicantProcessRoles.forEach(processRole ->
                when(processRoleRepositoryMock.findByApplicationIdAndRoleId(processRole.getApplication().getId(), processRole.getRole().getId())).thenReturn(singletonList(processRole))
        );

        asList(fundedApplication1, unfundedApplication2, fundedApplication3).forEach(application ->
                when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application)
        );

        when(notificationServiceMock.sendNotification(createFullNotificationExpectations(expectedFundedNotification), eq(EMAIL))).thenReturn(serviceSuccess());
        when(notificationServiceMock.sendNotification(createFullNotificationExpectations(expectedUnfundedNotification), eq(EMAIL))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.notifyLeadApplicantsOfAssessorFeedback(competition.getId());
        assertTrue(result.isSuccess());

        verify(notificationServiceMock).sendNotification(createFullNotificationExpectations(expectedFundedNotification), eq(EMAIL));
        verify(notificationServiceMock).sendNotification(createFullNotificationExpectations(expectedUnfundedNotification), eq(EMAIL));
        verifyNoMoreInteractions(notificationServiceMock);
    }

    @Test
    public void testNotifyLeadApplicantsOfAssessorFeedbackAndJustLeadApplicants() {

        Competition competition = newCompetition().withId(111L).build();

        Application fundedApplication1 = newApplication().withApplicationStatus(APPROVED).build();
        Application unfundedApplication2 = newApplication().withApplicationStatus(REJECTED).build();

        // add some collaborators into the mix - they should not receive Notifications
        User fundedApplication1LeadApplicant = newUser().build();
        User fundedApplication1Collaborator = newUser().build();
        User unfundedApplication2LeadApplicant = newUser().build();
        User unfundedApplication2Collaborator = newUser().build();

        Role leadApplicantRole = newRole().with(id(456L)).build();
        Role collaboratorRole = newRole().with(id(789L)).build();

        List<ProcessRole> allProcessRoles = newProcessRole().
                withUser(fundedApplication1LeadApplicant, fundedApplication1Collaborator, unfundedApplication2LeadApplicant, unfundedApplication2Collaborator).
                withApplication(fundedApplication1, fundedApplication1, unfundedApplication2, unfundedApplication2).
                withRole(leadApplicantRole, collaboratorRole, leadApplicantRole, collaboratorRole).
                build(3);

        List<NotificationTarget> expectedFundedLeadApplicants = singletonList(new UserNotificationTarget(fundedApplication1LeadApplicant));
        Notification expectedFundedNotification =
                new Notification(systemNotificationSourceMock, expectedFundedLeadApplicants, APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED, emptyMap());

        List<NotificationTarget> expectedUnfundedLeadApplicants = singletonList(new UserNotificationTarget(unfundedApplication2LeadApplicant));
        Notification expectedUnfundedNotification = new Notification(systemNotificationSourceMock, expectedUnfundedLeadApplicants, APPLICATION_NOT_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED, emptyMap());

        when(competitionRepositoryMock.findOne(competition.getId())).thenReturn(competition);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationStatusIdIn(competition.getId(), FUNDING_DECISIONS_MADE_STATUS_IDS)).
                thenReturn(asList(fundedApplication1, unfundedApplication2));

        asList(fundedApplication1, unfundedApplication2).forEach(application ->
                when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application)
        );

        when(roleRepositoryMock.findOneByName(LEADAPPLICANT.getName())).thenReturn(leadApplicantRole);

        allProcessRoles.forEach(processRole ->
                when(processRoleRepositoryMock.findByApplicationIdAndRoleId(processRole.getApplication().getId(), processRole.getRole().getId())).thenReturn(singletonList(processRole))
        );

        when(notificationServiceMock.sendNotification(createSimpleNotificationExpectations(expectedFundedNotification), eq(EMAIL))).thenReturn(serviceSuccess());
        when(notificationServiceMock.sendNotification(createSimpleNotificationExpectations(expectedUnfundedNotification), eq(EMAIL))).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.notifyLeadApplicantsOfAssessorFeedback(competition.getId());
        assertTrue(result.isSuccess());

        verify(notificationServiceMock).sendNotification(createSimpleNotificationExpectations(expectedFundedNotification), eq(EMAIL));
        verify(notificationServiceMock).sendNotification(createSimpleNotificationExpectations(expectedUnfundedNotification), eq(EMAIL));
        verifyNoMoreInteractions(notificationServiceMock);
    }

    @Override
    protected AssessorFeedbackServiceImpl supplyServiceUnderTest() {
        AssessorFeedbackServiceImpl assessorFeedbackService = new AssessorFeedbackServiceImpl();
        ReflectionTestUtils.setField(assessorFeedbackService, "webBaseUrl", webBaseUrl);
        return assessorFeedbackService;
    }
}
