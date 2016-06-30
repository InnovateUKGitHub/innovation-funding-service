package com.worth.ifs.assessment.domain;

import com.worth.ifs.application.domain.Question;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Represents feedback given as part of the assessment journey to a question for an application.
 */
@Entity
public class AssessmentFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name="assessmentId", referencedColumnName="id")
    private Assessment assessment;

    @Lob
    @NotNull
    private String feedback;

    private Integer score;

    @NotNull
    @ManyToOne
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
