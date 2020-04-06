package org.innovateuk.ifs.form.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.cache.CacheableWhenCompetitionOpen;
import org.innovateuk.ifs.question.resource.QuestionSetupType;

/**
 * Question defines database relations and a model to use client side and server side.
 */
public class QuestionResource implements Comparable<QuestionResource>, CacheableWhenCompetitionOpen {
    private Long id;
    private String name;
    private String shortName;
    private String description;
    private Boolean markAsCompletedEnabled = false;
    private Boolean assignEnabled = true;
    private Boolean multipleStatuses = false;
    private Integer priority;
    private Long competition;
    private Long section;
    private String questionNumber;
    private QuestionType type;
    private QuestionSetupType questionSetupType;
    private Integer assessorMaximumScore;
    //Used by @Cacheable
    @JsonIgnore

    private boolean competitionOpen;

    public QuestionResource() {
        //default constructor
    }

    public Integer getAssessorMaximumScore() {
        return assessorMaximumScore;
    }

    public void setAssessorMaximumScore(Integer assessorMaximumScore) {
        this.assessorMaximumScore = assessorMaximumScore;
    }

    public String getName() {
        return this.name;
    }

    public String getShortName() {
        return this.shortName;
    }

    public Long getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public Long getSection() {
        return this.section;
    }

    public Boolean isMarkAsCompletedEnabled() {
        return this.markAsCompletedEnabled == null ? false : this.markAsCompletedEnabled;
    }

    public Boolean hasMultipleStatuses() {
        return this.multipleStatuses;
    }

    public Boolean getMultipleStatuses() {
        return this.multipleStatuses;
    }

    public Boolean getMarkAsCompletedEnabled() {
        return this.markAsCompletedEnabled;
    }

    public void setMarkAsCompletedEnabled(Boolean markAsCompletedEnabled) {
        this.markAsCompletedEnabled = markAsCompletedEnabled;
    }

    public void setMultipleStatuses(Boolean multipleStatuses) {
        this.multipleStatuses = multipleStatuses;
    }

    public Boolean isAssignEnabled() {
        // never return a null value.. it is enabled or disabled.
        return this.assignEnabled == null ? true : this.assignEnabled;
    }

    public void setAssignEnabled(Boolean assignEnabled) {
        this.assignEnabled = assignEnabled;
    }

    public Integer getPriority() {
        return this.priority;
    }

    public String getQuestionNumber() {
        return this.questionNumber;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Boolean getAssignEnabled() {
        return this.assignEnabled;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setSection(Long section) {
        this.section = section;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
    }
    
    public QuestionType getType() {
		return type;
	}
    
    public void setType(QuestionType type) {
		this.type = type;
	}

    public QuestionSetupType getQuestionSetupType() {
        return questionSetupType;
    }

    public void setQuestionSetupType(QuestionSetupType questionSetupType) {
        this.questionSetupType = questionSetupType;
    }

    public Long getCompetition() {
        return competition;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }

    @Override
    public boolean isCompetitionOpen() {
        return competitionOpen;
    }

    public void setCompetitionOpen(boolean competitionOpen) {
        this.competitionOpen = competitionOpen;
    }

    @Override
    public int compareTo(QuestionResource o) {
        return Integer.compare(this.priority, o.priority);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        QuestionResource that = (QuestionResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(shortName, that.shortName)
                .append(description, that.description)
                .append(markAsCompletedEnabled, that.markAsCompletedEnabled)
                .append(assignEnabled, that.assignEnabled)
                .append(multipleStatuses, that.multipleStatuses)
                .append(priority, that.priority)
                .append(section, that.section)
                .append(questionNumber, that.questionNumber)
                .append(type, that.type)
                .append(questionSetupType, that.questionSetupType)
                .append(assessorMaximumScore, that.assessorMaximumScore)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(shortName)
                .append(description)
                .append(markAsCompletedEnabled)
                .append(assignEnabled)
                .append(multipleStatuses)
                .append(priority)
                .append(section)
                .append(questionNumber)
                .append(type)
                .append(questionSetupType)
                .append(assessorMaximumScore)
                .toHashCode();
    }
}
