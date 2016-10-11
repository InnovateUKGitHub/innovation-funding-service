package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * QuestionStatus defines the model and relations that are stored in the database.
 * This model is used to store metadata related to BOTH {@link Question} and {@link Application}
 */
@Entity
public class QuestionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Boolean markedAsComplete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="markedAsCompleteById", referencedColumnName="id")
    private ProcessRole markedAsCompleteBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assigneeId", referencedColumnName="id")
    private ProcessRole assignee;
    private LocalDateTime assignedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assignedById", referencedColumnName="id")
    private ProcessRole assignedBy;
    private Boolean notified;

    public QuestionStatus() {
    	// no-arg constructor
    }

    public QuestionStatus(Question question,  Application application, ProcessRole markedAsCompleteBy, Boolean markedAsComplete) {
        this.application = application;
        this.markedAsComplete = markedAsComplete;
        this.markedAsCompleteBy = markedAsCompleteBy;
        this.question = question;
    }

    public QuestionStatus(Question question, Application application, ProcessRole assignee, ProcessRole assignedBy, LocalDateTime assignedDate) {
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

    public void setMarkedAsCompleteBy(ProcessRole markedAsCompleteBy) {
        this.markedAsCompleteBy = markedAsCompleteBy;
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

    @JsonIgnore
    public Question getQuestion() {
        return this.question;
    }

    @JsonIgnore
    public Application getApplication() {
        return this.application;
    }
}
