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
import com.worth.ifs.workflow.mapper.ProcessOutcomeMapper;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private static String SUMMARY_FEEDBACK =  "summary feedback";
    private static String SUMMARY_COMMENT =  "summary comment";

    private static Map<String,CommonFailureKeys> failureMap = Collections.unmodifiableMap(Stream.of(
            new AbstractMap.SimpleEntry<>(SUMMARY_FEEDBACK, SUMMARY_FEEDBACK_WORD_LIMIT_EXCEEDED),
            new AbstractMap.SimpleEntry<>(SUMMARY_COMMENT, SUMMARY_COMMENT_WORD_LIMIT_EXCEEDED))
            .collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue())));

    @Override
    public ServiceResult<AssessmentResource> findById(final Long id) {
        return find(assessmentRepository.findOne(id), notFoundError(Assessment.class, id)).andOnSuccessReturn(assessmentMapper::mapToResource);
    }

    @Override
    public ServiceResult<Void> recommend(Long assessmentId, ProcessOutcomeResource processOutcome) {
        return
                validateWordCount(SUMMARY_FEEDBACK, processOutcome.getDescription()).andOnSuccess(() ->
                        validateWordCount(SUMMARY_COMMENT, processOutcome.getComment()).andOnSuccess(() ->
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

    private ServiceResult<Void> validateWordCount(String form, String value) {
        //TODO lookup word limit: INFUND-4512
        int wordLimit = 100;

        if (value != null) {
            // clean any HTML markup from the value
            String cleaned = Jsoup.parse(value).text();

            if (cleaned.split("\\s+").length > wordLimit) {
                return serviceFailure(new Error(failureMap.get(form)));
            }
        }
        return serviceSuccess();
    }
}
