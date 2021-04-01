package org.innovateuk.ifs.questionnaire.response.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.crud.AbstractIfsCrudRestServiceImpl;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResponseResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionnaireQuestionResponseRestServiceImpl extends AbstractIfsCrudRestServiceImpl<QuestionnaireQuestionResponseResource, Long> implements QuestionnaireQuestionResponseRestService {

    @Override
    protected String getBaseUrl() {
        return "/questionnaire-question-response";
    }

    @Override
    protected Class<QuestionnaireQuestionResponseResource> getResourceClass() {
        return QuestionnaireQuestionResponseResource.class;
    }

    @Override
    protected ParameterizedTypeReference<List<QuestionnaireQuestionResponseResource>> getListTypeReference() {
        return new ParameterizedTypeReference<List<QuestionnaireQuestionResponseResource>>() {};
    }

    @Override
    public RestResult<QuestionnaireQuestionResponseResource> findByQuestionnaireQuestionIdAndQuestionnaireResponseId(long questionnaireQuestionId, String questionnaireResponseId) {
        return getWithRestResult(getBaseUrl() + String.format("/question/%d/response/%s", questionnaireQuestionId, questionnaireResponseId), getResourceClass());
    }
}
