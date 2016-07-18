package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessorFormInputResponseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains methods to retrieve and store {@link com.worth.ifs.assessment.resource.AssessorFormInputResponseResource} related data,
 * through the RestService {@link AssessorFormInputResponseRestService}.
 */
@Service
public class AssessorFormInputResponseServiceImpl implements AssessorFormInputResponseService {

    @Autowired
    private AssessorFormInputResponseRestService assessorFormInputResponseRestService;

    @Override
    public List<AssessorFormInputResponseResource> getAllAssessorFormInputResponses(Long assessmentId) {
        return assessorFormInputResponseRestService.getAllAssessorFormInputResponses(assessmentId).getSuccessObjectOrThrowException();
    }

    @Override
    public List<AssessorFormInputResponseResource> getAllAssessorFormInputResponsesByAssessmentAndQuestion(Long assessmentId, Long questionId) {
        return assessorFormInputResponseRestService.getAllAssessorFormInputResponsesByAssessmentAndQuestion(assessmentId, questionId).getSuccessObjectOrThrowException();
    }
}