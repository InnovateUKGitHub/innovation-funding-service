package org.innovateuk.ifs.questionnaire.config.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class QuestionnaireQuestion extends QuestionnaireDecision {

    private int priority;

    private String title;
    private String question;
    private String guidance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="questionnaireId", referencedColumnName="id")
    private Questionnaire questionnaire;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    private List<QuestionnaireOption> options;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
        questionnaire.getQuestions().add(this);
    }
}
