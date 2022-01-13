package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.time.ZonedDateTime;

public class QuestionStatusResource {
    private Long id;

    private Boolean markedAsComplete;
    private Long markedAsCompleteBy;
    private ZonedDateTime markedAsCompleteOn;
    private long markedAsCompleteByUserId;
    private String markedAsCompleteByUserName;
    private Long question;
    private Long assignee;
    private ZonedDateTime assignedDate;
    private Long application;
    private Long assignedBy;
    private Boolean notified;
    private Long markedAsCompleteByOrganisationId;

    // Following are needed by the view
    private String assigneeName;
    private String assignedByName;
    private Long assigneeUserId;
    private long assignedByUserId;

    public QuestionStatusResource() {
    }

    public QuestionStatusResource(QuestionResource question, ApplicationResource application, ProcessRoleResource markedAsCompleteBy, Boolean markedAsComplete) {
        this.application = application.getId();
        this.markedAsComplete = markedAsComplete;
        this.markedAsCompleteBy = markedAsCompleteBy.getId();
        this.question = question.getId();
    }

    public QuestionStatusResource(QuestionResource question, ApplicationResource application, ProcessRoleResource assignee, ProcessRoleResource assignedBy, ZonedDateTime assignedDate) {
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

    public ZonedDateTime getAssignedDate() {
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

    public void setAssignee(ProcessRoleResource assignee, ProcessRoleResource assignedBy, ZonedDateTime assignedDate) {
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

    public void setAssignedDate(ZonedDateTime assignedDate) {
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

    public ZonedDateTime getMarkedAsCompleteOn() {
        return markedAsCompleteOn;
    }

    public void setMarkedAsCompleteOn(ZonedDateTime markedAsCompleteOn) {
        this.markedAsCompleteOn = markedAsCompleteOn;
    }

    public long getMarkedAsCompleteByUserId() {
        return markedAsCompleteByUserId;
    }

    public Long getMarkedAsCompleteByOrganisationId() {
        return markedAsCompleteByOrganisationId;
    }

    public void setMarkedAsCompleteByOrganisationId(Long markedAsCompleteByOrganisationId) {
        this.markedAsCompleteByOrganisationId = markedAsCompleteByOrganisationId;
    }

    public void setMarkedAsCompleteByUserId(long markedAsCompleteByUserId) {
        this.markedAsCompleteByUserId = markedAsCompleteByUserId;
    }

    public String getMarkedAsCompleteByUserName() {
        return markedAsCompleteByUserName;
    }

    public void setMarkedAsCompleteByUserName(String markedAsCompleteByUserName) {
        this.markedAsCompleteByUserName = markedAsCompleteByUserName;
    }
}