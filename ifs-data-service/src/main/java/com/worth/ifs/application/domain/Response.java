package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    @Column(length=5000)
    private String value;

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
    private List<AssessorFeedback> responseAssessmentFeedbacks;

    public Response(Long id, LocalDateTime updateDate, String value, ProcessRole updatedBy, Question question, Application app) {
        this.id = id;
        this.updateDate = updateDate;
        this.value = value;
        this.question = question;
        this.application = app;
        this.updatedBy = updatedBy;
    }

    public Response () {

    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public String getValue() {
        return value;
    }

    @JsonIgnore
    public Integer getWordCount(){
        return Integer.valueOf(this.value.split("\\s+").length);
    }

    @JsonIgnore
    public Integer getWordCountLeft(){
        return Integer.valueOf(question.getWordCount() - this.getWordCount());
    }


    public Question getQuestion() {
        return question;
    }

    public void setValue(String value) {
        this.value = value;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Response response = (Response) o;

        if (id != null ? !id.equals(response.id) : response.id != null) return false;
        if (updateDate != null ? !updateDate.equals(response.updateDate) : response.updateDate != null) return false;
        if (value != null ? !value.equals(response.value) : response.value != null) return false;
        if (updatedBy != null ? !updatedBy.equals(response.updatedBy) : response.updatedBy != null) return false;
        if (question != null ? !question.equals(response.question) : response.question != null) return false;
        if (application != null ? !application.equals(response.application) : response.application != null)
            return false;
        return !(responseAssessmentFeedbacks != null ? !responseAssessmentFeedbacks.equals(response.responseAssessmentFeedbacks) : response.responseAssessmentFeedbacks != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (updatedBy != null ? updatedBy.hashCode() : 0);
        result = 31 * result + (question != null ? question.hashCode() : 0);
        result = 31 * result + (application != null ? application.hashCode() : 0);
        result = 31 * result + (responseAssessmentFeedbacks != null ? responseAssessmentFeedbacks.hashCode() : 0);
        return result;
    }
}
