package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AssessorFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="responseId", referencedColumnName="id")
    private Response response;

    @ManyToOne
    @JoinColumn(name="assessorId", referencedColumnName="id")
    private ProcessRole assessor;

    @Column(name = "assessorId", insertable = false, updatable = false)
    private Long assessorId;

    // TODO DW - for Alpha, storing the Assessor's score against a Response.  In Beta, the Assessor will
    // probably be assessing ALL responses for a question at the same time, at which point a new table
    // will be needed, like "question_response_set" or "consortium_response", that links a question to a
    // set of responses and also allows storing of scores against it
    private String assessmentValue;

    @Column(length=5000) // TODO DW - this column will probably move along with assessmentScore
    private String assessmentFeedback;

    private AssessorFeedback(Response response, ProcessRole assessor) {
        this.response = response;
        this.assessor = assessor;
    }

    public static final AssessorFeedback createForResponseAndAssessor(Response response, ProcessRole assessor) {
        return new AssessorFeedback(response, assessor);
    }

    @SuppressWarnings("unused")
    AssessorFeedback() {
        // for ORM user
    }

    public Long getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public String getAssessmentValue() { return assessmentValue; }

    @SuppressWarnings("unused")
    public String getAssessmentFeedback() {
        return assessmentFeedback;
    }

    public void setAssessmentValue(String assessmentValue) {
        this.assessmentValue = assessmentValue;
    }

    public void setAssessmentFeedback(String assessmentFeedback) {
        this.assessmentFeedback = assessmentFeedback;
    }

    @JsonIgnore
    public Response getResponse() {
        return response;
    }

    public Long getAssessorId() {
        return assessorId;
    }


}
