package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.AbstractIfsCrudServiceImpl;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireQuestion;
import org.innovateuk.ifs.questionnaire.config.repository.QuestionnaireQuestionRepository;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionnaireQuestionServiceImpl
        extends AbstractIfsCrudServiceImpl<QuestionnaireQuestionResource, QuestionnaireQuestion, Long>
        implements QuestionnaireQuestionService {

    @Autowired
    private QuestionnaireQuestionRepository questionnaireQuestionRepository;

    @Override
    protected CrudRepository<QuestionnaireQuestion, Long> crudRepository() {
        return questionnaireQuestionRepository;
    }

    @Override
    protected Class<QuestionnaireQuestion> getDomainClazz() {
        return QuestionnaireQuestion.class;
    }

    @Override
    protected QuestionnaireQuestion mapToDomain(QuestionnaireQuestion questionnaireQuestion, QuestionnaireQuestionResource questionnaireQuestionResource) {
        questionnaireQuestion.setDepth(questionnaireQuestionResource.getDepth());
        questionnaireQuestion.setTitle(questionnaireQuestionResource.getTitle());
        questionnaireQuestion.setGuidance(questionnaireQuestionResource.getGuidance());
        return questionnaireQuestion;
    }

}
