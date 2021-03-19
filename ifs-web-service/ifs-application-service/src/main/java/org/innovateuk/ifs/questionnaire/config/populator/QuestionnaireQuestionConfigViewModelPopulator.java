package org.innovateuk.ifs.questionnaire.config.populator;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireQuestionRestService;
import org.innovateuk.ifs.questionnaire.config.service.QuestionnaireRestService;
import org.innovateuk.ifs.questionnaire.config.viewmodel.QuestionnaireQuestionConfigViewModel;
import org.innovateuk.ifs.questionnaire.config.viewmodel.QuestionnaireQuestionListItem;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Component
public class QuestionnaireQuestionConfigViewModelPopulator {

    @Autowired
    private QuestionnaireRestService questionnaireRestService;

    @Autowired
    private QuestionnaireQuestionRestService questionnaireQuestionRestService;

    public QuestionnaireQuestionConfigViewModel populate(long questionnaireId, QuestionnaireQuestionResource question) {
        QuestionnaireResource questionnaire = questionnaireRestService.get(questionnaireId).getSuccess();
        List<QuestionnaireQuestionResource> allQuestions = questionnaireQuestionRestService.get(questionnaire.getQuestions())
                .getSuccess();
        QuestionnaireQuestionResource questionInList = findInList(allQuestions, question);
        boolean linked = !questionInList.getPreviousQuestions().isEmpty();
        boolean first = allQuestions.indexOf(questionInList) == 0;
        List<QuestionnaireQuestionListItem> availableQuestions;
        List<QuestionnaireQuestionListItem> previousQuestions;
        if (linked || first) {
            Map<Long, QuestionnaireQuestionResource> indexedQuestions = allQuestions
                    .stream()
                    .collect(toMap(QuestionnaireQuestionResource::getId, Function.identity()));
            List<QuestionnaireQuestionResource> questionsInThisTree = new ArrayList<>();
            addRecusively(questionInList, questionsInThisTree, indexedQuestions);
            availableQuestions = allQuestions.stream()
                    .filter(q -> !questionsInThisTree.contains(q))
                    .map(q -> new QuestionnaireQuestionListItem(q.getId(), q.getTitle()))
                    .collect(Collectors.toList());
            previousQuestions = questionInList.getPreviousQuestions().stream()
                    .map(indexedQuestions::get)
                    .map(q -> new QuestionnaireQuestionListItem(q.getId(), q.getTitle()))
                    .collect(Collectors.toList());
        } else {
            availableQuestions = new ArrayList<>();
            previousQuestions = new ArrayList<>();
        }
        return new QuestionnaireQuestionConfigViewModel(questionnaireId, first, linked, availableQuestions, previousQuestions);
    }

    private void addRecusively(QuestionnaireQuestionResource question, List<QuestionnaireQuestionResource> questionsInThisTree, Map<Long, QuestionnaireQuestionResource> indexedQuestions) {
        questionsInThisTree.add(question);
        if (question.getPreviousQuestions().isEmpty()) {
            return;
        }
        question.getPreviousQuestions().forEach(id -> addRecusively(indexedQuestions.get(id), questionsInThisTree, indexedQuestions));
    }

    private QuestionnaireQuestionResource findInList(List<QuestionnaireQuestionResource> allQuestions,  QuestionnaireQuestionResource question) {
        return allQuestions.stream().filter(q -> q.getId().equals(question.getId())).findFirst().orElseThrow(ObjectNotFoundException::new);
    }

}
