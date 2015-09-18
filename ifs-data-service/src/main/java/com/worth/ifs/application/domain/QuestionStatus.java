package com.worth.ifs.application.domain;

import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class QuestionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Boolean markedAsComplete;

    @ManyToOne
    @JoinColumn(name="markedAsCompleteById", referencedColumnName="id")
    private ProcessRole markedAsCompleteBy;

    @ManyToOne
    @JoinColumn(name="questionId", referencedColumnName="id")
    private Question question;

    @ManyToOne
    @JoinColumn(name="assigneeId", referencedColumnName="id")
    private ProcessRole assignee;
    private LocalDateTime assignedDate;

    public QuestionStatus() {
    }

    public QuestionStatus(Question question,  ProcessRole markedAsCompleteBy, Boolean markedAsComplete) {
        this.markedAsComplete = markedAsComplete;
        this.markedAsCompleteBy = markedAsCompleteBy;
        this.question = question;
    }

    public QuestionStatus(Question question, ProcessRole assignee, LocalDateTime assignedDate) {
        this.question = question;
        this.assignee = assignee;
        this.assignedDate = assignedDate;
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

    public LocalDateTime getAssignedDate() {
        return assignedDate;
    }

    public void markAsComplete() {
        this.markedAsComplete = true;
    }

    public void markAsInComplete() {
        this.markedAsComplete = false;
    }

    public void setAssignee(ProcessRole assignee, LocalDateTime assignedDate) {
        this.assignedDate = assignedDate;
        this.assignee = assignee;
    }
}
