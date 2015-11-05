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

    private String assessmentValue;

    @Column(length=5000)
    private String assessmentFeedback;

    private AssessorFeedback(Response response, ProcessRole assessor) {
        this.response = response;
        this.assessor = assessor;
    }

    public static final AssessorFeedback createForResponseAndAssessor(Response response, ProcessRole assessor) {
        return new AssessorFeedback(response, assessor);
    }

    @SuppressWarnings("unused")
    public AssessorFeedback() {
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
        return assessor != null ? assessor.getId() : assessorId;
    }

    void setAssessor(ProcessRole assessor) {
        this.assessor = assessor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssessorFeedback that = (AssessorFeedback) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (response != null ? !response.equals(that.response) : that.response != null) return false;
        if (assessor != null ? !assessor.equals(that.assessor) : that.assessor != null) return false;
        if (assessorId != null ? !assessorId.equals(that.assessorId) : that.assessorId != null) return false;
        if (assessmentValue != null ? !assessmentValue.equals(that.assessmentValue) : that.assessmentValue != null)
            return false;
        return !(assessmentFeedback != null ? !assessmentFeedback.equals(that.assessmentFeedback) : that.assessmentFeedback != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (response != null ? response.hashCode() : 0);
        result = 31 * result + (assessor != null ? assessor.hashCode() : 0);
        result = 31 * result + (assessorId != null ? assessorId.hashCode() : 0);
        result = 31 * result + (assessmentValue != null ? assessmentValue.hashCode() : 0);
        result = 31 * result + (assessmentFeedback != null ? assessmentFeedback.hashCode() : 0);
        return result;
    }
}
