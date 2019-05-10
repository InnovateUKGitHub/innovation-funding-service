package org.innovateuk.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.form.domain.Question;
import org.innovateuk.ifs.user.domain.ProcessRole;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * QuestionStatus defines the model and relations that are stored in the database.
 * This model is used to store metadata related to BOTH {@link Question} and {@link Application}
 */
@Entity
public class QuestionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean markedAsComplete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "markedAsCompleteById", referencedColumnName = "id")
    private ProcessRole markedAsCompleteBy;

    private ZonedDateTime markedAsCompleteOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId", referencedColumnName = "id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigneeId", referencedColumnName = "id")
    private ProcessRole assignee;
    private ZonedDateTime assignedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationId", referencedColumnName = "id")
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignedById", referencedColumnName = "id")
    private ProcessRole assignedBy;
    private Boolean notified;

    public QuestionStatus() {
    }

    public QuestionStatus(Question question, Application application) {
        this.application = application;
        this.question = question;
    }

    public QuestionStatus(Question question, Application application, ProcessRole assignee, ProcessRole assignedBy, ZonedDateTime assignedDate) {
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

    public ZonedDateTime getAssignedDate() {
        return assignedDate;
    }

    public QuestionStatus markAsComplete(ProcessRole markedAsCompleteBy, ZonedDateTime markedAsCompleteOn) {
        this.markedAsComplete = true;
        this.markedAsCompleteBy = markedAsCompleteBy;
        this.markedAsCompleteOn = markedAsCompleteOn;
        return this;
    }

    public void markAsInComplete() {
        this.markedAsComplete = false;
    }

    public void setAssignee(ProcessRole assignee, ProcessRole assignedBy, ZonedDateTime assignedDate) {
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
    public Long getOrganisationId() {
        return this.markedAsCompleteBy.getOrganisationId();
    }

    @JsonIgnore
    public Application getApplication() {
        return this.application;
    }

    public ZonedDateTime getMarkedAsCompleteOn() {
        return markedAsCompleteOn;
    }
}