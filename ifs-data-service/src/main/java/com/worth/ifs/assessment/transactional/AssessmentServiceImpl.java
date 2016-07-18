package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.mapper.AssessmentMapper;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.workflow.AssessmentWorkflowEventHandler;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.assessment.domain.AssessmentFeedback} data.
 */
@Service
public class AssessmentServiceImpl extends BaseTransactionalService implements AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentMapper assessmentMapper;

    @Autowired
    private AssessmentWorkflowEventHandler assessmentWorkflowEventHandler;

    @Override
    public ServiceResult<AssessmentResource> findById(final Long id) {
        return find(assessmentRepository.findOne(id), notFoundError(Assessment.class, id)).andOnSuccessReturn(assessmentMapper::mapToResource);
    }

    @Override
    public ServiceResult<Void> updateStatus(Long assessmentId, ProcessOutcome processOutcome) {
        return find(assessmentRepository.findOne(assessmentId),notFoundError(AssessmentRepository.class,assessmentId)).andOnSuccess(found -> {
            if (processOutcome.getOutcomeType().equals(AssessmentOutcomes.REJECT.getType())) {
                assessmentWorkflowEventHandler.rejectInvitation(found.getProcessRole().getId(), found.getProcessStatus(), processOutcome);
            }
            return serviceSuccess();
        }).andOnSuccessReturnVoid();
    }
}
