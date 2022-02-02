package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.AbstractIfsCrudRestServiceImpl;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionnaireQuestionRestServiceImpl extends AbstractIfsCrudRestServiceImpl<QuestionnaireQuestionResource, Long> implements QuestionnaireQuestionRestService {

    @Override
    protected String getBaseUrl() {
        return "/questionnaire-question";
    }

    @Override
    protected Class<QuestionnaireQuestionResource> getResourceClass() {
        return QuestionnaireQuestionResource.class;
    }

    @Override
    protected ParameterizedTypeReference<List<QuestionnaireQuestionResource>> getListTypeReference() {
        return new ParameterizedTypeReference<List<QuestionnaireQuestionResource>>() {};
    }
}
