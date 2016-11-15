package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.QuestionAssessmentResource;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionAssessmentServiceImpl implements QuestionAssessmentService {

    @Autowired
    private QuestionAssessmentRestService questionAssessmentRestService;

    @Override
    public ServiceResult<QuestionAssessmentResource> findById(Long id) {
        return questionAssessmentRestService.findById(id).toServiceResult();
    }

    @Override
    public ServiceResult<QuestionAssessmentResource> findByQuestionId(Long questionId) {
        return questionAssessmentRestService.findByQuestionId(questionId).toServiceResult();
    }
}
