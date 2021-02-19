package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.AbstractIfsCrudServiceImpl;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireTextOutcome;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireOptionRepository;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireTextOutcomeRepository;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireTextOutcomeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionnaireTextOutcomeServiceImpl
        extends AbstractIfsCrudServiceImpl<QuestionnaireTextOutcomeResource, QuestionnaireTextOutcome, Long>
        implements QuestionnaireTextOutcomeService {

    @Autowired
    private QuestionnaireTextOutcomeRepository questionnaireTextOutcomeRepository;

    @Autowired
    private QuestionnaireOptionRepository questionnaireOptionRepository;

    @Override
    protected CrudRepository<QuestionnaireTextOutcome, Long> crudRepository() {
        return questionnaireTextOutcomeRepository;
    }

    @Override
    protected Class<QuestionnaireTextOutcome> getDomainClazz() {
        return QuestionnaireTextOutcome.class;
    }

    @Override
    protected QuestionnaireTextOutcome mapToDomain(QuestionnaireTextOutcome domain, QuestionnaireTextOutcomeResource resource) {
        domain.setText(resource.getText());
        domain.setImplementation(resource.getImplementation());
        return domain;
    }

}
