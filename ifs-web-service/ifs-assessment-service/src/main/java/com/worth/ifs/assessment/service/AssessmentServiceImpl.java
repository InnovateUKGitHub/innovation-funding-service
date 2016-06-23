package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.AssessmentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to retrieve and store {@link com.worth.ifs.assessment.resource.AssessmentResource} related data,
 * through the RestService {@link AssessmentRestService}.
 */
@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AssessmentRestService assessmentRestService;

    @Override
    public AssessmentResource getById(final Long id) {
        return assessmentRestService.getById(id).getSuccessObjectOrThrowException();
    }
}
