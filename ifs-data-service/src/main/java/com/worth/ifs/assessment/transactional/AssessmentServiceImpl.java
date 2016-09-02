package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.mapper.AssessmentMapper;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.workflow.AssessmentWorkflowEventHandler;
import com.worth.ifs.commons.error.CommonFailureKeys;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.util.MapFunctions;
import com.worth.ifs.workflow.mapper.ProcessOutcomeMapper;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
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
    private ProcessOutcomeMapper processOutcomeMapper;

    @Autowired
    private AssessmentWorkflowEventHandler assessmentWorkflowEventHandler;

    private enum DecisionField {
        FEEDBACK,
        COMMENT;
    }

    private Map<DecisionField, CommonFailureKeys> wordCountFailureMap = MapFunctions.asMap(DecisionField.FEEDBACK, ASSESSMENT_SUMMARY_FEEDBACK_WORD_LIMIT_EXCEEDED,
            DecisionField.COMMENT, ASSESSMENT_SUMMARY_COMMENT_WORD_LIMIT_EXCEEDED);

    @Override
    public ServiceResult<AssessmentResource> findById(final Long id) {
        return find(assessmentRepository.findOne(id), notFoundError(Assessment.class, id)).andOnSuccessReturn(assessmentMapper::mapToResource);
    }

    @Override
    public ServiceResult<Void> recommend(Long assessmentId, ProcessOutcomeResource processOutcome) {
        return validateFeedbackWordCount(processOutcome).andOnSuccess(() ->
                validateCommentWordCount(processOutcome).andOnSuccess(() ->
                        find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
                            if (!assessmentWorkflowEventHandler.recommend(found.getProcessRole().getId(), found, processOutcomeMapper.mapToDomain(processOutcome))) {
                                return serviceFailure(new Error(ASSESSMENT_RECOMMENDATION_FAILED));
                            }
                            return serviceSuccess();
                        }).andOnSuccessReturnVoid()));
    }

    @Override
    public ServiceResult<Void> rejectInvitation(Long assessmentId, ProcessOutcomeResource processOutcome) {
        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowEventHandler.rejectInvitation(found.getProcessRole().getId(), found, processOutcomeMapper.mapToDomain(processOutcome))) {
                return serviceFailure(new Error(ASSESSMENT_REJECTION_FAILED));
            }
            return serviceSuccess();
        }).andOnSuccessReturnVoid();
    }

    private ServiceResult<Void> validateFeedbackWordCount(ProcessOutcomeResource processOutcome) {
        return validateWordCount(DecisionField.FEEDBACK, processOutcome.getDescription());
    }

    private ServiceResult<Void> validateCommentWordCount(ProcessOutcomeResource processOutcome) {
        return validateWordCount(DecisionField.COMMENT, processOutcome.getComment());
    }

    private ServiceResult<Void> validateWordCount(DecisionField field, String value) {
        //TODO lookup word limit: INFUND-4512
        int wordLimit = 100;

        if (value != null) {
            // clean any HTML markup from the value
            String cleaned = Jsoup.parse(value).text();

            if (cleaned.split("\\s+").length > wordLimit) {
                return serviceFailure(new Error(wordCountFailureMap.get(field), wordLimit));
            }
        }
        return serviceSuccess();
    }
}
