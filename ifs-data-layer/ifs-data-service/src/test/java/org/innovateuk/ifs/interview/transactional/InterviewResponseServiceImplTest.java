package org.innovateuk.ifs.interview.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.workflow.configuration.InterviewAssignmentWorkflowHandler;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentResponseOutcomeBuilder.newInterviewAssignmentResponseOutcome;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InterviewResponseServiceImplTest extends BaseServiceUnitTest<InterviewResponseServiceImpl> {

    @Mock
    private InterviewAssignmentRepository interviewAssignmentRepositoryMock;

    @Mock
    private FileEntryService fileEntryServiceMock;

    @Mock
    private InterviewAssignmentWorkflowHandler interviewAssignmentWorkflowHandler;

    @Override
    protected InterviewResponseServiceImpl supplyServiceUnderTest() {
        return new InterviewResponseServiceImpl();
    }

    @Test
    public void findResponse() throws Exception {
        long applicationId = 1L;
        FileEntry fileEntry = newFileEntry().build();
        InterviewAssignment interviewAssignment = newInterviewAssignment().
                withResponse(newInterviewAssignmentResponseOutcome()
                        .withFileResponse(fileEntry)
                        .build()
                ).build();
        FileEntryResource fileEntryResource = newFileEntryResource().build();
        when(interviewAssignmentRepositoryMock.findOneByTargetId(applicationId)).thenReturn(interviewAssignment);
        when(fileEntryServiceMock.findOne(fileEntry.getId())).thenReturn(serviceSuccess(fileEntryResource));

        FileEntryResource response = service.findResponse(applicationId).getSuccess();

        assertEquals(fileEntryResource, response);
    }

    @Test
    public void downloadResponse() throws Exception {
        final long applicationId = 1L;
        final long fileId = 2L;
        final FileEntry fileEntry = new FileEntry(fileId, "somefile.pdf", MediaType.APPLICATION_PDF, 1111L);
        InterviewAssignment interviewAssignment = newInterviewAssignment().
                withResponse(newInterviewAssignmentResponseOutcome()
                        .withFileResponse(fileEntry)
                        .build()
                ).build();
        FileEntryResource fileEntryResource = new FileEntryResource();
        fileEntryResource.setId(fileId);

        when(interviewAssignmentRepositoryMock.findOneByTargetId(applicationId)).thenReturn(interviewAssignment);
        when(fileEntryServiceMock.findOne(fileId)).thenReturn(serviceSuccess(fileEntryResource));
        final Supplier<InputStream> contentSupplier = () -> null;
        when(fileServiceMock.getFileByFileEntryId(fileEntry.getId())).thenReturn(ServiceResult.serviceSuccess(contentSupplier));

        FileAndContents fileAndContents = service.downloadResponse(applicationId).getSuccess();

        assertEquals(fileAndContents.getContentsSupplier(), contentSupplier);
        assertEquals(fileAndContents.getFileEntry(), fileEntryResource);
    }

    @Test
    public void deleteResponse() throws Exception {
        final long applicationId = 1L;
        InterviewAssignment interviewAssignment = newInterviewAssignment()
                .build();

        when(interviewAssignmentRepositoryMock.findOneByTargetId(applicationId)).thenReturn(interviewAssignment);
        when(interviewAssignmentWorkflowHandler.withdrawResponse(interviewAssignment)).thenReturn(true);

        ServiceResult<Void> response = service.deleteResponse(applicationId);

        assertTrue(response.isSuccess());
        verify(interviewAssignmentWorkflowHandler).withdrawResponse(interviewAssignment);
    }
}