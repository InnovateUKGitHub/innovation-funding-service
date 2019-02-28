package org.innovateuk.ifs.granttransfer.transactional;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.transactional.FileEntryService;
import org.innovateuk.ifs.file.transactional.FileService;
import org.innovateuk.ifs.granttransfer.domain.EuGrantTransfer;
import org.innovateuk.ifs.granttransfer.mapper.EuGrantTransferMapper;
import org.innovateuk.ifs.granttransfer.repository.EuGrantTransferRepository;
import org.innovateuk.ifs.granttransfer.resource.EuGrantTransferResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

@Service
@Transactional(readOnly = true)
public class EuGrantTransferServiceImpl implements EuGrantTransferService {

    @Value("${ifs.data.service.file.storage.eu.grant.transfer.agreement.max.filesize.bytes}")
    private Long maxFileSize;

    @Value("${ifs.data.service.file.storage.eu.grant.transfer.agreement.valid.media.types}")
    private List<String> validMediaTypes;

    @Autowired
    private EuGrantTransferRepository euGrantTransferRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileEntryService fileEntryService;

    @Autowired
    @Qualifier("mediaTypeStringsFileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    @Autowired
    private EuGrantTransferMapper mapper;

    private FileControllerUtils fileControllerUtils = new FileControllerUtils();

    @Override
    @Transactional
    public ServiceResult<Void> uploadGrantAgreement(String contentType, String contentLength, String originalFilename, long applicationId, HttpServletRequest request) {
        return findGrantTransferByApplicationIdCreateIfNotExists(applicationId).andOnSuccess(grantTransfer ->
                fileControllerUtils.handleFileUpload(contentType, contentLength, originalFilename, fileValidator, validMediaTypes, maxFileSize, request,
                        (fileAttributes, inputStreamSupplier) -> fileService.createFile(fileAttributes.toFileEntryResource(), inputStreamSupplier)
                                .andOnSuccessReturnVoid(created ->
                                        grantTransfer.setGrantAgreement(created.getRight())
                                )).toServiceResult());
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteGrantAgreement(long applicationId) {
        return findGrantTransferByApplicationId(applicationId).andOnSuccess(grantTransfer -> {
            long fileId = grantTransfer.getGrantAgreement().getId();
            return fileService.deleteFileIgnoreNotFound(fileId).andOnSuccessReturnVoid(() ->
                    grantTransfer.setGrantAgreement(null)
            );
        });
    }

    @Override
    public ServiceResult<FileAndContents> downloadGrantAgreement(long applicationId) {
        return findGrantTransferByApplicationId(applicationId).andOnSuccess(grantTransfer ->
                fileEntryService.findOne(grantTransfer.getGrantAgreement().getId())
                        .andOnSuccess(this::getFileAndContents));
    }

    @Override
    public ServiceResult<FileEntryResource> findGrantAgreement(long applicationId) {
        return findGrantTransferByApplicationId(applicationId).andOnSuccess(grantTransfer ->
                ofNullable(grantTransfer.getGrantAgreement())
                        .map(FileEntry::getId)
                        .map(fileEntryService::findOne)
                        .orElse(serviceFailure(notFoundError(FileEntryResource.class, applicationId))));
    }

    @Override
    public ServiceResult<EuGrantTransferResource> getGrantTransferByApplicationId(long applicationId) {
        return findGrantTransferByApplicationId(applicationId).andOnSuccessReturn(mapper::mapToResource);
    }

    @Override
    @Transactional
    public ServiceResult<Void> updateGrantTransferByApplicationId(EuGrantTransferResource euGrantTransferResource, long applicationId) {
        return findGrantTransferByApplicationIdCreateIfNotExists(applicationId).andOnSuccessReturnVoid(domain -> {
            domain.setGrantAgreementNumber(euGrantTransferResource.getGrantAgreementNumber());
            domain.setParticipantId(euGrantTransferResource.getParticipantId());
            domain.setProjectStartDate(euGrantTransferResource.getProjectStartDate());
            domain.setProjectEndDate(euGrantTransferResource.getProjectEndDate());
            domain.setFundingContribution(euGrantTransferResource.getFundingContribution());
            domain.setProjectCoordinator(euGrantTransferResource.isProjectCoordinator());

            //TODO action type

            domain.getApplication().setName(euGrantTransferResource.getProjectName());
        });
    }

    private ServiceResult<FileAndContents> getFileAndContents(FileEntryResource fileEntry) {
        return fileService.getFileByFileEntryId(fileEntry.getId())
                .andOnSuccessReturn(inputStream -> new BasicFileAndContents(fileEntry, inputStream));
    }

    private ServiceResult<EuGrantTransfer> findGrantTransferByApplicationIdCreateIfNotExists(long applicationId) {
        EuGrantTransfer grantTransfer = euGrantTransferRepository.findByApplicationId(applicationId);
        if (grantTransfer == null) {
            grantTransfer = new EuGrantTransfer();
            Application application = new Application();
            application.setId(applicationId);
            grantTransfer.setApplication(application);
            grantTransfer = euGrantTransferRepository.save(grantTransfer);
        }
        return serviceSuccess(grantTransfer);

    }

    private ServiceResult<EuGrantTransfer> findGrantTransferByApplicationId(long applicationId) {
        return find(euGrantTransferRepository.findByApplicationId(applicationId), notFoundError(EuGrantTransfer.class, applicationId));
    }
}
