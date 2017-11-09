package org.innovateuk.ifs.project.grantofferletter;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

/**
 * A service for dealing with a project's grant offer functionality
 */
public interface GrantOfferLetterService {

    @NotSecured("Not currently secured")
    Optional<ByteArrayResource> getSignedGrantOfferLetterFile(Long projectId);

    @NotSecured("Not currently secured")
    Optional<FileEntryResource> getSignedGrantOfferLetterFileDetails(Long projectId);

    @NotSecured("Not currently secured")
    Optional<ByteArrayResource> getGrantOfferFile(Long projectId);

    @NotSecured("Not currently secured")
    Optional<FileEntryResource> getGrantOfferFileDetails(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<FileEntryResource> addSignedGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    @NotSecured("Not currently secured")
    ServiceResult<FileEntryResource> addGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    @NotSecured("Not currently secured")
    ServiceResult<Void> removeGrantOfferLetter(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> removeSignedGrantOfferLetter(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> submitGrantOfferLetter(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> sendGrantOfferLetter(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType);

    @NotSecured("Not currently secured")
    ServiceResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<GrantOfferLetterState> getGrantOfferLetterWorkflowState(Long projectId);

    @NotSecured("Not currently secured")
    Optional<ByteArrayResource> getAdditionalContractFile(Long projectId);

    @NotSecured("Not currently secured")
    Optional<FileEntryResource> getAdditionalContractFileDetails(Long projectId);

    @NotSecured("Not currently secured")
    ServiceResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

}
