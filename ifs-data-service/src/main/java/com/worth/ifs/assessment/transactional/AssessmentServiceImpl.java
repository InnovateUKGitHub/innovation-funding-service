package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.mapper.AssessmentMapper;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.workflow.mapper.ProcessOutcomeMapper;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_RECOMMENDATION_FAILED;
import static com.worth.ifs.commons.error.CommonFailureKeys.ASSESSMENT_REJECTION_FAILED;
import static com.worth.ifs.commons.error.ErrorConverterFactory.toField;
import static com.worth.ifs.commons.rest.ValidationMessages.rejectValue;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static com.worth.ifs.util.StringFunctions.countWords;
import static java.util.stream.Collectors.toList;

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
    public ServiceResult<Void> rejectInvitation(Long assessmentId, ProcessOutcomeResource processOutcome) {
        ValidationMessages validationMessages = validate(processOutcome, 100);
        if (validationMessages.hasErrors()) {
            return serviceFailure(validationMessages.getErrors());
        }

        return find(assessmentRepository.findOne(assessmentId), notFoundError(AssessmentRepository.class, assessmentId)).andOnSuccess(found -> {
            if (!assessmentWorkflowService.rejectInvitation(found.getParticipant().getId(), found, processOutcomeMapper.mapToDomain(processOutcome))) {
                return serviceFailure(new Error(ASSESSMENT_REJECTION_FAILED));
            }
            return serviceSuccess();
        });
    }

    private ValidationMessages validate(ProcessOutcomeResource processOutcome, int wordLimit) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(processOutcome, "processOutcome");
        if (!validateWordCount(wordLimit, processOutcome.getDescription())) {
            rejectValue(errors, "description", "validation.field.max.word.count", "", wordLimit);
        }

        if (!validateWordCount(wordLimit, processOutcome.getComment())) {
            rejectValue(errors, "comment", "validation.field.max.word.count", "", wordLimit);
        }
        return new ValidationMessages(errors);
    }

    private boolean validateWordCount(int wordLimit, String content) {
        return countWords(content) <= wordLimit;
    }
}
