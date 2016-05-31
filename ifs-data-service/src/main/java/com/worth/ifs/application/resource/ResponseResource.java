package com.worth.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ResponseResource {
    private Long id;
    private LocalDateTime updateDate;
    private Long updatedBy;
    private Long question;
    private Long application;
    private List<Long> responseAssessmentFeedbacks = new ArrayList<>();

    public ResponseResource(Long id, LocalDateTime updateDate, Long updatedBy, Long questionId, ApplicationResource app) {
        this.id = id;
        this.updateDate = updateDate;
        this.question = questionId;
        this.application = app.getId();
        this.updatedBy = updatedBy;
    }

    public ResponseResource() {
    	// no-arg constructor
    }

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public Long getQuestion() {
        return question;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application.getId();
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public Long getApplication() {
        return application;
    }

    public List<Long> getResponseAssessmentFeedbacks() {
        return responseAssessmentFeedbacks;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }

    public void setApplication(Long application) {
        this.application = application;
    }

    public void setResponseAssessmentFeedbacks(List<Long> responseAssessmentFeedbacks) {
        this.responseAssessmentFeedbacks = responseAssessmentFeedbacks;
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
        ResponseResource rhs = (ResponseResource) obj;
        return new EqualsBuilder()
            .append(this.id, rhs.id)
            .append(this.updateDate, rhs.updateDate)
            .append(this.updatedBy, rhs.updatedBy)
            .append(this.application, rhs.application)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(id)
            .append(updateDate)
            .append(updatedBy)
            .append(application)
            .toHashCode();
    }
}
