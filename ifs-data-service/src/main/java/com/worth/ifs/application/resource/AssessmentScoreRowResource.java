package com.worth.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AssessmentScoreRowResource {
    private Long id;
    private Integer start;
    private Integer end;
    private String justification;

    public AssessmentScoreRowResource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentScoreRowResource that = (AssessmentScoreRowResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(start, that.start)
                .append(end, that.end)
                .append(justification, that.justification)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(start)
                .append(end)
                .append(justification)
                .toHashCode();
    }
}
