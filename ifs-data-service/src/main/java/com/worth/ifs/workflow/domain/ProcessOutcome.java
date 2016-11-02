package com.worth.ifs.workflow.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    private String outcomeType;
    @Column(name = "process_index", nullable = false)
    private Integer index;

    @JsonIgnore
    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOutcomeType() {
        return outcomeType;
    }

    public void setOutcomeType(String outcomeType) {
        this.outcomeType = outcomeType;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProcessOutcome that = (ProcessOutcome) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(outcome, that.outcome)
                .append(description, that.description)
                .append(comment, that.comment)
                .append(process, that.process)
                .append(outcomeType, that.outcomeType)
                .append(index, that.index)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(outcome)
                .append(description)
                .append(comment)
                .append(process)
                .append(outcomeType)
                .append(index)
                .toHashCode();
    }
}
