package org.innovateuk.ifs.questionnaire.response.security;

import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategies;
import org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResponseResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResponseResource;
import org.innovateuk.ifs.questionnaire.response.mapper.QuestionnaireQuestionResponseMapper;
import org.innovateuk.ifs.questionnaire.response.mapper.QuestionnaireResponseMapper;
import org.innovateuk.ifs.questionnaire.response.repository.QuestionnaireQuestionResponseRepository;
import org.innovateuk.ifs.questionnaire.response.repository.QuestionnaireResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@PermissionEntityLookupStrategies
public class QuestionnaireResponseLookupStrategy {

    @Autowired
    private QuestionnaireResponseRepository questionnaireResponseRepository;

    @Autowired
    private QuestionnaireResponseMapper questionnaireResponseMapper;

    @Autowired
    private QuestionnaireQuestionResponseRepository questionnaireQuestionResponseRepository;

    @Autowired
    private QuestionnaireQuestionResponseMapper questionnaireQuestionResponseMapper;
    @PermissionEntityLookupStrategy
    public QuestionnaireResponseResource get(UUID responseId) {
        return questionnaireResponseRepository.findById(responseId)
                .map(questionnaireResponseMapper::mapToResource)
                .orElse(null);
    }

    @PermissionEntityLookupStrategy
    public QuestionnaireQuestionResponseResource getQuestion(Long responseId) {
        return questionnaireQuestionResponseRepository.findById(responseId)
                .map(questionnaireQuestionResponseMapper::mapToResource)
                .orElse(null);
    }
}
