package org.innovateuk.ifs.project.grantofferletter;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

public interface ProjectGrantOfferService {

    Optional<ByteArrayResource> getSignedGrantOfferLetterFile(Long projectId);

    Optional<FileEntryResource> getSignedGrantOfferLetterFileDetails(Long projectId);

    Optional<ByteArrayResource> getGrantOfferFile(Long projectId);

    Optional<FileEntryResource> getGrantOfferFileDetails(Long projectId);

    ServiceResult<FileEntryResource> addSignedGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    ServiceResult<FileEntryResource> addGrantOfferLetter(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    ServiceResult<Void> removeGrantOfferLetter(Long projectId);

    ServiceResult<Void> removeSignedGrantOfferLetter(Long projectId);

    ServiceResult<Void> submitGrantOfferLetter(Long projectId);

    ServiceResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId);

    ServiceResult<Void> sendGrantOfferLetter(Long projectId);

    ServiceResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId);

    ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType);

    ServiceResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId);

    ServiceResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId);

    Optional<ByteArrayResource> getAdditionalContractFile(Long projectId);

    Optional<FileEntryResource> getAdditionalContractFileDetails(Long projectId);

    ServiceResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

}
