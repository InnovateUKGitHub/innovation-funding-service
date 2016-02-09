package com.worth.ifs.application.model;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.user.domain.ProcessRole;

import java.time.LocalDateTime;

/**
 * Created by rav on 01/02/2016.
 *
 */
public class QuestionStatusModel {
    private Long id;

    private Boolean markedAsComplete;

    private ProcessRole markedAsCompleteBy;

    private Question question;

    private ProcessRole assignee;
    private LocalDateTime assignedDate;

    private Application application;

    private ProcessRole assignedBy;
    private Boolean notified;

    public QuestionStatusModel() {
    }

    public QuestionStatusModel(Question question,  Application application, ProcessRole markedAsCompleteBy, Boolean markedAsComplete) {
        this.application = application;
        this.markedAsComplete = markedAsComplete;
        this.markedAsCompleteBy = markedAsCompleteBy;
        this.question = question;
    }

    public QuestionStatusModel(Question question, Application application, ProcessRole assignee, ProcessRole assignedBy, LocalDateTime assignedDate) {
        this.question = question;
        this.application = application;
        this.assignee = assignee;
        this.assignedDate = assignedDate;
        this.assignedBy = assignedBy;
        this.notified = false;
    }

    public Long getId() {
        return id;
    }

    public Boolean getMarkedAsComplete() {
        return markedAsComplete;
    }

    public ProcessRole getMarkedAsCompleteBy() {
        return markedAsCompleteBy;
    }

    public ProcessRole getAssignee() {
        return assignee;
    }

    public ProcessRole getAssignedBy() {
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
        this.assignee = assignee;
        this.assignedBy = assignedBy;
        this.notified = false;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public Question getQuestion() {
        return this.question;
    }

    public Application getApplication() {
        return this.application;
    }
}
