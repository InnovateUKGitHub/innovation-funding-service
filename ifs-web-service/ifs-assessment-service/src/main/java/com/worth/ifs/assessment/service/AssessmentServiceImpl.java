package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.Optional.ofNullable;

/**
 * This class contains methods to retrieve and store {@link com.worth.ifs.assessment.resource.AssessmentResource} related data,
 * through the RestService {@link AssessmentRestService}.
 */
@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Override
    public AssessmentResource getById(final Long id) {
        return assessmentRestService.getById(id).getSuccessObjectOrThrowException();
    }

    @Override
    public ServiceResult<Void> recommend(Long assessmentId, Boolean fundingConfirmation, String feedback, String comment) {
        ProcessOutcomeResource processOutcome = new ProcessOutcomeResource();
        processOutcome.setOutcomeType(AssessmentOutcomes.RECOMMEND.getType());
        processOutcome.setOutcome(ofNullable(fundingConfirmation).map(BooleanUtils::toStringYesNo).orElse(null));
        processOutcome.setComment(comment);
        processOutcome.setDescription(feedback);

        return assessmentRestService.recommend(assessmentId, processOutcome).toServiceResult();
    }

    @Override
    public ServiceResult<Void> rejectInvitation(Long assessmentId, String reason, String comment) {
        ProcessOutcomeResource processOutcome = new ProcessOutcomeResource();
        processOutcome.setOutcomeType(AssessmentOutcomes.REJECT.getType());
        processOutcome.setComment(comment);
        processOutcome.setDescription(reason);

        return assessmentRestService.rejectInvitation(assessmentId, processOutcome).toServiceResult();
    }
}
