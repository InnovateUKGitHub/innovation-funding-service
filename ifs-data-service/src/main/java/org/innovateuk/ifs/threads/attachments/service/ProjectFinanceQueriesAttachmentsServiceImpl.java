package org.innovateuk.ifs.threads.attachments.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.innovateuk.ifs.threads.attachments.repository.PostAttachmentRepository;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileDownload;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileUpload;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;


public class ProjectFinanceQueriesAttachmentsServiceImpl implements ProjectFinanceQueriesAttachmentService {
    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    private PostAttachmentRepository attachmentsRepository;

    @Autowired
    @Qualifier("postAttachmentValidator")
    private FileHttpHeadersValidator fileValidator;

    @Override
    public ServiceResult<Attachment> findOne(Long attachmentId) {
        return find(attachmentsRepository.findOne(attachmentId), notFoundError(Attachment.class, attachmentId));
    }

    @Override
    public ServiceResult<Attachment> upload(String contentType, String contentLength, String originalFilename, HttpServletRequest request) {
        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier) ->
                fileService.createFile(fileAttributes.toFileEntryResource(), inputStreamSupplier)
                        .andOnSuccessReturn(created -> save(newAttachment(null, created.getRight())))); //TODO Nuno: add user
    }

    private ServiceResult<Attachment> save(Attachment attachment) {
        return ServiceResult.serviceSuccess(attachmentsRepository.save(attachment));
    }

    private Attachment newAttachment(User uploader, FileEntry fileEntry) {
        return new Attachment(uploader, fileEntry);
    }

    @Override
    public ServiceResult<Void> delete(Long attachmentId) {
        return findOne(attachmentId)
                .andOnSuccess(attachment -> fileService.deleteFile(attachment.fileEntry().getId())
                        .andOnSuccessReturnVoid(c -> attachmentsRepository.delete(attachmentId)));
    }

    @Override
    public ResponseEntity<Object> download(Long attachmentId) throws IOException {
        return handleFileDownload(() -> findOne(attachmentId)
                .andOnSuccess(a -> fileEntryService.findOne(a.fileEntry().getId())
                        .andOnSuccess(fileEntry -> getFileAndContents(fileEntry))));
    }


    private ServiceResult<FileAndContents> getFileAndContents(FileEntryResource fileEntry) {
        return fileService.getFileByFileEntryId(fileEntry.getId())
                .andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntry, inputStream));
    }
}
