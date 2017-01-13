package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.mapper.AssessmentMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static java.util.stream.Collectors.toList;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.assessment.domain.Assessment} data.
 */
@Service
public class AssessmentServiceImpl extends BaseTransactionalService implements AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private AssessmentMapper assessmentMapper;

    @Autowired
    private AssessmentWorkflowHandler assessmentWorkflowHandler;

    @Override
    public ServiceResult<AssessmentResource> findById(Long id) {
        return find(assessmentRepository.findOne(id), notFoundError(Assessment.class, id)).andOnSuccessReturn(assessmentMapper::mapToResource);
    }

    @Override
    public ServiceResult<AssessmentResource> findAssignableById(Long id) {
        return find(assessmentRepository.findOne(id), notFoundError(Assessment.class, id)).andOnSuccessReturn(assessmentMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<AssessmentResource>> findByUserAndCompetition(Long userId, Long competitionId) {
        return serviceSuccess(simpleMap(assessmentRepository.findByParticipantUserIdAndParticipantApplicationCompetitionIdOrderByActivityStateStateAscIdAsc(userId, competitionId), assessmentMapper::mapToResource));
    }

    @Override
    public ServiceResult<AssessmentTotalScoreResource> getTotalScore(Long assessmentId) {
        return serviceSuccess(assessmentRepository.getTotalScore(assessmentId));
    }

    @Override
    public ServiceResult<Void> recommend(Long assessmentId, AssessmentFundingDecisionResource assessmentFundingDecision) {
        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowHandler.fundingDecision(found, assessmentFundingDecision)) {
                return serviceFailure(new Error(ASSESSMENT_RECOMMENDATION_FAILED));
            }
            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<Void> notify(Long assessmentId) {
        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowHandler.notify(found)) {
                return serviceFailure(new Error(ASSESSMENT_NOTIFY_FAILED));
            }
            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<Void> rejectInvitation(Long assessmentId, ApplicationRejectionResource applicationRejection) {
        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowHandler.rejectInvitation(found, applicationRejection)) {
                return serviceFailure(new Error(ASSESSMENT_REJECTION_FAILED));
            }
            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<Void> withdrawAssessment(Long assessmentId) {
        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowHandler.withdrawAssessment(found)) {
                return serviceFailure(new Error(ASSESSMENT_WITHDRAWN_FAILED));
            }
            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<Void> acceptInvitation(Long assessmentId) {
        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowHandler.acceptInvitation(found)) {
                return serviceFailure(new Error(ASSESSMENT_ACCEPT_FAILED));
            }
            return serviceSuccess();
        });
    }

    @Override
    public ServiceResult<Void> submitAssessments(AssessmentSubmissionsResource assessmentSubmissionsResource) {
        List<Assessment> assessments = assessmentRepository.findAll(assessmentSubmissionsResource.getAssessmentIds());
        List<Error> failures = new ArrayList<>();
        Set<Long> foundAssessmentIds = new HashSet<>();

        assessments.forEach(assessment -> {
            foundAssessmentIds.add(assessment.getId());

            if (!assessmentWorkflowHandler.submit(assessment) || !assessment.isInState(AssessmentStates.SUBMITTED)) {
                failures.add(new Error(ASSESSMENT_SUBMIT_FAILED, assessment.getId(), assessment.getTarget().getName()));
            }
        });

        failures.addAll(
                assessmentSubmissionsResource.getAssessmentIds().stream()
                        .filter(assessmentId -> !foundAssessmentIds.contains(assessmentId))
                        .map(assessmentId -> notFoundError(Assessment.class, assessmentId))
                        .collect(toList())
        );

        if (!failures.isEmpty()) {
            return serviceFailure(failures);
        }

        return serviceSuccess();
    }
}
