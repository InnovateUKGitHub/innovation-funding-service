package com.worth.ifs.application.domain;

import javax.persistence.*;

/**
 * AssessmentScoreRow defines database relations and a model to use client side and server side.
 */
@Entity
public class AssessmentScoreRow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_assessment_id", referencedColumnName = "id")
    private QuestionAssessment questionAssessment;

    private Integer start;

    private Integer end;

    private String justification;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuestionAssessment getQuestionAssessment() {
        return questionAssessment;
    }

    public void setQuestionAssessment(QuestionAssessment questionAssessment) {
        this.questionAssessment = questionAssessment;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}
