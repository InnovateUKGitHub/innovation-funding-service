package com.worth.ifs.workflow.resource;

public class ProcessOutcomeResource {
    private Long id;
    private String outcome;
    private String description;
    private String comment;
    private Long process;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOutcome() {
        return this.outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getProcess() {
        return this.process;
    }

    public void setProcess(Long process) {
        this.process = process;
    }
}
