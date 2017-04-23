package org.innovateuk.ifs.project.grantofferletter.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.springframework.core.io.ByteArrayResource;

import java.util.Optional;

public interface ProjectGrantOfferLetterRestService {

    RestResult<Optional<ByteArrayResource>> getSignedGrantOfferLetterFile(Long projectId);

    RestResult<Void> sendGrantOfferLetter(Long projectId);

    RestResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId);

    RestResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId);

    RestResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType);

    RestResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId);

    RestResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId);
}
