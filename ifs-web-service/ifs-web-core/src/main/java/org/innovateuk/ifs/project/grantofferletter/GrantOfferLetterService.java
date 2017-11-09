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

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<ByteArrayResource> getSignedGrantOfferLetterFile(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<FileEntryResource> getSignedGrantOfferLetterFileDetails(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<ByteArrayResource> getGrantOfferFile(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<FileEntryResource> getGrantOfferFileDetails(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> addSignedGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> addGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> removeGrantOfferLetter(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> removeSignedGrantOfferLetter(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> submitGrantOfferLetter(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> sendGrantOfferLetter(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<GrantOfferLetterState> getGrantOfferLetterWorkflowState(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<ByteArrayResource> getAdditionalContractFile(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<FileEntryResource> getAdditionalContractFileDetails(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

}
