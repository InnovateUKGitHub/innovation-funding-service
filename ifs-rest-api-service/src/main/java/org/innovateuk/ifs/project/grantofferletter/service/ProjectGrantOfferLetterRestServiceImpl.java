package org.innovateuk.ifs.project.grantofferletter.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectGrantOfferLetterRestServiceImpl extends BaseRestService implements ProjectGrantOfferLetterRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<Void> sendGrantOfferLetter(Long projectId) {
        return  postWithRestResult(projectRestURL + "/" + projectId + "/grant-offer/send", Void.class);
    }

    @Override
    public RestResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/is-send-grant-offer-letter-allowed", Boolean.class);
    }

    @Override
    public RestResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/is-grant-offer-letter-already-sent", Boolean.class);
    }

    @Override
    public RestResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/signed-grant-offer-letter/approval/" + approvalType, Void.class);
    }

    @Override
    public RestResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/signed-grant-offer-letter/approval", Boolean.class);
    }

    @Override
    public RestResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/grant-offer-letter/state", GOLState.class);
    }

    @Override
    public RestResult<Optional<ByteArrayResource>> getSignedGrantOfferLetterFile(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/signed-grant-offer", ByteArrayResource.class).toOptionalIfNotFound();
    }
}
