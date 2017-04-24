package org.innovateuk.ifs.project.grantofferletter.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

public interface ProjectGrantOfferLetterRestService {

    RestResult<Optional<ByteArrayResource>> getSignedGrantOfferLetterFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getSignedGrantOfferLetterFileDetails(Long projectId);

    RestResult<FileEntryResource> addSignedGrantOfferLetterFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<FileEntryResource> addGrantOfferLetterFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

    RestResult<Void> removeGrantOfferLetter(Long projectId);

    RestResult<Void> removeSignedGrantOfferLetter(Long projectId);

    RestResult<Optional<ByteArrayResource>> getGrantOfferFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getGrantOfferFileDetails(Long projectId);

    RestResult<Void> submitGrantOfferLetter(Long projectId);

    RestResult<Void> sendGrantOfferLetter(Long projectId);

    RestResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId);

    RestResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId);

    RestResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType);

    RestResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId);

    RestResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId);

    RestResult<Optional<ByteArrayResource>> getAdditionalContractFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getAdditionalContractFileDetails(Long projectId);

    RestResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

}
