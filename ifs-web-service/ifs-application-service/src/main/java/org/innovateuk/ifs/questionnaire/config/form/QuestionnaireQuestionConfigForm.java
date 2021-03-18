package org.innovateuk.ifs.questionnaire.config.form;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireQuestionConfigForm {

    private String title;
    private String question;
    private String guidance;

    private List<QuestionnaireQuestionOptionForm> options = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getGuidance() {
        return guidance;
    }

    public void setGuidance(String guidance) {
        this.guidance = guidance;
    }

    public List<QuestionnaireQuestionOptionForm> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionnaireQuestionOptionForm> options) {
        this.options = options;
    }
}
