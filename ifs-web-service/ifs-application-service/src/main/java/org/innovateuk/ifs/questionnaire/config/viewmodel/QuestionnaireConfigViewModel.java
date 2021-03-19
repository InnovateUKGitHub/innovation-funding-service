package org.innovateuk.ifs.questionnaire.config.viewmodel;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireConfigViewModel {

    private final long questionnaireId;

    private final QuestionnaireQuestionListItem firstQuestion;

    private final List<QuestionnaireQuestionListItem> linkedQuestions;

    private final List<QuestionnaireQuestionListItem> unlinkedQuestions;

    public QuestionnaireConfigViewModel(long questionnaireId) {
        this.questionnaireId = questionnaireId;
        this.firstQuestion = null;
        this.linkedQuestions = new ArrayList<>();
        this.unlinkedQuestions = new ArrayList<>();
    }
    public QuestionnaireConfigViewModel(long questionnaireId, QuestionnaireQuestionListItem firstQuestion, List<QuestionnaireQuestionListItem> linkedQuestions, List<QuestionnaireQuestionListItem> unlinkedQuestions) {
        this.questionnaireId = questionnaireId;
        this.firstQuestion = firstQuestion;
        this.linkedQuestions = linkedQuestions;
        this.unlinkedQuestions = unlinkedQuestions;
    }

    public long getQuestionnaireId() {
        return questionnaireId;
    }

    public QuestionnaireQuestionListItem getFirstQuestion() {
        return firstQuestion;
    }

    public List<QuestionnaireQuestionListItem> getLinkedQuestions() {
        return linkedQuestions;
    }

    public List<QuestionnaireQuestionListItem> getUnlinkedQuestions() {
        return unlinkedQuestions;
    }
}
