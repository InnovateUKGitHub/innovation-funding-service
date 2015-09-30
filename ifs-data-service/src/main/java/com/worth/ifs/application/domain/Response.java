package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime updateDate;

    @Column(length=5000)
    private String value;

    // TODO DW - for Alpha, storing the Assessor's score against a Response.  In Beta, the Assessor will
    // probably be assessing ALL responses for a question at the same time, at which point a new table
    // will be needed, like "question_response_set" or "consortium_response", that links a question to a
    // set of responses and also allows storing of scores against it
    private Integer assessmentScore;

    @ManyToOne
    @JoinColumn(name="updatedById", referencedColumnName="id")
    private ProcessRole updatedBy;

    @ManyToOne
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    @ManyToOne
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

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

    public Integer getAssessmentScore() { return assessmentScore; }

    public void setAssessmentScore(Integer assessmentScore) { this.assessmentScore = assessmentScore; }

    public void setId(Long id) { this.id = id; }
}
