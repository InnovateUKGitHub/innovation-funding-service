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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="processId", referencedColumnName="id")
    private Process process;

    public ProcessOutcome() {
    	// no-arg constructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcessOutcome that = (ProcessOutcome) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (outcome != null ? !outcome.equals(that.outcome) : that.outcome != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        if (process != null ? !process.equals(that.process) : that.process != null) return false;
        return outcomeType != null ? outcomeType.equals(that.outcomeType) : that.outcomeType == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (outcome != null ? outcome.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (process != null ? process.hashCode() : 0);
        result = 31 * result + (outcomeType != null ? outcomeType.hashCode() : 0);
        return result;
    }
}
