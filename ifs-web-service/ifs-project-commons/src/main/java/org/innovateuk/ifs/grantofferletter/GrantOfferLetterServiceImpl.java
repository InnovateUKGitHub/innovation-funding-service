package org.innovateuk.ifs.grantofferletter;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.grantofferletter.service.GrantOfferLetterRestService;
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
        return grantOfferLetterRestService.getSignedGrantOfferLetterFile(projectId).getSuccess();
    }

    @Override
    public Optional<FileEntryResource> getSignedGrantOfferLetterFileDetails(Long projectId) {
        return grantOfferLetterRestService.getSignedGrantOfferLetterFileDetails(projectId).getSuccess();
    }

    @Override
    public Optional<ByteArrayResource> getGrantOfferFile(Long projectId) {
        return grantOfferLetterRestService.getGrantOfferFile(projectId).getSuccess();
    }

    @Override
    public Optional<FileEntryResource> getGrantOfferFileDetails(Long projectId) {
        return grantOfferLetterRestService.getGrantOfferFileDetails(projectId).getSuccess();
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
    public ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, GrantOfferLetterApprovalResource grantOfferLetterApprovalResource) {
        return grantOfferLetterRestService.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource).toServiceResult();
    }

    @Override
    public ServiceResult<GrantOfferLetterStateResource> getGrantOfferLetterState(Long projectId) {
        return grantOfferLetterRestService.getGrantOfferLetterState(projectId).toServiceResult();
    }

    @Override
    public Optional<ByteArrayResource> getAdditionalContractFile(Long projectId) {
        return grantOfferLetterRestService.getAdditionalContractFile(projectId).getSuccess();
    }

    @Override
    public Optional<FileEntryResource> getAdditionalContractFileDetails(Long projectId) {
        return grantOfferLetterRestService.getAdditionalContractFileDetails(projectId).getSuccess();
    }

    @Override
    public ServiceResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes) {
        return grantOfferLetterRestService.addAdditionalContractFile(projectId, contentType, fileSize, originalFilename, bytes).toServiceResult();
    }

}
