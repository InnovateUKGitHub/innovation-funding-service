package org.innovateuk.ifs.thread.attachment.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.innovateuk.ifs.threads.attachments.service.ProjectFinanceAttachmentsServiceImpl;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.io.InputStream;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class ProjectFinanceAttachmentServiceTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ProjectFinanceAttachmentsServiceImpl service;

    @Test
    public void test_findOne() throws Exception {
        Long attachmentId = 1L;
        Attachment attachment = new Attachment(attachmentId, newUser().withId(89L).build(), newFileEntry().build());
        AttachmentResource attachmentResource = new AttachmentResource(attachmentId, attachment.fileName(), attachment.mediaType(), attachment.sizeInBytes());
        when(attachmentRepositoryMock.findOne(attachmentId)).thenReturn(attachment);
        when(attachmentMapperMock.mapToResource(attachment)).thenReturn(attachmentResource);

        AttachmentResource response = service.findOne(attachmentId).getSuccessObjectOrThrowException();

        assertEquals(attachmentResource, response);
    }

    @Test
    public void test_downloadSuccessfulIfAttachmentExists() throws Exception {
        final Long attachmentId = 1L;
        final Long attachmentsFileEntryId = 101L;
        final FileEntry attachmentsFileEntry = new FileEntry(attachmentsFileEntryId, "name", APPLICATION_JSON,432 );
        final Attachment attachment = new Attachment(attachmentId, newUser().withId(89L).build(), attachmentsFileEntry);
        final AttachmentResource attachmentResource = new AttachmentResource(attachmentId, attachment.fileName(), attachment.mediaType(), attachment.sizeInBytes());

        when(attachmentRepositoryMock.findOne(attachmentId)).thenReturn(attachment);
        when(attachmentMapperMock.mapToResource(attachment)).thenReturn(attachmentResource);
        when(attachmentMapperMock.mapToDomain(attachmentResource)).thenReturn(attachment);
        when(fileEntryRepositoryMock.findOne(attachmentsFileEntryId)).thenReturn(attachmentsFileEntry);
        final FileEntryResource fileEntryResource = new FileEntryResource(attachment.fileId(), attachment.fileName(),
                attachment.mediaType(), attachment.sizeInBytes());

        when(fileEntryServiceMock.findOne(attachmentsFileEntryId))
                .thenReturn(ServiceResult.serviceSuccess(fileEntryResource));


        final Supplier<InputStream> contentSupplier = () -> null;
        FileAndContents fileAndContents = new BasicFileAndContents(fileEntryResource, contentSupplier);

        when(fileServiceMock.getFileByFileEntryId(attachment.fileId())).thenReturn(ServiceResult.serviceSuccess(contentSupplier));
        ServiceResult<FileAndContents> response = service.attachmentFileAndContents(attachmentId);

        assert(response.isSuccess());
        assertEquals(response.getSuccessObject().getFileEntry(), fileEntryResource);
        assertEquals(response.getSuccessObject().getContentsSupplier(), contentSupplier);
    }

    @Test
    public void test_downloadUnsuccessfulIfAttachmentDoesNotExist() throws Exception {
        final Long attachmentId = 1L;

        when(attachmentRepositoryMock.findOne(attachmentId)).thenReturn(null);
        ServiceResult<FileAndContents> response = service.attachmentFileAndContents(attachmentId);

        assertTrue(response.isFailure());
        assertTrue(response.getErrors().stream().allMatch(e -> e.equals(notFoundError(AttachmentResource.class, attachmentId))));
    }

    @Test
    public void test_upload() throws Exception {
        Long attachmentId = 1L;
        Attachment attachment = new Attachment(attachmentId, newUser().withId(89L).build(), newFileEntry().build());
        AttachmentResource attachmentResource = new AttachmentResource(attachmentId, attachment.fileName(), attachment.mediaType(), attachment.sizeInBytes());
        when(attachmentRepositoryMock.findOne(attachmentId)).thenReturn(attachment);
        when(attachmentMapperMock.mapToResource(attachment)).thenReturn(attachmentResource);

        AttachmentResource response = service.findOne(attachmentId).getSuccessObjectOrThrowException();

        assertEquals(attachmentResource, response);
    }

    @Test
    public void test_deleteIsSuccessfulIsAttachmentExists() throws Exception {
        final Long attachmentId = 1L;
        final Long attachmentsFileEntryId = 101L;
        final FileEntry attachmentsFileEntry = new FileEntry(attachmentsFileEntryId, "name", APPLICATION_JSON, 432);
        when(attachmentMapperMock.mapIdToDomain(attachmentId)).thenReturn(new Attachment(attachmentId, newUser().build(), attachmentsFileEntry));
        when(fileServiceMock.deleteFile(attachmentsFileEntryId)).thenReturn(ServiceResult.serviceSuccess(attachmentsFileEntry));
        ServiceResult<Void> response = service.delete(attachmentId);
        assertTrue(response.isSuccess());
    }

    @Test
    public void test_deleteIsUnsuccessfulIsAttachmentExists() throws Exception {
        final Long attachmentId = 1L;
        final Long attachmentsFileEntryId = 101L;
        final FileEntry attachmentsFileEntry = new FileEntry(attachmentsFileEntryId, "name", APPLICATION_JSON, 432);
        when(attachmentMapperMock.mapIdToDomain(attachmentId))
                .thenReturn(new Attachment(attachmentId, newUser().build(), attachmentsFileEntry));
        when(fileServiceMock.deleteFile(attachmentsFileEntryId))
                .thenReturn(ServiceResult.serviceFailure(notFoundError(FileEntry.class, attachmentsFileEntryId)));
        ServiceResult<Void> response = service.delete(attachmentId);
        assertTrue(response.isFailure()
                        && response.getErrors().stream().allMatch(e -> e.getStatusCode().equals(NOT_FOUND)));
    }

}
