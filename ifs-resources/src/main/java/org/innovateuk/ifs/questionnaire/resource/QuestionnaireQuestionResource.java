package org.innovateuk.ifs.questionnaire.resource;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireQuestionResource extends QuestionnaireDecisionResource {

    private int depth;
    private QuestionnaireSecurityType securityType;

    private String title;
    private String question;
    private String guidance;

    private Long questionnaire;

    private List<Long> options = new ArrayList<>();

    private List<Long> previousQuestions = new ArrayList<>();

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public QuestionnaireSecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(QuestionnaireSecurityType securityType) {
        this.securityType = securityType;
    }

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

    public Long getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Long questionnaire) {
        this.questionnaire = questionnaire;
    }

    public List<Long> getOptions() {
        return options;
    }

    public void setOptions(List<Long> options) {
        this.options = options;
    }

    public List<Long> getPreviousQuestions() {
        return previousQuestions;
    }

    public void setPreviousQuestions(List<Long> previousQuestions) {
        this.previousQuestions = previousQuestions;
    }
}
