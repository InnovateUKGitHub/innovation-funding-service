package org.innovateuk.ifs.questionnaire.config.saver;

import org.innovateuk.ifs.questionnaire.config.form.QuestionnaireQuestionConfigForm;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireTextOutcomeRestService;
import org.innovateuk.ifs.questionnaire.resource.DecisionType;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireTextOutcomeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionnaireQuestionConfigFormSaver {

    @Autowired
    private QuestionnaireQuestionRestService questionnaireQuestionRestService;

    @Autowired
    private QuestionnaireOptionRestService questionnaireOptionRestService;

    @Autowired
    private QuestionnaireTextOutcomeRestService questionnaireTextOutcomeRestService;


    public void create(long questionnaireId, QuestionnaireQuestionConfigForm form) {
        QuestionnaireQuestionResource question = new QuestionnaireQuestionResource();
        question.setQuestionnaire(questionnaireId);
        question.setTitle(form.getTitle());
        question.setQuestion(form.getQuestion());
        question.setGuidance(form.getGuidance());
        questionnaireQuestionRestService.create(question).getSuccess();
    }

    public void edit(long questionnaireId, long questionId, QuestionnaireQuestionConfigForm form) {
        QuestionnaireQuestionResource question = questionnaireQuestionRestService.get(questionId).getSuccess();
        question.setTitle(form.getTitle());
        question.setQuestion(form.getQuestion());
        question.setGuidance(form.getGuidance());
        questionnaireQuestionRestService.update(questionId, question).getSuccess();

        form.getOptions().forEach(option -> {
            QuestionnaireOptionResource optionResource;
            if (option.getOptionId() == null) {
                optionResource = new QuestionnaireOptionResource();
                optionResource.setQuestion(questionId);
                optionResource.setDecisionType(option.getDecisionType());
            } else {
                optionResource = questionnaireOptionRestService.get(option.getOptionId()).getSuccess();
            }
            optionResource.setText(option.getText());

            if (option.getDecisionType() == DecisionType.QUESTION) {
                optionResource.setDecision(option.getDecisionId());
            } else if (option.getDecisionType() == DecisionType.TEXT_OUTCOME) {
                QuestionnaireTextOutcomeResource outcomeResource;
                if (option.getDecisionId() == null) {
                    outcomeResource = new QuestionnaireTextOutcomeResource();
                    outcomeResource.setText(option.getTextOutcome());
                    outcomeResource = questionnaireTextOutcomeRestService.create(outcomeResource).getSuccess();
                    optionResource.setDecision(outcomeResource.getId());
                } else {
                    outcomeResource = questionnaireTextOutcomeRestService.get(option.getDecisionId()).getSuccess();
                    outcomeResource.setText(option.getTextOutcome());
                    questionnaireTextOutcomeRestService.update(outcomeResource.getId(), outcomeResource).getSuccess();
                }
            }
            if (optionResource.getId() == null) {
                questionnaireOptionRestService.create(optionResource).getSuccess();
            } else {
                questionnaireOptionRestService.update(optionResource.getId(), optionResource).getSuccess();
            }
        });
    }
}
