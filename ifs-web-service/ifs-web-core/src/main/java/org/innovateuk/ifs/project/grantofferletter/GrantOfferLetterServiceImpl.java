package org.innovateuk.ifs.project.grantofferletter;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GOLState;
import org.innovateuk.ifs.project.grantofferletter.service.GrantOfferLetterRestService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service implementation for dealing with a project's grant offer functionality
 */
@Service
public class GrantOfferLetterServiceImpl implements GrantOfferLetterService {

    @Autowired
    private GrantOfferLetterRestService grantOfferLetterRestService;

    @Override
    public Optional<ByteArrayResource> getSignedGrantOfferLetterFile(Long projectId) {
        return grantOfferLetterRestService.getSignedGrantOfferLetterFile(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<FileEntryResource> getSignedGrantOfferLetterFileDetails(Long projectId) {
        return grantOfferLetterRestService.getSignedGrantOfferLetterFileDetails(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<ByteArrayResource> getGrantOfferFile(Long projectId) {
        return grantOfferLetterRestService.getGrantOfferFile(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<FileEntryResource> getGrantOfferFileDetails(Long projectId) {
        return grantOfferLetterRestService.getGrantOfferFileDetails(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<FileEntryResource> addSignedGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return grantOfferLetterRestService.addSignedGrantOfferLetterFile(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
    }

    @Override
    public ServiceResult<FileEntryResource> addGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return grantOfferLetterRestService.addGrantOfferLetterFile(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
    }

    @Override
    public ServiceResult<Void> removeGrantOfferLetter(Long projectId) {
        return grantOfferLetterRestService.removeGrantOfferLetter(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> removeSignedGrantOfferLetter(Long projectId) {
        return grantOfferLetterRestService.removeSignedGrantOfferLetter(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> submitGrantOfferLetter(Long projectId) {
        return grantOfferLetterRestService.submitGrantOfferLetter(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> sendGrantOfferLetter(Long projectId) {
        return grantOfferLetterRestService.sendGrantOfferLetter(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId) {
        return grantOfferLetterRestService.isSendGrantOfferLetterAllowed(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId) {
        return grantOfferLetterRestService.isGrantOfferLetterAlreadySent(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType) {
        return grantOfferLetterRestService.approveOrRejectSignedGrantOfferLetter(projectId, approvalType).toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId) {
        return grantOfferLetterRestService.isSignedGrantOfferLetterApproved(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId) {
        return grantOfferLetterRestService.getGrantOfferLetterWorkflowState(projectId).toServiceResult();
    }

    @Override
    public Optional<ByteArrayResource> getAdditionalContractFile(Long projectId) {
        return grantOfferLetterRestService.getAdditionalContractFile(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public Optional<FileEntryResource> getAdditionalContractFileDetails(Long projectId) {
        return grantOfferLetterRestService.getAdditionalContractFileDetails(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return grantOfferLetterRestService.addAdditionalContractFile(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
    }

}
