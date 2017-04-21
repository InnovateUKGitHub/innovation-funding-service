package org.innovateuk.ifs.project.grantofferletter.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.ApprovalType;

public interface GrantOfferLetterRestService {

    RestResult<Void> sendGrantOfferLetter(Long projectId);

    RestResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId);

    RestResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId);

    RestResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType);

    RestResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId);

    RestResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId);
}
