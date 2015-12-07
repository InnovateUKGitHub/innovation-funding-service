package com.worth.ifs.workflow.domain;

import javax.persistence.*;

@Entity
public class ProcessOutcome {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String outcome;
    private String description;
    private String comment;

    public ProcessOutcome() {
    }

    public ProcessOutcome(String outcome, String description, String comment) {
        this.outcome = outcome;
        this.description = description;
        this.comment = comment;
    }

    @ManyToOne
    @JoinColumn(name="processId", referencedColumnName="id")
    Process process;

    private String outcomeType;

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOutcomeType() {
        return outcomeType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
