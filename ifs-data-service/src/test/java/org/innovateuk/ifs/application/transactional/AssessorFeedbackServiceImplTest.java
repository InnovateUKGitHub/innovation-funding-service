package org.innovateuk.ifs.application.transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.constant.ApplicationStatusConstants.APPROVED;
import static org.innovateuk.ifs.application.constant.ApplicationStatusConstants.REJECTED;
import static org.innovateuk.ifs.application.transactional.ApplicationFundingServiceImplMockTest.createNotificationExpectationsWithGlobalArgs;
import static org.innovateuk.ifs.application.transactional.ApplicationFundingServiceImplMockTest.createSimpleNotificationExpectations;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.FUNDING_DECISIONS_MADE_STATUS_IDS;
import static org.innovateuk.ifs.application.transactional.AssessorFeedbackServiceImpl.Notifications.APPLICATION_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED;
import static org.innovateuk.ifs.application.transactional.AssessorFeedbackServiceImpl.Notifications.APPLICATION_NOT_FUNDED_ASSESSOR_FEEDBACK_PUBLISHED;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
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

    @Override
    protected AssessorFeedbackServiceImpl supplyServiceUnderTest() {
        AssessorFeedbackServiceImpl assessorFeedbackService = new AssessorFeedbackServiceImpl();
        ReflectionTestUtils.setField(assessorFeedbackService, "webBaseUrl", webBaseUrl);
        return assessorFeedbackService;
    }
}
