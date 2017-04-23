package org.innovateuk.ifs.project.grantofferletter;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.gol.resource.GOLState;
import org.innovateuk.ifs.project.grantofferletter.service.ProjectGrantOfferLetterRestService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectGrantOfferServiceImpl implements ProjectGrantOfferService {

    @Autowired
    private ProjectGrantOfferLetterRestService projectGrantOfferLetterRestService;

    @Override
    public Optional<ByteArrayResource> getSignedGrantOfferLetterFile(Long projectId) {
        return projectGrantOfferLetterRestService.getSignedGrantOfferLetterFile(projectId).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> sendGrantOfferLetter(Long projectId) {
        return projectGrantOfferLetterRestService.sendGrantOfferLetter(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isSendGrantOfferLetterAllowed(Long projectId) {
        return projectGrantOfferLetterRestService.isSendGrantOfferLetterAllowed(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isGrantOfferLetterAlreadySent(Long projectId) {
        return projectGrantOfferLetterRestService.isGrantOfferLetterAlreadySent(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<Void> approveOrRejectSignedGrantOfferLetter(Long projectId, ApprovalType approvalType) {
        return projectGrantOfferLetterRestService.approveOrRejectSignedGrantOfferLetter(projectId, approvalType).toServiceResult();
    }

    @Override
    public ServiceResult<Boolean> isSignedGrantOfferLetterApproved(Long projectId) {
        return projectGrantOfferLetterRestService.isSignedGrantOfferLetterApproved(projectId).toServiceResult();
    }

    @Override
    public ServiceResult<GOLState> getGrantOfferLetterWorkflowState(Long projectId) {
        return projectGrantOfferLetterRestService.getGrantOfferLetterWorkflowState(projectId).toServiceResult();
    }

}
