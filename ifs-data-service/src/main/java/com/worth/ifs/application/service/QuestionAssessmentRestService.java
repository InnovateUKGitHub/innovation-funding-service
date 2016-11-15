package com.worth.ifs.application.service;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.QuestionAssessment;
import com.worth.ifs.application.resource.QuestionAssessmentResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link Question} related data.
 */
public interface QuestionAssessmentRestService {
    RestResult<QuestionAssessmentResource> findById(Long id);
    RestResult<QuestionAssessmentResource> findByQuestionId(Long questionId);

}
