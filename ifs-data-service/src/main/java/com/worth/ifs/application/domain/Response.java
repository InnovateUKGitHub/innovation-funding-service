package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserApplicationRole;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Response {
    public Response(Long id, LocalDateTime updateDate, String value, Boolean markedAsComplete, UserApplicationRole updatedBy, Question question, Application app) {
        this.id = id;
        this.updateDate = updateDate;
        this.value = value;
        this.markedAsComplete = markedAsComplete;
        this.question = question;
        this.application = app;
        this.updatedBy = updatedBy;
    }

    public Response () {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime updateDate;
    @Column(length=5000)
    private String value;
    private Boolean markedAsComplete;

    @ManyToOne
    @JoinColumn(name="updatedById", referencedColumnName="id")
    private UserApplicationRole updatedBy;

    @ManyToOne
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    @ManyToOne
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    @ManyToOne
    @JoinColumn(name="assigneeId", referencedColumnName="id")
    private User assignee;
    private LocalDateTime assignedDate;


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


    public Boolean isMarkedAsComplete() {
        return (markedAsComplete == true);
    }

    public Question getQuestion() {
        return question;
    }

    public void setMarkedAsComplete(boolean markedAsComplete) {
        this.markedAsComplete = markedAsComplete;
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

    public User getAssignee() {
        return assignee;
    }

    public void setAssignee(User assignee) {
        this.assignee = assignee;
    }

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }

    public UserApplicationRole getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserApplicationRole updatedBy) {
        this.updatedBy = updatedBy;
    }
}
