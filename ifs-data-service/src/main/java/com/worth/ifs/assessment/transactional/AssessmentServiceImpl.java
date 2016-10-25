package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.mapper.AssessmentMapper;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.ApplicationRejectionResource;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_RECOMMENDATION_FAILED;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REJECTION_FAILED;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.assessment.domain.Assessment} data.
 */
@Service
public class AssessmentServiceImpl extends BaseTransactionalService implements AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentMapper assessmentMapper;

    @Autowired
    private AssessmentWorkflowHandler assessmentWorkflowService;

    @Override
    public ServiceResult<AssessmentResource> findById(Long id) {
        return find(assessmentRepository.findOne(id), notFoundError(Assessment.class, id)).andOnSuccessReturn(assessmentMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<AssessmentResource>> findByUserAndCompetition(Long userId, Long competitionId) {
        return serviceSuccess(simpleMap(assessmentRepository.findByParticipantUserIdAndParticipantApplicationCompetitionId(userId, competitionId), assessmentMapper::mapToResource));
    }

    @Override
    public ServiceResult<Void> recommend(Long assessmentId, AssessmentFundingDecisionResource assessmentFundingDecision) {
        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowService.recommend(found.getParticipant().getId(), found, assessmentFundingDecision)) {
                return serviceFailure(new Error(ASSESSMENT_RECOMMENDATION_FAILED));
            }
            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<Void> rejectInvitation(Long assessmentId, ApplicationRejectionResource applicationRejection) {
        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowService.rejectInvitation(found.getParticipant().getId(), found, applicationRejection)) {
                return serviceFailure(new Error(ASSESSMENT_REJECTION_FAILED));
            }
            return serviceSuccess();
        });
    }
}
