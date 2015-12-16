package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.user.domain.ProcessRole;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.worth.ifs.application.domain.AssessorFeedback.createForResponseAndAssessor;

/**
 * Response class defines the model in which the response on a {@link Question} is stored.
 * For each question-application combination {@link Application} there can be a response.
 */
@Entity
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime updateDate;

    @ManyToOne
    @JoinColumn(name="updatedById", referencedColumnName="id")
    private ProcessRole updatedBy;

    @ManyToOne
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    @ManyToOne
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    @OneToMany(mappedBy="response", cascade = CascadeType.ALL)
    private List<AssessorFeedback> responseAssessmentFeedbacks = new ArrayList<>();

    public Response(Long id, LocalDateTime updateDate, ProcessRole updatedBy, Question question, Application app) {
        this.id = id;
        this.updateDate = updateDate;
        this.question = question;
        this.application = app;
        this.updatedBy = updatedBy;
    }

    public Response () {

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

    public Question getQuestion() {
        return question;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public ProcessRole getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(ProcessRole updatedBy) {
        this.updatedBy = updatedBy;
    }

    @JsonIgnore
    public Application getApplication() {
        return application;
    }

    public List<AssessorFeedback> getResponseAssessmentFeedbacks() {
        return responseAssessmentFeedbacks;
    }

    public Optional<AssessorFeedback> getResponseAssessmentForAssessor(ProcessRole assessor) {
        return responseAssessmentFeedbacks.stream().filter(r -> r.getAssessorId().equals(assessor.getId())).findFirst();
    }

    public AssessorFeedback getOrCreateResponseAssessorFeedback(ProcessRole assessor) {
        Optional<AssessorFeedback> existingFeedback = getResponseAssessmentForAssessor(assessor);
        return existingFeedback.map(Function.identity()).orElseGet(() -> {
            AssessorFeedback feedback = createForResponseAndAssessor(this, assessor);
            responseAssessmentFeedbacks.add(feedback);
            return feedback;
        });
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
        Response rhs = (Response) obj;
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
