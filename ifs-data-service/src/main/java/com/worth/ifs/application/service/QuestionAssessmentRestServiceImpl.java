package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.QuestionAssessmentResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

@Service
public class QuestionAssessmentRestServiceImpl extends BaseRestService implements QuestionAssessmentRestService {

    private String questionAssessmentRestURL = "/questionAssessment";

    @Override
    public RestResult<QuestionAssessmentResource> findById(Long id) {
        return getWithRestResult(questionAssessmentRestURL + "/" + id, QuestionAssessmentResource.class);
    }

    @Override
    public RestResult<QuestionAssessmentResource> findByQuestionId(Long questionId) {
        return getWithRestResult(questionAssessmentRestURL + "/findByQuestion/" + questionId, QuestionAssessmentResource.class);
    }
}
