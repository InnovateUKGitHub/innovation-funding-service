package org.innovateuk.ifs.questionnaire.config.domain;

import javax.persistence.*;

@Entity
public class QuestionnaireOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="decisionId", referencedColumnName="id")
    private QuestionnaireDecision decision;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="questionId", referencedColumnName="id")
    private QuestionnaireQuestion question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionnaireDecision getDecision() {
        return decision;
    }

    public void setDecision(QuestionnaireDecision decision) {
        this.decision = decision;
    }
}
