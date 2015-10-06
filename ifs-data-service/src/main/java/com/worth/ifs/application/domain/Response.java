package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.worth.ifs.application.domain.ResponseAssessorFeedback.createForResponseAndAssessor;
import static com.worth.ifs.util.IfsFunctionUtils.ifPresent;

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

    @OneToMany(mappedBy="response")
    private List<ResponseAssessorFeedback> responseAssessmentFeedbacks;

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

    public void setId(Long id) { this.id = id; }

    public Application getApplication() {
        return application;
    }

    public List<ResponseAssessorFeedback> getResponseAssessmentFeedbacks() {
        return responseAssessmentFeedbacks;
    }

    public Optional<ResponseAssessorFeedback> getResponseAssessmentForAssessor(ProcessRole assessor) {
        return responseAssessmentFeedbacks.stream().filter(r -> r.getAssessor().equals(assessor)).findFirst();
    }

    public ResponseAssessorFeedback getOrCreateResponseAssessorFeedback(ProcessRole assessor) {
        Optional<ResponseAssessorFeedback> existingFeedback = getResponseAssessmentForAssessor(assessor);
        return ifPresent(existingFeedback, Function.identity()).orElseGet(() -> {
            ResponseAssessorFeedback feedback = createForResponseAndAssessor(this, assessor);
            responseAssessmentFeedbacks.add(feedback);
            return feedback;
        });
    }
}
