package org.innovateuk.ifs.threads.attachments.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.threads.attachment.resource.AttachmentResource;
import org.innovateuk.ifs.threads.attachments.domain.Attachment;
import org.innovateuk.ifs.threads.attachments.mapper.AttachmentMapper;
import org.innovateuk.ifs.threads.attachments.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * This class contains methods to upload, find, download and delete {@link AttachmentResource}s
 * under the context of Project Finance Threads, i.e., Queries or Notes.
 */
@Service
@Transactional(readOnly = true)
public class ProjectFinanceAttachmentsServiceImpl implements ProjectFinanceAttachmentService {

    @Value("${ifs.data.service.file.storage.projectfinance.threadsattachments.max.filesize.bytes}")
    private Long maxFilesizeBytesForProjectFinanceThreadsAttachments;

    @Value("${ifs.data.service.file.storage.projectfinance.threadsattachments.valid.media.types.internal}")
    private List<String> validMediaTypesForProjectFinanceThreadsAttachmentsInternal;

    @Value("${ifs.data.service.file.storage.projectfinance.threadsattachments.valid.media.types.external}")
    private List<String> validMediaTypesForProjectFinanceThreadsAttachmentsExternal;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    private AttachmentRepository attachmentsRepository;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    @Autowired
    private AttachmentMapper mapper;

    @Autowired
    private LoggedInUserSupplier loggedInUserSupplier;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @Override
    public ServiceResult<AttachmentResource> findOne(Long attachmentId) {
        return find(attachmentsRepository.findById(attachmentId), notFoundError(AttachmentResource.class, attachmentId))
                .andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<AttachmentResource> upload(String contentType, String contentLength, String originalFilename,
                                                    Long projectId, HttpServletRequest request) {

        if (loggedInUserSupplier.get().isInternalUser()) {

            return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypesForProjectFinanceThreadsAttachmentsInternal, maxFilesizeBytesForProjectFinanceThreadsAttachments, request,
                    (fileAttributes, inputStreamSupplier) -> fileService.createFile(fileAttributes.toFileEntryResource(), inputStreamSupplier)
                            .andOnSuccess(created -> save(new Attachment(loggedInUserSupplier.get(), created.getRight(), now()))
                                    .andOnSuccessReturn(mapper::mapToResource))).toServiceResult();
        }

        return fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypesForProjectFinanceThreadsAttachmentsExternal, maxFilesizeBytesForProjectFinanceThreadsAttachments, request,
                (fileAttributes, inputStreamSupplier) -> fileService.createFile(fileAttributes.toFileEntryResource(), inputStreamSupplier)
                        .andOnSuccess(created -> save(new Attachment(loggedInUserSupplier.get(), created.getRight(), now()))
                                .andOnSuccessReturn(mapper::mapToResource))).toServiceResult();
    }

    private ServiceResult<Attachment> save(Attachment attachment) {
        return ServiceResult.serviceSuccess(attachmentsRepository.save(attachment));
    }


    @Override
    @Transactional
    public ServiceResult<Void> delete(Long attachmentId) {
        return ofNullable(mapper.mapIdToDomain(attachmentId))
                .map(attachment -> {
                    attachmentsRepository.deleteById(attachment.id());
                    return fileService.deleteFileIgnoreNotFound(attachment.fileId()).andOnSuccessReturnVoid();
                }).orElse(ServiceResult.serviceFailure(notFoundError(AttachmentResource.class, attachmentId)));
    }

    @Override
    public ServiceResult<FileAndContents> attachmentFileAndContents(Long attachmentId) {
        return findOne(attachmentId)
                .andOnSuccessReturn(a -> mapper.mapToDomain(a))
                .andOnSuccess(a -> fileEntryService.findOne(a.fileId()).andOnSuccess(this::getFileAndContents));
    }


    private ServiceResult<FileAndContents> getFileAndContents(FileEntryResource fileEntry) {
        return fileService.getFileByFileEntryId(fileEntry.getId())
                .andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntry, inputStream));
    }
}
