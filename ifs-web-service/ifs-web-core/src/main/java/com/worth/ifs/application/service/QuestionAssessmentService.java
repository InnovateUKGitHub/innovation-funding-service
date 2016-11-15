package com.worth.ifs.application.service;

import com.worth.ifs.application.resource.QuestionAssessmentResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.resource.QuestionStatusResource;
import com.worth.ifs.application.resource.QuestionType;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.rest.ValidationMessages;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.ProcessRoleResource;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link QuestionResource} related data.
 */
public interface QuestionAssessmentService {

    ServiceResult<QuestionAssessmentResource> findById(Long id);

    ServiceResult<QuestionAssessmentResource> findByQuestionId(Long questionId);
}
