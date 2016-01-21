package com.worth.ifs.application.resource;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.user.domain.ProcessRole;

import java.time.LocalDateTime;

public class QuestionStatusResource {
    private Long id;

    private Boolean markedAsComplete;
    private Long markedAsCompleteBy;
    private Long question;
    private Long assignee;
    private LocalDateTime assignedDate;
    private Long application;
    private Long assignedBy;
    private Boolean notified;

    public QuestionStatusResource() {
    }

    public QuestionStatusResource(Question question, Application application, ProcessRole markedAsCompleteBy, Boolean markedAsComplete) {
        this.application = application.getId();
        this.markedAsComplete = markedAsComplete;
        this.markedAsCompleteBy = markedAsCompleteBy.getId();
        this.question = question.getId();
    }

    public QuestionStatusResource(Question question, Application application, ProcessRole assignee, ProcessRole assignedBy, LocalDateTime assignedDate) {
        this.question = question.getId();
        this.application = application.getId();
        this.assignee = assignee.getId();
        this.assignedDate = assignedDate;
        this.assignedBy = assignedBy.getId();
        this.notified = false;
    }

    public Long getId() {
        return id;
    }

    public Boolean getMarkedAsComplete() {
        return markedAsComplete;
    }

    public Long getMarkedAsCompleteBy() {
        return markedAsCompleteBy;
    }

    public Long getAssignee() {
        return assignee;
    }

    public Long getAssignedBy() {
        return assignedBy;
    }

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void markAsComplete() {
        this.markedAsComplete = true;
    }

    public void markAsInComplete() {
        this.markedAsComplete = false;
    }

    public void setAssignee(ProcessRole assignee, ProcessRole assignedBy, LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
        this.assignee = assignee.getId();
        this.assignedBy = assignedBy.getId();
        this.notified = false;
    }

    public void setApplication(Application application) {
        this.application = application.getId();
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public Long getQuestion() {
        return this.question;
    }

    public Long getApplication() {
        return this.application;
    }
}
