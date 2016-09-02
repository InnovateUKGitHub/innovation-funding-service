package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.mapper.AssessorFormInputResponseMapper;
import com.worth.ifs.assessment.repository.AssessorFormInputResponseRepository;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.form.resource.FormInputResource;
import com.worth.ifs.form.transactional.FormInputService;
import com.worth.ifs.transactional.BaseTransactionalService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.FORM_WORD_LIMIT_EXCEEDED;
import static com.worth.ifs.commons.error.Error.globalError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.time.LocalDateTime.now;

/**
 * Transactional and secured service providing operations around {@link com.worth.ifs.assessment.domain.AssessorFormInputResponse} data.
 */
@Service
public class AssessorFormInputResponseServiceImpl extends BaseTransactionalService implements AssessorFormInputResponseService {

    @Autowired
    private AssessorFormInputResponseRepository assessorFormInputResponseRepository;

    @Autowired
    private AssessorFormInputResponseMapper assessorFormInputResponseMapper;

    @Autowired
    private FormInputService formInputService;

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
        return validateWordCount(response).andOnSuccessReturnVoid(() -> performUpdateFormInputResponse(response));
    }

    private ServiceResult<Void> performUpdateFormInputResponse(AssessorFormInputResponseResource response) {
        AssessorFormInputResponseResource assessorFormInputResponse = getOrCreateAssessorFormInputResponse(response.getAssessment(), response.getFormInput()).getSuccessObjectOrThrowException();
        String value = StringUtils.stripToNull(response.getValue());
        boolean same = (value == null && assessorFormInputResponse.getValue() == null) || (value != null && value.equals(assessorFormInputResponse.getValue()));
        if (!same) {
            assessorFormInputResponse.setUpdatedDate(now());
        }
        assessorFormInputResponse.setValue(value);
        assessorFormInputResponseRepository.save(assessorFormInputResponseMapper.mapToDomain(assessorFormInputResponse));
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

    private ServiceResult<Void> validateWordCount(AssessorFormInputResponseResource response) {
        String value = response.getValue();
        FormInputResource formInputResource = formInputService.findFormInput(response.getFormInput()).getSuccessObject();
        Integer wordLimit = formInputResource.getWordCount();

        if (value != null && wordLimit != null && wordLimit > 0) {
            // clean any HTML markup from the value
            String cleaned = Jsoup.parse(value).text();

            if (cleaned.split("\\s+").length > formInputResource.getWordCount()) {
                return serviceFailure(globalError(FORM_WORD_LIMIT_EXCEEDED));
            }
        }
        return serviceSuccess();
    }
}
