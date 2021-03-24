package org.innovateuk.ifs.questionnaire.config.domain;

import org.innovateuk.ifs.questionnaire.resource.DecisionType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class QuestionnaireQuestion extends QuestionnaireDecision {

    private int depth = 0;

    private String title;
    private String question;
    private String guidance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="questionnaireId", referencedColumnName="id")
    private Questionnaire questionnaire;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question", cascade = CascadeType.ALL)
    private List<QuestionnaireOption> options = new ArrayList<>();

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
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

    public List<QuestionnaireOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionnaireOption> options) {
        this.options = options;
    }

    @Override
    public DecisionType getDecisionType() {
        return DecisionType.QUESTION;
    }

    public List<QuestionnaireQuestion> getPreviousQuestions() {
        return getOptionsLinkedToThisDecision().stream()
                .map(QuestionnaireOption::getQuestion)
                .collect(Collectors.toList());
    }
}
