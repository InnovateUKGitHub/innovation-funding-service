package com.worth.ifs.application.domain;

import com.worth.ifs.user.domain.ProcessRole;

import javax.persistence.*;

/**
 * An entity representing an Assessor's assessment of a given Response to an assessable question.
 *
 * The idea is that multiple Assessors can score a single Response.
 *
 * Created by dwatson on 05/10/15.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"responseId", "assessorId"}))
public class ResponseAssessorFeedback {

    @ManyToOne
    @JoinColumn(name="responseId", referencedColumnName="id")
    private Response response;

    @ManyToOne
    @JoinColumn(name="assessorId", referencedColumnName="id")
    private ProcessRole assessor;

    // TODO DW - for Alpha, storing the Assessor's score against a Response.  In Beta, the Assessor will
    // probably be assessing ALL responses for a question at the same time, at which point a new table
    // will be needed, like "question_response_set" or "consortium_response", that links a question to a
    // set of responses and also allows storing of scores against it
    private Integer assessmentValue;

    @Column(length=5000) // TODO DW - this column will probably move along with assessmentScore
    private String assessmentFeedback;

    private ResponseAssessorFeedback(Response response, ProcessRole assessor) {
        this.response = response;
        this.assessor = assessor;
    }

    public static final ResponseAssessorFeedback createForResponseAndAssessor(Response response, ProcessRole assessor) {
        return new ResponseAssessorFeedback(response, assessor);
    }

    @SuppressWarnings("unused")
    ResponseAssessorFeedback() {
        // for ORM user
    }

    @SuppressWarnings("unused")
    public Integer getAssessmentValue() { return assessmentValue; }

    @SuppressWarnings("unused")
    public String getAssessmentFeedback() {
        return assessmentFeedback;
    }

    public void setAssessmentValue(Integer assessmentValue) {
        this.assessmentValue = assessmentValue;
    }

    public void setAssessmentFeedback(String assessmentFeedback) {
        this.assessmentFeedback = assessmentFeedback;
    }

    public Response getResponse() {
        return response;
    }

    public ProcessRole getAssessor() {
        return assessor;
    }


}
