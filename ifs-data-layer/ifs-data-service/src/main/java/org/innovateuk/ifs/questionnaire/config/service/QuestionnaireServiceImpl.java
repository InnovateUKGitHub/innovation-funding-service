package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.AbstractIfsCrudServiceImpl;
import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;
import org.springframework.stereotype.Service;

@Service
public class QuestionnaireServiceImpl
        extends AbstractIfsCrudServiceImpl<QuestionnaireResource, Questionnaire, Long>
        implements QuestionnaireService {

    @Override
    protected Class<Questionnaire> getDomainClazz() {
        return Questionnaire.class;
    }

    @Override
    protected Questionnaire mapToDomain(Questionnaire questionnaire, QuestionnaireResource questionnaireResource) {
        return questionnaire;
    }
}
