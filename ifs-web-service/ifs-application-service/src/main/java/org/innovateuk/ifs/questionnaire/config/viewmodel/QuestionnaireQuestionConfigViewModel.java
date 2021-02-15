package org.innovateuk.ifs.questionnaire.config.viewmodel;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireQuestionConfigViewModel {

    private final long questionnaireId;
    private final boolean create;
    private final boolean linked;
    private final List<QuestionnaireQuestionListItem> availableQuestions;
    private final List<QuestionnaireQuestionListItem> previousQuestions;

    private QuestionnaireQuestionConfigViewModel(long questionnaireId) {
        this(questionnaireId, true, false, new ArrayList<>(), new ArrayList<>());
    }

    public QuestionnaireQuestionConfigViewModel(long questionnaireId, boolean linked, List<QuestionnaireQuestionListItem> availableQuestions, List<QuestionnaireQuestionListItem> previousQuestions) {
        this(questionnaireId, false, linked, availableQuestions, previousQuestions);
    }

    private  QuestionnaireQuestionConfigViewModel(long questionnaireId, boolean create, boolean linked, List<QuestionnaireQuestionListItem> availableQuestions, List<QuestionnaireQuestionListItem> previousQuestions) {
        this.questionnaireId = questionnaireId;
        this.create = create;
        this.linked = linked;
        this.availableQuestions = availableQuestions;
        this.previousQuestions = previousQuestions;
    }

    public long getQuestionnaireId() {
        return questionnaireId;
    }

    public boolean isCreate() {
        return create;
    }

    public boolean isLinked() {
        return linked;
    }

    public List<QuestionnaireQuestionListItem> getAvailableQuestions() {
        return availableQuestions;
    }

    public List<QuestionnaireQuestionListItem> getPreviousQuestions() {
        return previousQuestions;
    }

    public static QuestionnaireQuestionConfigViewModel aCreateViewModel(long questionnaireId) {
        return new QuestionnaireQuestionConfigViewModel(questionnaireId);
    }
}
