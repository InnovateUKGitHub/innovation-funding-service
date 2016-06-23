package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.domain.AssessmentFeedback;
import com.worth.ifs.assessment.mapper.AssessmentFeedbackMapper;
import com.worth.ifs.assessment.repository.AssessmentFeedbackRepository;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.assessment.domain.AssessmentFeedback} data.
 */
@Service
public class AssessmentFeedbackServiceImpl extends BaseTransactionalService implements AssessmentFeedbackService {

    @Autowired
    private AssessmentFeedbackRepository assessmentFeedbackRepository;

    @Autowired
    private AssessmentFeedbackMapper assessmentFeedbackMapper;

    @Override
    public ServiceResult<List<AssessmentFeedbackResource>> getAllAssessmentFeedback(Long assessmentId) {
        return serviceSuccess(simpleMap(assessmentFeedbackRepository.findByAssessmentId(assessmentId), assessmentFeedbackMapper::mapToResource));
    }

    @Override
    public ServiceResult<AssessmentFeedbackResource> getAssessmentFeedbackByAssessmentAndQuestion(final Long assessmentId, final Long questionId) {
        return find(assessmentFeedbackRepository.findByAssessmentIdAndQuestionId(assessmentId, questionId), notFoundError(AssessmentFeedback.class, assessmentId, questionId)).andOnSuccessReturn(assessmentFeedbackMapper::mapToResource);
    }
}