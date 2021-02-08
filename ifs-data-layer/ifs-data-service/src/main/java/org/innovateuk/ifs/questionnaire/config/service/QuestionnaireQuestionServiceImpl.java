package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.AbstractIfsCrudServiceImpl;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireQuestion;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;
import org.springframework.stereotype.Service;

@Service
public class QuestionnaireQuestionServiceImpl
        extends AbstractIfsCrudServiceImpl<QuestionnaireQuestionResource, QuestionnaireQuestion, Long>
        implements QuestionnaireQuestionService {

    @Override
    protected Class<QuestionnaireQuestion> getDomainClazz() {
        return QuestionnaireQuestion.class;
    }

    @Override
    protected QuestionnaireQuestion mapToDomain(QuestionnaireQuestion questionnaireQuestion, QuestionnaireQuestionResource questionnaireQuestionResource) {
        questionnaireQuestion.setPriority(questionnaireQuestionResource.getPriority());
        questionnaireQuestion.setTitle(questionnaireQuestionResource.getTitle());
        questionnaireQuestion.setGuidance(questionnaireQuestionResource.getGuidance());
        return questionnaireQuestion;
    }

}
