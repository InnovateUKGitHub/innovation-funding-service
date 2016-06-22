package com.worth.ifs.assessment.domain;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.user.domain.ProcessRole;

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
    @JoinColumn(name="processRoleId", referencedColumnName="id")
    private ProcessRole processRole;

    @Lob
    @NotNull
    private String feedback;

    @NotNull
    private Integer score;

    @NotNull
    @ManyToOne
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public ProcessRole getProcessRole() {
        return processRole;
    }

    public void setProcessRole(final ProcessRole processRole) {
        this.processRole = processRole;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(final String feedback) {
        this.feedback = feedback;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(final Integer score) {
        this.score = score;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(final Question question) {
        this.question = question;
    }
}
