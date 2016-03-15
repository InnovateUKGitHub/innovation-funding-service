package com.worth.ifs.workflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class ProcessOutcome {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String outcome;
    private String description;
    private String comment;
    @ManyToOne
    @JoinColumn(name="processId", referencedColumnName="id")
    private Process process;

    public ProcessOutcome() {
    }

    public ProcessOutcome(String outcome, String description, String comment) {
        this.outcome = outcome;
        this.description = description;
        this.comment = comment;
    }

    @JsonIgnore
    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

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

    public void setOutcomeType(String outcomeType) {
        this.outcomeType = outcomeType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
