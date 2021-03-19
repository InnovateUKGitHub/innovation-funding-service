package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.AbstractIfsCrudRestServiceImpl;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionnaireOptionRestServiceImpl extends AbstractIfsCrudRestServiceImpl<QuestionnaireOptionResource, Long> implements QuestionnaireOptionRestService {

    @Override
    protected String getBaseUrl() {
        return "/questionnaire-option";
    }

    @Override
    protected Class<QuestionnaireOptionResource> getResourceClass() {
        return QuestionnaireOptionResource.class;
    }

    @Override
    protected ParameterizedTypeReference<List<QuestionnaireOptionResource>> getListTypeReference() {
        return new ParameterizedTypeReference<List<QuestionnaireOptionResource>>() {};
    }
}
