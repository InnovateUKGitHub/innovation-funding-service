package org.innovateuk.ifs.questionnaire.response.viewmodel;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireOptionResource;
import org.innovateuk.ifs.questionnaire.resource.QuestionnaireQuestionResource;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionnaireQuestionViewModel {

    private String questionnaireResponseId;
    private long questionnaireQuestionId;
    private String title;
    private String subtitle;
    private String question;
    private String guidance;
    private List<Pair<Long, String>> options;
    private AnswerTableViewModel previousQuestions;

    public QuestionnaireQuestionViewModel(String questionnaireResponseId, String subtitle, QuestionnaireQuestionResource question, List<QuestionnaireOptionResource> options, AnswerTableViewModel previousQuestions) {
        this.questionnaireResponseId = questionnaireResponseId;
        this.subtitle = subtitle;
        this.questionnaireQuestionId = question.getId();
        this.title = question.getTitle();
        this.question = question.getQuestion();
        this.guidance = question.getGuidance();
        this.options = options.stream()
                .map(option -> Pair.of(option.getId(), option.getText()))
                .collect(Collectors.toList());
        this.previousQuestions = previousQuestions;
    }

    public String getQuestionnaireResponseId() {
        return questionnaireResponseId;
    }

    public String getSubtitle() {
        return subtitle;
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

    public AnswerTableViewModel getPreviousQuestions() {
        return previousQuestions;
    }
}
