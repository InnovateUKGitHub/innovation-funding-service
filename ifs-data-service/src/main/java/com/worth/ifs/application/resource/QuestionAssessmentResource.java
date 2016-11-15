package com.worth.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Question defines database relations and a model to use client side and server side.
 */
public class QuestionAssessmentResource {
    private Long id;
    private Boolean scored;
    private Integer scoreTotal;
    private Boolean writtenFeedback;
    private String guidance;
    private Integer wordCount;
    private List<AssessmentScoreRowResource> scoreRows;

    public QuestionAssessmentResource() {
        //default constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getScored() {
        return scored;
    }

    public void setScored(Boolean scored) {
        this.scored = scored;
    }

    public Integer getScoreTotal() {
        return scoreTotal;
    }

    public void setScoreTotal(Integer scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public Boolean getWrittenFeedback() {
        return writtenFeedback;
    }

    public void setWrittenFeedback(Boolean writtenFeedback) {
        this.writtenFeedback = writtenFeedback;
    }

    public String getGuidance() {
        return guidance;
    }

    public void setGuidance(String guidance) {
        this.guidance = guidance;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public List<AssessmentScoreRowResource> getScoreRows() {
        return scoreRows;
    }

    public void setScoreRows(List<AssessmentScoreRowResource> scoreRows) {
        this.scoreRows = scoreRows;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        QuestionAssessmentResource that = (QuestionAssessmentResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(scored, that.scored)
                .append(scoreTotal, that.scoreTotal)
                .append(writtenFeedback, that.writtenFeedback)
                .append(guidance, that.guidance)
                .append(wordCount, that.wordCount)
                .append(scoreRows, that.scoreRows)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(scored)
                .append(scoreTotal)
                .append(writtenFeedback)
                .append(guidance)
                .append(wordCount)
                .append(scoreRows)
                .toHashCode();
    }
}
