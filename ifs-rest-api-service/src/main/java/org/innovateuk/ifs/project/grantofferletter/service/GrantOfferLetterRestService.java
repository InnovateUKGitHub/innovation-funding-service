package org.innovateuk.ifs.project.grantofferletter.service;

import org.innovateuk.ifs.commons.ZeroDowntime;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

/**
 * This Service calls the REST endpoint request mapping of the controller in ifs data service layer 'GrantOfferLetterController'
 * for Grant Offer Letter activity.
 */
public interface GrantOfferLetterRestService {

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

    @ZeroDowntime(reference = "IFS-2579", description = "Remove in Sprint 19 - replaced with usage of getGrantOfferLetterState()")
    RestResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId);

    @ZeroDowntime(reference = "IFS-2579", description = "Remove in Sprint 19 - replaced with usage of getGrantOfferLetterState()")
    RestResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId);

    RestResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType);

    @ZeroDowntime(reference = "IFS-2579", description = "Remove in Sprint 19 - replaced with usage of getGrantOfferLetterState()")
    RestResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId);

    @ZeroDowntime(reference = "IFS-2579", description = "Remove in Sprint 19 - replaced with usage of getGrantOfferLetterState()")
    RestResult<Boolean> isSignedGrantOfferLetterRejected(Long projectId);

    RestResult<GrantOfferLetterState> getGrantOfferLetterWorkflowState(Long projectId);

    RestResult<GrantOfferLetterStateResource> getGrantOfferLetterState(Long projectId);

    RestResult<Optional<ByteArrayResource>> getAdditionalContractFile(Long projectId);

    RestResult<Optional<FileEntryResource>> getAdditionalContractFileDetails(Long projectId);

    RestResult<FileEntryResource> addAdditionalContractFile(Long projectId, String contentType, long fileSize, String originalFilename, byte[] bytes);

}
