package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.AbstractIfsCrudServiceImpl;
import org.innovateuk.ifs.questionnaire.config.domain.Questionnaire;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireRepository;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionnaireServiceImpl
        extends AbstractIfsCrudServiceImpl<QuestionnaireResource, Questionnaire, Long>
        implements QuestionnaireService {

    @Autowired
    private QuestionnaireRepository questionnaireRepository;

    @Override
    protected CrudRepository<Questionnaire, Long> crudRepository() {
        return questionnaireRepository;
    }

    @Override
    protected Class<Questionnaire> getDomainClazz() {
        return Questionnaire.class;
    }

    @Override
    protected Questionnaire mapToDomain(Questionnaire questionnaire, QuestionnaireResource questionnaireResource) {
        questionnaire.setSecurityType(questionnaireResource.getSecurityType());
        questionnaire.setDescription(questionnaireResource.getDescription());
        questionnaire.setTitle(questionnaireResource.getTitle());
        return questionnaire;
    }
}
