package org.innovateuk.ifs.assessment.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.innovateuk.ifs.application.validation.ApplicationValidationUtil;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.assessment.mapper.AssessorFormInputResponseMapper;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.repository.AssessorFormInputResponseRepository;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.domain.FormInput;
import org.innovateuk.ifs.form.repository.FormInputRepository;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.form.transactional.QuestionService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.util.AssessorScoreAverageCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.*;
import static org.innovateuk.ifs.form.resource.FormInputScope.ASSESSMENT;
import static org.innovateuk.ifs.form.resource.FormInputType.*;
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
    private ApplicationValidationUtil validationUtil;

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
        BigDecimal averagePercentage = getAveragePercentage(responses);

        int totalScope = 0;
        int totalInScope = 0;
        for (AssessorFormInputResponse response : responses) {
            if (response.getFormInput().getType() == FormInputType.ASSESSOR_APPLICATION_IN_SCOPE) {
                totalScope++;
                if ("true".equals(response.getValue())) {
                    totalInScope++;
                }
            }
        }

        // Infer that assessment of the Scope question is required if there are Scope responses
        boolean scopeAssessed = totalScope > 0;

        return serviceSuccess(new ApplicationAssessmentAggregateResource(scopeAssessed, totalScope, totalInScope,
                avgScores, averagePercentage));
    }

    private BigDecimal getAveragePercentage(List<AssessorFormInputResponse> responses) {
        return BigDecimal.valueOf(responses.stream()
                .filter(input -> input.getFormInput().getType() == ASSESSOR_SCORE)
                .filter(response -> response.getValue() != null)
                .mapToDouble(value -> (Double.parseDouble(value.getValue()) / value.getFormInput().getQuestion().getAssessorMaximumScore()) * 100.0)
                .average()
                .orElse(0.0)).setScale(1, BigDecimal.ROUND_HALF_UP);
    }

    private Map<Long, BigDecimal> calculateAverageScorePerQuestion(List<AssessorFormInputResponse> responses) {
        return responses.stream()
                .filter(response -> response.getFormInput().getType() == ASSESSOR_SCORE)
                .filter(response -> response.getValue() != null)
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
                .filter(Objects::nonNull)
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
        formInputResponse.setApplication(assessmentRepository.findById(response.getAssessment()).get().getTarget());
        formInputResponse.setFormInput(formInputRepository.findById(response.getFormInput()).orElse(null));
        formInputResponse.setUpdateDate(response.getUpdatedDate());

        return formInputResponse;
    }

    @Override
    public ServiceResult<AssessmentDetailsResource> getAssessmentDetails(long assessmentId) {
        final Assessment assessment = assessmentRepository.findById(assessmentId).get();
        final Map<Long, List<FormInputResource>> assessmentFormInputs = getAssessmentFormInputs(assessment.getTarget().getCompetition().getId());
        final Map<Long, List<AssessorFormInputResponseResource>> assessorFormInputResponses = getAssessorResponses(assessmentId);
        final List<QuestionResource> questions = simpleFilter(
                questionService.getQuestionsByAssessmentId(assessmentId).getSuccess(),
                question -> assessmentFormInputs.containsKey(question.getId())
        );
        return serviceSuccess(new AssessmentDetailsResource(questions, assessmentFormInputs, assessorFormInputResponses));
    }

    @Override
    public ServiceResult<ApplicationAssessmentsResource> getApplicationAssessments(long applicationId) {
        List<ServiceResult<ApplicationAssessmentResource>> results = assessmentRepository.findByTargetId(applicationId).stream()
                .map(assessment -> getApplicationAssessment(applicationId, assessment.getId()))
                .collect(toList());
        return aggregate(results)
                .andOnSuccessReturn(assessments -> new ApplicationAssessmentsResource(applicationId, assessments));
    }

    @Override
    public ServiceResult<ApplicationAssessmentResource> getApplicationAssessment(long applicationId, long assessmentId) {
        List<AssessorFormInputResponse> responses = assessorFormInputResponseRepository.findByAssessmentId(assessmentId);

        boolean inScope = false;
        Map<Long, BigDecimal> scores = new HashMap<>();
        Map<Long, String> feedback = new HashMap<>();

        for (AssessorFormInputResponse resp : responses.stream()
                .filter(resp -> resp.getValue() != null)
                .collect(toList())) {
            FormInput formInput = resp.getFormInput();

            if (formInput.getType() == TEXTAREA) {
                feedback.put(formInput.getQuestion().getId(), resp.getValue());
            }

            if (formInput.getType() == ASSESSOR_SCORE) {
                scores.put(formInput.getQuestion().getId(), new BigDecimal(resp.getValue()));
            }

            if (formInput.getType() == ASSESSOR_APPLICATION_IN_SCOPE) {
                inScope = "true".equals(resp.getValue());
            }
        }
        return serviceSuccess(new ApplicationAssessmentResource(
                assessmentId,
                applicationId,
                inScope,
                scores,
                feedback,
                getAveragePercentage(responses)
        ));
    }

    private Map<Long, List<FormInputResource>> getAssessmentFormInputs(long competitionId) {
        List<FormInputResource> assessmentFormInputs = formInputService.findByCompetitionIdAndScope(competitionId, ASSESSMENT).getSuccess();
        return assessmentFormInputs.stream().collect(groupingBy(FormInputResource::getQuestion));
    }

    private Map<Long, List<AssessorFormInputResponseResource>> getAssessorResponses(long assessmentId) {
        List<AssessorFormInputResponseResource> assessorResponses = getAllAssessorFormInputResponses(assessmentId).getSuccess();
        return assessorResponses.stream().collect(groupingBy(AssessorFormInputResponseResource::getQuestion));
    }

    private ServiceResult<Void> saveAssessorFormInputResponse(AssessorFormInputResponseResource response) {
        AssessorFormInputResponseResource createdResponse = getOrCreateAssessorFormInputResponse(response.getAssessment(), response.getFormInput())
                .getSuccess();

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
