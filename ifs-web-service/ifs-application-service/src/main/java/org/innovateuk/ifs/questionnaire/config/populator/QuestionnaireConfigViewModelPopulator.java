package org.innovateuk.ifs.questionnaire.config.populator;

import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireRestService;
import org.innovateuk.ifs.questionnaire.config.viewmodel.QuestionnaireConfigViewModel;
import org.innovateuk.ifs.questionnaire.config.viewmodel.QuestionnaireQuestionListItem;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionnaireConfigViewModelPopulator {

    @Autowired
    private QuestionnaireRestService questionnaireRestService;

    @Autowired
    private QuestionnaireQuestionRestService questionnaireQuestionRestService;

    public QuestionnaireConfigViewModel populate(long questionnaireId) {
        QuestionnaireResource questionnaire = questionnaireRestService.get(questionnaireId).getSuccess();
        if (questionnaire.getQuestions().isEmpty()) {
            return new QuestionnaireConfigViewModel(questionnaireId);
        }
        List<QuestionnaireQuestionResource> questions = questionnaireQuestionRestService.get(questionnaire.getQuestions()).getSuccess();
        QuestionnaireQuestionListItem firstQuestion = toListItem(questions.get(0));
        questions.remove(0);

        List<QuestionnaireQuestionListItem> linkedQuestions = new ArrayList<>();
        List<QuestionnaireQuestionListItem> unlinkedQuestions = new ArrayList<>();

        for (QuestionnaireQuestionResource question : questions) {
            if (question.getPreviousQuestions().isEmpty()) {
                unlinkedQuestions.add(toListItem(question));
            } else {
                linkedQuestions.add(toListItem(question));
            }
        }

        return new QuestionnaireConfigViewModel(questionnaireId, firstQuestion, linkedQuestions, unlinkedQuestions);
    }


    private QuestionnaireQuestionListItem toListItem(QuestionnaireQuestionResource question) {
        return new QuestionnaireQuestionListItem(question.getId(), question.getTitle());
    }
}
