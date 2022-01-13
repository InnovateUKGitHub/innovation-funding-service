package org.innovateuk.ifs.questionnaire.response.service;

import org.innovateuk.ifs.crud.AbstractIfsCrudRestServiceImpl;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResponseResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionnaireResponseRestServiceImpl extends AbstractIfsCrudRestServiceImpl<QuestionnaireResponseResource, String> implements QuestionnaireResponseRestService {

    @Override
    protected String getBaseUrl() {
        return "/questionnaire-response";
    }

    @Override
    protected Class<QuestionnaireResponseResource> getResourceClass() {
        return QuestionnaireResponseResource.class;
    }

    @Override
    protected ParameterizedTypeReference<List<QuestionnaireResponseResource>> getListTypeReference() {
        return new ParameterizedTypeReference<List<QuestionnaireResponseResource>>() {};
    }
}
