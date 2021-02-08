package org.innovateuk.ifs.questionnaire.config.service;

import org.innovateuk.ifs.crud.AbstractIfsCrudServiceImpl;
import org.innovateuk.ifs.questionnaire.config.domain.QuestionnaireOption;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.springframework.stereotype.Service;

@Service
public class QuestionnaireOptionServiceImpl extends AbstractIfsCrudServiceImpl<QuestionnaireOptionResource, QuestionnaireOption, Long> implements QuestionnaireOptionService {
    @Override
    protected Class<QuestionnaireOption> getDomainClazz() {
        return QuestionnaireOption.class;
    }

    @Override
    protected QuestionnaireOption mapToDomain(QuestionnaireOption questionnaireOption, QuestionnaireOptionResource questionnaireOptionResource) {
        questionnaireOption.setText(questionnaireOptionResource.getText());
        return questionnaireOption;
    }
}
