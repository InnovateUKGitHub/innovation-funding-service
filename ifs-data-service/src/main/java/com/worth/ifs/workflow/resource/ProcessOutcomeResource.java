package com.worth.ifs.workflow.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ProcessOutcomeResource {
    private Long id;
    private String outcome;
    private String description;
    private String comment;
    private String outcomeType;
    private Integer index;

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

        ProcessOutcomeResource that = (ProcessOutcomeResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(outcome, that.outcome)
                .append(description, that.description)
                .append(comment, that.comment)
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
                .append(outcomeType)
                .append(index)
                .toHashCode();
    }
}
