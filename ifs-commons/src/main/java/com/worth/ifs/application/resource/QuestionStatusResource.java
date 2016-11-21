package com.worth.ifs.application.resource;

import com.worth.ifs.user.resource.ProcessRoleResource;

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

    // Following are needed by the view
    private String assigneeName;
    private String assignedByName;
    private Long assigneeUserId;
    private long assignedByUserId;


    public QuestionStatusResource() {
    	// no-arg constructor
    }

    public QuestionStatusResource(QuestionResource question, ApplicationResource application, ProcessRoleResource markedAsCompleteBy, Boolean markedAsComplete) {
        this.application = application.getId();
        this.markedAsComplete = markedAsComplete;
        this.markedAsCompleteBy = markedAsCompleteBy.getId();
        this.question = question.getId();
    }

    public QuestionStatusResource(QuestionResource question, ApplicationResource application, ProcessRoleResource assignee, ProcessRoleResource assignedBy, LocalDateTime assignedDate) {
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

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    public void setAssignee(ProcessRoleResource assignee, ProcessRoleResource assignedBy, LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
        this.assignee = assignee.getId();
        this.assignedBy = assignedBy.getId();
        this.notified = false;
    }

    public void setApplication(Long applicationId) {
        this.application = applicationId;
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

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public Long getAssigneeUserId() {
        return assigneeUserId;
    }

    public void setAssigneeUserId(Long assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
    }

    public void setMarkedAsComplete(Boolean markedAsComplete) {
        this.markedAsComplete = markedAsComplete;
    }

    public void setMarkedAsCompleteBy(Long markedAsCompleteBy) {
        this.markedAsCompleteBy = markedAsCompleteBy;
    }

    public void setQuestion(Long question) {
        this.question = question;
    }

    public void setAssignee(Long assignee) {
        this.assignee = assignee;
    }

    public void setAssignedDate(LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssignedByName() {
        return assignedByName;
    }

    public void setAssignedByName(String assignedByName) {
        this.assignedByName = assignedByName;
    }

    public long getAssignedByUserId() {
        return assignedByUserId;
    }

    public void setAssignedByUserId(long assignedByUserId) {
        this.assignedByUserId = assignedByUserId;
    }
}
