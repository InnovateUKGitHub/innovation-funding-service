package org.innovateuk.ifs.threads.attachments.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileHttpHeadersValidator;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.innovateuk.ifs.threads.attachments.mapper.AttachmentMapper;
import org.innovateuk.ifs.threads.attachments.repository.AttachmentRepository;
import org.innovateuk.threads.attachment.resource.AttachmentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileDownload;
import static org.innovateuk.ifs.file.controller.FileControllerUtils.handleFileUpload;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
@Transactional
public class ProjectFinanceAttachmentsServiceImpl implements ProjectFinanceAttachmentService {
    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    private AttachmentRepository attachmentsRepository;

    @Autowired
    @Qualifier("postAttachmentValidator")
    private FileHttpHeadersValidator fileValidator;

    @Autowired
    private AttachmentMapper mapper;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    @Override
    public ServiceResult<AttachmentResource> findOne(Long attachmentId) {
        return find(attachmentsRepository.findOne(attachmentId), notFoundError(AttachmentResource.class, attachmentId))
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    public ServiceResult<AttachmentResource> upload(String contentType, String contentLength, String originalFilename, HttpServletRequest request) {
        return handleFileUpload(contentType, contentLength, originalFilename, fileValidator, request, (fileAttributes, inputStreamSupplier)
                -> fileService.createFile(fileAttributes.toFileEntryResource(), inputStreamSupplier)
                .andOnSuccess(created -> save(new Attachment(loggedInUserSupplier.get(), created.getRight()))
                        .andOnSuccessReturn(mapper::mapToResource))).toServiceResult();
    }

    private ServiceResult<Attachment> save(Attachment attachment) {
        return ServiceResult.serviceSuccess(attachmentsRepository.save(attachment));
    }


    @Override
    public ServiceResult<Void> delete(Long attachmentId) {
        return ofNullable(mapper.mapIdToDomain(attachmentId))
                .map(attachment -> fileService.deleteFile(attachment.fileId()))
                .map(result -> result.andOnSuccessReturnVoid(deletedFile -> attachmentsRepository.delete(deletedFile.getId())))
                .orElse(ServiceResult.serviceFailure(notFoundError(AttachmentResource.class, attachmentId)));
    }

    @Override
    public ResponseEntity<Object> download(Long attachmentId) {
        return handleFileDownload(() -> findOne(attachmentId)
                .andOnSuccessReturn(a -> mapper.mapToDomain(a))
                .andOnSuccess(a -> fileEntryService.findOne(a.fileId()).andOnSuccess(this::getFileAndContents)));
    }


    private ServiceResult<FileAndContents> getFileAndContents(FileEntryResource fileEntry) {
        return fileService.getFileByFileEntryId(fileEntry.getId())
                .andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntry, inputStream));
    }
}
