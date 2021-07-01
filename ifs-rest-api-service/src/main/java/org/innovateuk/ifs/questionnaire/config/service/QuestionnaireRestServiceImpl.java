package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.AbstractIfsCrudRestServiceImpl;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionnaireRestServiceImpl extends AbstractIfsCrudRestServiceImpl<QuestionnaireResource, Long> implements QuestionnaireRestService {

    @Override
    protected String getBaseUrl() {
        return "/questionnaire";
    }

    @Override
    protected Class<QuestionnaireResource> getResourceClass() {
        return QuestionnaireResource.class;
    }

    @Override
    protected ParameterizedTypeReference<List<QuestionnaireResource>> getListTypeReference() {
        return new ParameterizedTypeReference<List<QuestionnaireResource>>() {};
    }
}
