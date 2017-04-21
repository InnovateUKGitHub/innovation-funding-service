package org.innovateuk.ifs.project.grantofferletter;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.grantofferletter.service.GrantOfferLetterRestService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrantOfferLetterServiceImpl implements GrantOfferLetterService {

    @Autowired
    private GrantOfferLetterRestService grantOfferLetterRestService;

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

}
