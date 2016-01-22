package com.worth.ifs.application.resource;

import com.worth.ifs.application.domain.Response;
import com.worth.ifs.user.domain.ProcessRole;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AssessorFeedbackResource {

    private Long id;
    private Long response;
    private Long assessor;
    private String assessmentValue;
    private String assessmentFeedback;

    private AssessorFeedbackResource(Response response, ProcessRole assessor) {
        this.response = response.getId();
        this.assessor = assessor.getId();
    }

    public static AssessorFeedbackResource createForResponseAndAssessor(Response response, ProcessRole assessor) {
        return new AssessorFeedbackResource(response, assessor);
    }

    @SuppressWarnings("unused")
    public AssessorFeedbackResource() {
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

    public Long getResponse() {
        return response;
    }

    public Long getAssessorId() {
        return assessor;
    }

    void setAssessor(ProcessRole assessor) {
        this.assessor = assessor.getId();
    }

    public Integer getWordCount(){
        return assessmentFeedback != null ? assessmentFeedback.split("\\s+").length : 0;
    }

    public Integer getWordCountLeft(){
        return 350 - this.getWordCount();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        AssessorFeedbackResource rhs = (AssessorFeedbackResource) obj;
        return new EqualsBuilder()
            .append(this.id, rhs.id)
            .append(this.response, rhs.response)
            .append(this.assessor, rhs.assessor)
            .append(this.assessmentValue, rhs.assessmentValue)
            .append(this.assessmentFeedback, rhs.assessmentFeedback)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(id)
            .append(response)
            .append(assessor)
            .append(assessmentValue)
            .append(assessmentFeedback)
            .toHashCode();
    }
}
