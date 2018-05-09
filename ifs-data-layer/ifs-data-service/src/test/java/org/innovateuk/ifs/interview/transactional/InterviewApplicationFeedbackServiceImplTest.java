package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentMessageOutcome;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentMessageOutcomeRepository;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentMessageOutcomeBuilder.newInterviewAssignmentMessageOutcome;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InterviewApplicationFeedbackServiceImplTest extends BaseServiceUnitTest<InterviewApplicationFeedbackServiceImpl> {

    @Mock
    private InterviewAssignmentRepository interviewAssignmentRepositoryMock;

    @Mock
    private InterviewAssignmentMessageOutcomeRepository interviewAssignmentMessageOutcomeRepository;

    @Mock
    private FileService fileServiceMock;

    @Mock
    private FileEntryService fileEntryServiceMock;

    @Override
    protected InterviewApplicationFeedbackServiceImpl supplyServiceUnderTest() {
        return new InterviewApplicationFeedbackServiceImpl();
    }

    @Test
    public void findFeedback() throws Exception {
        long applicationId = 1L;
        FileEntry fileEntry = newFileEntry().build();
        InterviewAssignment interviewAssignment = newInterviewAssignment().
                withMessage(newInterviewAssignmentMessageOutcome()
                        .withFeedback(fileEntry)
                        .build()
                ).build();
        FileEntryResource fileEntryResource = newFileEntryResource().build();
        when(interviewAssignmentRepositoryMock.findOneByTargetId(applicationId)).thenReturn(interviewAssignment);
        when(fileEntryServiceMock.findOne(fileEntry.getId())).thenReturn(serviceSuccess(fileEntryResource));

        FileEntryResource response = service.findFeedback(applicationId).getSuccess();

        assertEquals(fileEntryResource, response);
    }

    @Test
    public void downloadFeedback() throws Exception {
        final long applicationId = 1L;
        final long fileId = 2L;
        final FileEntry fileEntry = new FileEntry(fileId, "somefile.pdf", MediaType.APPLICATION_PDF, 1111L);
        InterviewAssignment interviewAssignment = newInterviewAssignment().
                withMessage(newInterviewAssignmentMessageOutcome()
                        .withFeedback(fileEntry)
                        .build()
                ).build();
        FileEntryResource fileEntryResource = new FileEntryResource();
        fileEntryResource.setId(fileId);

        when(interviewAssignmentRepositoryMock.findOneByTargetId(applicationId)).thenReturn(interviewAssignment);
        when(fileEntryServiceMock.findOne(fileId)).thenReturn(serviceSuccess(fileEntryResource));
        final Supplier<InputStream> contentSupplier = () -> null;
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(ServiceResult.serviceSuccess(contentSupplier));

        FileAndContents fileAndContents = service.downloadFeedback(applicationId).getSuccess();

        assertEquals(fileAndContents.getContentsSupplier(), contentSupplier);
        assertEquals(fileAndContents.getFileEntry(), fileEntryResource);
    }

    @Test
    public void deleteFeedback() throws Exception {
        final long applicationId = 1L;
        final long fileId = 101L;
        final FileEntry fileEntry = new FileEntry(fileId, "somefile.pdf", MediaType.APPLICATION_PDF, 1111L);
        InterviewAssignmentMessageOutcome messageOutcome = newInterviewAssignmentMessageOutcome()
                .withId(2L)
                .withFeedback(fileEntry)
                .build();
        InterviewAssignment interviewAssignment = newInterviewAssignment()
                .withMessage(messageOutcome)
                .build();

        when(interviewAssignmentRepositoryMock.findOneByTargetId(applicationId)).thenReturn(interviewAssignment);
        when(fileServiceMock.deleteFileIgnoreNotFound(fileId)).thenReturn(ServiceResult.serviceSuccess(fileEntry));

        ServiceResult<Void> response = service.deleteFeedback(applicationId);

        assertTrue(response.isSuccess());
        verify(interviewAssignmentMessageOutcomeRepository).delete(messageOutcome.getId());
        verify(fileServiceMock).deleteFileIgnoreNotFound(fileId);
    }
}