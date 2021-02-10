package org.innovateuk.ifs.questionnaire.viewmodel;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionnaireQuestionViewModel {

    private long questionnaireId;
    private long questionnaireQuestionId;
    private String title;
    private String question;
    private String guidance;
    private List<Pair<Long, String>> options;
    private List<PreviousQuestionViewModel> previousQuestions;

    public QuestionnaireQuestionViewModel(long questionnaireId, QuestionnaireQuestionResource question, List<QuestionnaireOptionResource> options, List<PreviousQuestionViewModel> previousQuestions) {
        this.questionnaireId = questionnaireId;
        this.questionnaireQuestionId = question.getId();
        this.title = question.getTitle();
        this.question = question.getQuestion();
        this.guidance = question.getGuidance();
        this.options = options.stream()
                .map(option -> Pair.of(option.getId(), option.getText()))
                .collect(Collectors.toList());
        this.previousQuestions = previousQuestions;
    }

    public long getQuestionnaireId() {
        return questionnaireId;
    }

    public long getQuestionnaireQuestionId() {
        return questionnaireQuestionId;
    }

    public String getTitle() {
        return title;
    }

    public String getQuestion() {
        return question;
    }

    public String getGuidance() {
        return guidance;
    }

    public List<Pair<Long, String>> getOptions() {
        return options;
    }

    public List<PreviousQuestionViewModel> getPreviousQuestions() {
        return previousQuestions;
    }
}
