package com.worth.ifs.application.resource;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.user.domain.ProcessRole;
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

    public ResponseResource(Long id, LocalDateTime updateDate, ProcessRole updatedBy, Question question, Application app) {
        this.id = id;
        this.updateDate = updateDate;
        this.question = question.getId();
        this.application = app.getId();
        this.updatedBy = updatedBy.getId();
    }

    public ResponseResource() {

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

    public void setApplication(Application application) {
        this.application = application.getId();
    }

    public void setQuestion(Question question) {
        this.question = question.getId();
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(ProcessRole updatedBy) {
        this.updatedBy = updatedBy.getId();
    }

    public Long getApplication() {
        return application;
    }

    public List<Long> getResponseAssessmentFeedbacks() {
        return responseAssessmentFeedbacks;
    }

    /*public Optional<AssessorFeedback> getResponseAssessmentForAssessor(ProcessRole assessor) {
        return responseAssessmentFeedbacks.stream().filter(r -> r.getAssessorId().equals(assessor.getId())).findFirst();
    }

    public AssessorFeedback getOrCreateResponseAssessorFeedback(ProcessRole assessor) {
        Optional<AssessorFeedback> existingFeedback = getResponseAssessmentForAssessor(assessor);
        return existingFeedback.map(Function.identity()).orElseGet(() -> {
            AssessorFeedback feedback = createForResponseAndAssessor(this, assessor);
            responseAssessmentFeedbacks.add(feedback);
            return feedback;
        });
    }*/


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
