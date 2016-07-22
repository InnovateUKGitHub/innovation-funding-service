package com.worth.ifs.assessment.transactional;

import com.worth.ifs.assessment.mapper.AssessorFormInputResponseMapper;
import com.worth.ifs.assessment.repository.AssessorFormInputResponseRepository;
import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
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
 * Transactional and secured service providing operations around {@link com.worth.ifs.assessment.domain.AssessorFormInputResponse} data.
 */
@Service
public class AssessorFormInputResponseServiceImpl extends BaseTransactionalService implements AssessorFormInputResponseService {

    @Autowired
    private AssessorFormInputResponseRepository assessorFormInputResponseRepository;

    @Autowired
    private AssessorFormInputResponseMapper assessorFormInputResponseMapper;

    @Override
    public ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponses(Long assessmentId) {
        return serviceSuccess(simpleMap(assessorFormInputResponseRepository.findByAssessmentId(assessmentId), assessorFormInputResponseMapper::mapToResource));
    }

    @Override
    public ServiceResult<List<AssessorFormInputResponseResource>> getAllAssessorFormInputResponsesByAssessmentAndQuestion(Long assessmentId, Long questionId) {
        return serviceSuccess(simpleMap(assessorFormInputResponseRepository.findByAssessmentIdAndFormInputQuestionId(assessmentId, questionId), assessorFormInputResponseMapper::mapToResource));
    }

    @Override
    public ServiceResult<Void> updateFormInputResponse(Long assessmentId, Long formInputId, String value) {
        AssessorFormInputResponseResource assessorFormInputResponse = getOrCreateAssessorFormInputResponse(assessmentId, formInputId).getSuccessObjectOrThrowException();
        assessorFormInputResponse.setValue(value);
        assessorFormInputResponseRepository.save(assessorFormInputResponseMapper.mapToDomain(assessorFormInputResponse));
        return serviceSuccess();
    }

    private ServiceResult<AssessorFormInputResponseResource> getOrCreateAssessorFormInputResponse(Long assessmentId, Long formInputId) {
        return find(assessorFormInputResponseRepository.findByAssessmentIdAndFormInputId(assessmentId, formInputId), notFoundError(AssessorFormInputResponseResource.class, assessmentId, formInputId)).handleSuccessOrFailure(failure -> {
                    AssessorFormInputResponseResource newAssessorFormInputResponseResource = new AssessorFormInputResponseResource();
                    newAssessorFormInputResponseResource.setAssessment(assessmentId);
                    newAssessorFormInputResponseResource.setFormInput(formInputId);
                    return serviceSuccess(newAssessorFormInputResponseResource);
                }, assessorFormInputResponseResource -> serviceSuccess(assessorFormInputResponseMapper.mapToResource(assessorFormInputResponseResource))
        );
    }
}
