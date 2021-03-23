package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.AbstractIfsCrudRestServiceImpl;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireTextOutcomeResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionnaireTextOutcomeRestServiceImpl extends AbstractIfsCrudRestServiceImpl<QuestionnaireTextOutcomeResource, Long> implements QuestionnaireTextOutcomeRestService {

    @Override
    protected String getBaseUrl() {
        return "/questionnaire-text-outcome";
    }

    @Override
    protected Class<QuestionnaireTextOutcomeResource> getResourceClass() {
        return QuestionnaireTextOutcomeResource.class;
    }

    @Override
    protected ParameterizedTypeReference<List<QuestionnaireTextOutcomeResource>> getListTypeReference() {
        return new ParameterizedTypeReference<List<QuestionnaireTextOutcomeResource>>() {};
    }
}
