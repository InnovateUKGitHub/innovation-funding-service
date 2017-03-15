package org.innovateuk.ifs.assessment.transactional;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.assessment.domain.AssessorFormInputResponse;
import org.innovateuk.ifs.assessment.mapper.AssessorFormInputResponseMapper;
import org.innovateuk.ifs.assessment.repository.AssessorFormInputResponseRepository;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFeedbackAggregateResource;
import org.innovateuk.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.category.resource.ResearchCategoryResource;
import org.innovateuk.ifs.category.transactional.CategoryService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.transactional.FormInputService;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.util.AssessorScoreAverageCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.StringFunctions.countWords;

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
    private FormInputService formInputService;

    @Autowired
    private AssessmentWorkflowHandler assessmentWorkflowHandler;

    @Autowired
    private CategoryService categoryService;

    @Override
    public ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(Long assessmentId) {
        return serviceSuccess(simpleMap(assessorFormInputResponseRepository.findByAssessmentId(assessmentId), assessorFormInputResponseMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(Long assessmentId, Long questionId) {
        return serviceSuccess(simpleMap(assessorFormInputResponseRepository.findByAssessmentIdAndFormInputQuestionId(assessmentId, questionId), assessorFormInputResponseMapper::mapToResource));
    }

    @Override
    public ServiceResult<Void> updateFormInputResponse(AssessorFormInputResponseResource response) {
        return validate(response).andOnSuccessReturnVoid(() -> performUpdateFormInputResponse(response));
    }


    @Override
    public ServiceResult<ApplicationAssessmentAggregateResource> getApplicationAggregateScores(long applicationId) {
        List<AssessorFormInputResponse> responses = assessorFormInputResponseRepository.findByAssessmentTargetId(applicationId);
        int totalScope = 0;
        int totalInScope = 0;
        Map<Long, BigDecimal> avgScores = responses.stream()
                .filter(input -> input.getFormInput().getType() == FormInputType.ASSESSOR_SCORE)
                .collect(
                        Collectors.groupingBy(
                                x -> x.getFormInput().getQuestion().getId(),
                                Collectors.mapping(
                                        AssessorFormInputResponse::getValue,
                                        new AssessorScoreAverageCollector())));

        long averagePercentage = Math.round(responses.stream()
                .filter(input -> input.getFormInput().getType() == FormInputType.ASSESSOR_SCORE)
                .mapToDouble(value -> (Double.parseDouble(value.getValue()) / value.getFormInput().getQuestion().getAssessorMaximumScore()) * 100.0)
                .average()
                .orElse(0.0));


        for (AssessorFormInputResponse response : responses) {
            if (response.getFormInput().getType() == FormInputType.ASSESSOR_APPLICATION_IN_SCOPE) {
                totalScope++;
                if (response.getValue().equals("true")) {
                    totalInScope++;
                }
            }
        }
        return serviceSuccess(new ApplicationAssessmentAggregateResource(totalScope, totalInScope,avgScores,averagePercentage));
    }

    @Override
    public ServiceResult<AssessmentFeedbackAggregateResource> getAssessmentAggregateFeedback(long applicationId, long questionId) {
        List<AssessorFormInputResponse> responses = assessorFormInputResponseRepository.findByAssessmentTargetIdAndFormInputQuestionId(applicationId, questionId);
        BigDecimal avgScore = responses.stream()
                .filter(input -> input.getFormInput().getType() == FormInputType.ASSESSOR_SCORE)
                .map(AssessorFormInputResponse::getValue)
                .collect(new AssessorScoreAverageCollector());
        List<String> feedback = responses.stream()
                .filter(input -> input.getFormInput().getType() == FormInputType.TEXTAREA)
                .map(AssessorFormInputResponse::getValue)
                .collect(toList());
        return serviceSuccess(new AssessmentFeedbackAggregateResource(avgScore, feedback));
    }

    private ServiceResult<Void> performUpdateFormInputResponse(AssessorFormInputResponseResource response) {
        AssessorFormInputResponseResource assessorFormInputResponse = getOrCreateAssessorFormInputResponse(response.getAssessment(), response.getFormInput()).getSuccessObjectOrThrowException();
        String value = StringUtils.stripToNull(response.getValue());
        boolean same = (value == null && assessorFormInputResponse.getValue() == null) || (value != null && value.equals(assessorFormInputResponse.getValue()));
        if (!same) {
            assessorFormInputResponse.setUpdatedDate(now());
        }
        assessorFormInputResponse.setValue(value);
        saveAndNotifyWorkflowHandler(assessorFormInputResponse);
        return serviceSuccess();
    }

    private ServiceResult<AssessorFormInputResponseResource> getOrCreateAssessorFormInputResponse(Long assessmentId, Long formInputId) {
        return find(assessorFormInputResponseRepository.findByAssessmentIdAndFormInputId(assessmentId, formInputId), notFoundError(AssessorFormInputResponseResource.class, assessmentId, formInputId)).handleSuccessOrFailure(failure -> {
                    AssessorFormInputResponseResource newAssessorFormInputResponseResource = new AssessorFormInputResponseResource();
                    newAssessorFormInputResponseResource.setAssessment(assessmentId);
                    newAssessorFormInputResponseResource.setFormInput(formInputId);
                    newAssessorFormInputResponseResource.setUpdatedDate(now());
                    return serviceSuccess(newAssessorFormInputResponseResource);
                }, assessorFormInputResponseResource -> serviceSuccess(assessorFormInputResponseMapper.mapToResource(assessorFormInputResponseResource))
        );
    }

    private ServiceResult<Void> validate(AssessorFormInputResponseResource response) {
        ServiceResult<Void> result = validateWordCount(response);

        if (result.isSuccess()) {
            return validateResearchCategory(response);
        }

        return result;
    }

    private ServiceResult<Void> validateWordCount(AssessorFormInputResponseResource response) {
        String value = response.getValue();
        FormInputResource formInputResource = formInputService.findFormInput(response.getFormInput()).getSuccessObject();
        Integer wordLimit = formInputResource.getWordCount();

        if (value != null && wordLimit != null && wordLimit > 0) {
            if (countWords(value) > formInputResource.getWordCount()) {
                return serviceFailure(fieldError("value", value, "validation.field.max.word.count", "", wordLimit));
            }
        }

        return serviceSuccess();
    }

    private ServiceResult<Void> validateResearchCategory(AssessorFormInputResponseResource response) {
        String value = response.getValue();
        FormInputResource formInputResource = formInputService.findFormInput(response.getFormInput()).getSuccessObject();

        if (!StringUtils.isEmpty(value) && FormInputType.ASSESSOR_RESEARCH_CATEGORY == formInputResource.getType()) {
            List<ResearchCategoryResource> categoryResources = categoryService.getResearchCategories().getSuccessObject();
            if (categoryResources.stream().filter(category -> category.getId().equals(Long.parseLong(value))).count() == 0) {
                return serviceFailure(fieldError("value", value, "org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException", "CategoryResource", value));
            }
        }

        return serviceSuccess();
    }

    private void saveAndNotifyWorkflowHandler(AssessorFormInputResponseResource response) {
        AssessorFormInputResponse assessorFormInputResponse = assessorFormInputResponseMapper.mapToDomain(response);
        assessorFormInputResponseRepository.save(assessorFormInputResponse);
        assessmentWorkflowHandler.feedback(assessorFormInputResponse.getAssessment());
    }
}
