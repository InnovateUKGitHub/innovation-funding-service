package org.innovateuk.ifs.application.finance.model;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.time.ZonedDateTime;

public class QuestionStatusModel {
    private Long id;

    private Boolean markedAsComplete;

    private ProcessRoleResource markedAsCompleteBy;

    private QuestionResource question;

    private ProcessRoleResource assignee;
    private ZonedDateTime assignedDate;

    private ApplicationResource application;

    private ProcessRoleResource assignedBy;
    private Boolean notified;

    public QuestionStatusModel() {
    	// no-arg constructor
    }

    public QuestionStatusModel(QuestionResource question,  ApplicationResource application, ProcessRoleResource markedAsCompleteBy, Boolean markedAsComplete) {
        this.application = application;
        this.markedAsComplete = markedAsComplete;
        this.markedAsCompleteBy = markedAsCompleteBy;
        this.question = question;
    }

    public QuestionStatusModel(QuestionResource question, ApplicationResource application, ProcessRoleResource assignee, ProcessRoleResource assignedBy, ZonedDateTime assignedDate) {
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

    public ProcessRoleResource getMarkedAsCompleteBy() {
        return markedAsCompleteBy;
    }

    public ProcessRoleResource getAssignee() {
        return assignee;
    }

    public ProcessRoleResource getAssignedBy() {
        return assignedBy;
    }

    public ZonedDateTime getAssignedDate() {
        return assignedDate;
    }

    public void setAssignee(ProcessRoleResource assignee, ProcessRoleResource assignedBy, ZonedDateTime assignedDate) {
        this.assignedDate = assignedDate;
        this.assignee = assignee;
        this.assignedBy = assignedBy;
        this.notified = false;
    }

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }

    public QuestionResource getQuestion() {
        return this.question;
    }

    public ApplicationResource getApplication() {
        return this.application;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMarkedAsComplete(Boolean markedAsComplete) {
        this.markedAsComplete = markedAsComplete;
    }

    public void setMarkedAsCompleteBy(ProcessRoleResource markedAsCompleteBy) {
        this.markedAsCompleteBy = markedAsCompleteBy;
    }
}
