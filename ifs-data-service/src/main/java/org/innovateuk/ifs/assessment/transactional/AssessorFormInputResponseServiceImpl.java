package org.innovateuk.ifs.assessment.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.transactional.QuestionService;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.assessment.mapper.AssessorFormInputResponseMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AssessorFormInputResponseRepository;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.rest.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.util.AssessorScoreAverageCollector;
import org.innovateuk.ifs.validator.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.processAnyFailuresOrSucceed;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.innovateuk.ifs.form.resource.FormInputType.ASSESSOR_SCORE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFilter;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * Transactional and secured service providing operations around {@link org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse} data.
 */
@Service
public class AssessorFormInputResponseServiceImpl extends BaseTransactionalService implements AssessorFormInputResponseService {

    @Autowired
    private AssessorFormInputResponseRepository assessorFormInputResponseRepository;

    @Autowired
    private AssessorFormInputResponseMapper assessorFormInputResponseMapper;

    @Autowired
    private FormInputRepository formInputRepository;

    @Autowired
    private AssessmentWorkflowHandler assessmentWorkflowHandler;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    private FormInputService formInputService;

    @Override
    public ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(long assessmentId) {
        return serviceSuccess(simpleMap(assessorFormInputResponseRepository.findByAssessmentId(assessmentId), assessorFormInputResponseMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(long assessmentId, long questionId) {
        return serviceSuccess(simpleMap(assessorFormInputResponseRepository.findByAssessmentIdAndFormInputQuestionId(assessmentId, questionId), assessorFormInputResponseMapper::mapToResource));
    }

    @Override
    @Transactional
   public ServiceResult<Void> updateFormInputResponses(AssessorFormInputResponsesResource responses) {
        return processAnyFailuresOrSucceed(simpleMap(responses.getResponses(), this::saveAssessorFormInputResponse));
    }

    @Override
    public ServiceResult<ApplicationAssessmentAggregateResource> getApplicationAggregateScores(long applicationId) {
        List<AssessorFormInputResponse> responses = assessorFormInputResponseRepository.findByAssessmentTargetId(applicationId);

        Map<Long, BigDecimal> avgScores = calculateAverageScorePerQuestion(responses);
        long averagePercentage = getAveragePercentage(responses);

        int totalScope = 0;
        int totalInScope = 0;
        for (AssessorFormInputResponse response : responses) {
            if (response.getFormInput().getType() == FormInputType.ASSESSOR_APPLICATION_IN_SCOPE) {
                totalScope++;
                if (response.getValue().equals("true")) {
                    totalInScope++;
                }
            }
        }

        // Infer that assessment of the Scope question is required if there are Scope responses
        boolean scopeAssessed = totalScope > 0;

        return serviceSuccess(new ApplicationAssessmentAggregateResource(scopeAssessed, totalScope, totalInScope,
                avgScores, averagePercentage));
    }

    private long getAveragePercentage(List<AssessorFormInputResponse> responses) {
        return Math.round(responses.stream()
                    .filter(input -> input.getFormInput().getType() == ASSESSOR_SCORE)
                    .mapToDouble(value -> (Double.parseDouble(value.getValue()) / value.getFormInput().getQuestion().getAssessorMaximumScore()) * 100.0)
                    .average()
                    .orElse(0.0));
    }

    private Map<Long, BigDecimal> calculateAverageScorePerQuestion(List<AssessorFormInputResponse> responses) {
        return responses.stream()
                    .filter(response -> response.getFormInput().getType() == ASSESSOR_SCORE)
                    .collect(
                            Collectors.groupingBy(
                                    x -> x.getFormInput().getQuestion().getId(),
                                    Collectors.mapping(
                                            AssessorFormInputResponse::getValue,
                                            new AssessorScoreAverageCollector())));
    }

    @Override
    public ServiceResult<AssessmentFeedbackAggregateResource> getAssessmentAggregateFeedback(long applicationId, long questionId) {
        List<AssessorFormInputResponse> responses = assessorFormInputResponseRepository.findByAssessmentTargetIdAndFormInputQuestionId(applicationId, questionId);
        BigDecimal avgScore = responses.stream()
                .filter(input -> input.getFormInput().getType() == ASSESSOR_SCORE)
                .map(AssessorFormInputResponse::getValue)
                .collect(new AssessorScoreAverageCollector());
        List<String> feedback = responses.stream()
                .filter(input -> input.getFormInput().getType() == FormInputType.TEXTAREA)
                .map(AssessorFormInputResponse::getValue)
                .collect(toList());
        return serviceSuccess(new AssessmentFeedbackAggregateResource(avgScore, feedback));
    }

    private FormInputResponse mapToFormInputResponse(AssessorFormInputResponseResource response) {
        FormInputResponse formInputResponse = new FormInputResponse();

        formInputResponse.setValue(response.getValue());
        formInputResponse.setApplication(assessmentRepository.findOne(response.getAssessment()).getTarget());
        formInputResponse.setFormInput(formInputRepository.findOne(response.getFormInput()));
        formInputResponse.setUpdateDate(response.getUpdatedDate());

        return formInputResponse;
    }

    @Override
    public ServiceResult<AssessmentDetailsResource> getAssessmentDetails(long assessmentId) {
        final Assessment assessment = assessmentRepository.findOne(assessmentId);
        final Map<Long, List<FormInputResource>> assessmentFormInputs = getAssessmentFormInputs(assessment.getTarget().getCompetition().getId());
        final Map<Long, List<AssessorFormInputResponseResource>> assessorFormInputResponses = getAssessorResponses(assessmentId);
        final List<QuestionResource> questions = simpleFilter(
                questionService.getQuestionsByAssessmentId(assessmentId).getSuccessObjectOrThrowException(),
                question -> assessmentFormInputs.containsKey(question.getId())
        );
        return serviceSuccess(new AssessmentDetailsResource(questions, assessmentFormInputs, assessorFormInputResponses));
    }

    private Map<Long, List<FormInputResource>> getAssessmentFormInputs(long competitionId) {
        List<FormInputResource> assessmentFormInputs = formInputService.findByCompetitionIdAndScope(competitionId, ASSESSMENT).getSuccessObjectOrThrowException();
        return assessmentFormInputs.stream().collect(groupingBy(FormInputResource::getQuestion));
    }

    private Map<Long, List<AssessorFormInputResponseResource>> getAssessorResponses(long assessmentId) {
        List<AssessorFormInputResponseResource> assessorResponses = getAllAssessorFormInputResponses(assessmentId).getSuccessObjectOrThrowException();
        return assessorResponses.stream().collect(groupingBy(AssessorFormInputResponseResource::getQuestion));
    }

    private ServiceResult<Void> saveAssessorFormInputResponse(AssessorFormInputResponseResource response) {
        AssessorFormInputResponseResource createdResponse = getOrCreateAssessorFormInputResponse(response.getAssessment(), response.getFormInput())
                .getSuccessObjectOrThrowException();

        String value = StringUtils.stripToNull(response.getValue());
        boolean same = StringUtils.compare(value, createdResponse.getValue()) == 0;

        if (!same) {
            createdResponse.setUpdatedDate(now());
        }
        createdResponse.setValue(value);

        BindingResult result = validationUtil.validateResponse(mapToFormInputResponse(createdResponse), true);

        if (result.hasErrors()) {
            return serviceFailure(new ValidationMessages(result).getErrors());
        }

        return saveAndNotifyWorkflowHandler(createdResponse);
    }

    private ServiceResult<AssessorFormInputResponseResource> getOrCreateAssessorFormInputResponse(Long assessmentId, Long formInputId) {
        return find(assessorFormInputResponseRepository.findByAssessmentIdAndFormInputId(assessmentId, formInputId), notFoundError(AssessorFormInputResponseResource.class, assessmentId, formInputId)).handleSuccessOrFailure(failure -> {
                    AssessorFormInputResponseResource newAssessorFormInputResponseResource = new AssessorFormInputResponseResource();
                    newAssessorFormInputResponseResource.setAssessment(assessmentId);
                    newAssessorFormInputResponseResource.setFormInput(formInputId);
                    newAssessorFormInputResponseResource.setUpdatedDate(now());
                    return serviceSuccess(newAssessorFormInputResponseResource);
                }, assessorFormInputResponse -> serviceSuccess(assessorFormInputResponseMapper.mapToResource(assessorFormInputResponse))
        );
    }

    private ServiceResult<Void> saveAndNotifyWorkflowHandler(AssessorFormInputResponseResource response) {
        AssessorFormInputResponse assessorFormInputResponse = assessorFormInputResponseMapper.mapToDomain(response);

        if (assessmentWorkflowHandler.feedback(assessorFormInputResponse.getAssessment())) {
            assessorFormInputResponseRepository.save(assessorFormInputResponse);

            return serviceSuccess();
        }

        return serviceFailure(CommonFailureKeys.GENERAL_FORBIDDEN);
    }
}
