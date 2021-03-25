package org.innovateuk.ifs.questionnaire.config.populator;

import org.innovateuk.ifs.commons.exception.IFSRuntimeException;
import org.innovateuk.ifs.questionnaire.config.form.QuestionnaireQuestionConfigForm;
import org.innovateuk.ifs.questionnaire.config.form.QuestionnaireQuestionOptionForm;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireOptionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireTextOutcomeRestService;
import org.innovateuk.ifs.questionnaire.resource.DecisionType;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireTextOutcomeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionnaireQuestionConfigFormPopulator {
    @Autowired
    private QuestionnaireOptionRestService questionnaireOptionRestService;

    @Autowired
    private QuestionnaireQuestionRestService questionnaireQuestionRestService;

    @Autowired
    private QuestionnaireTextOutcomeRestService questionnaireTextOutcomeRestService;

    public QuestionnaireQuestionConfigForm form(long questionnaireId, QuestionnaireQuestionResource question) {
        QuestionnaireQuestionConfigForm form =  new QuestionnaireQuestionConfigForm();
        form.setTitle(question.getTitle());
        form.setQuestion(question.getQuestion());
        form.setGuidance(question.getGuidance());

        List<QuestionnaireOptionResource> options = questionnaireOptionRestService.get(question.getOptions()).getSuccess();

        List<QuestionnaireQuestionOptionForm> optionForms = options.stream()
                .map(option -> {
                    QuestionnaireQuestionOptionForm optionForm = new QuestionnaireQuestionOptionForm();
                    optionForm.setOptionId(option.getId());
                    optionForm.setDecisionType(option.getDecisionType());
                    optionForm.setText(option.getText());
                    optionForm.setDecisionId(option.getDecision());
                    if (option.getDecisionType() == DecisionType.QUESTION) {
                        QuestionnaireQuestionResource optionQuestion = questionnaireQuestionRestService.get(option.getDecision()).getSuccess();
                        optionForm.setQuestionTitle(optionQuestion.getTitle());
                    } else if (option.getDecisionType() == DecisionType.TEXT_OUTCOME) {
                        QuestionnaireTextOutcomeResource textOutcome = questionnaireTextOutcomeRestService.get(option.getDecision()).getSuccess();
                        optionForm.setTextOutcome(textOutcome.getText());
                    } else {
                        throw new IFSRuntimeException("Unknown deciion type " + option.getDecisionType());
                    }
                    return optionForm;
                })
                .collect(Collectors.toList());

        form.setOptions(optionForms);
        return form;
    }
}
