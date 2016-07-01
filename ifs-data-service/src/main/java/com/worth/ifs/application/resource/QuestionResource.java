package com.worth.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Question defines database relations and a model to use client side and server side.
 */
public class QuestionResource {
    private Long id;
    private String name;
    private String shortName;
    private String description;
    private String assessorGuidanceQuestion;
    private String assessorGuidanceAnswer;
    private final List<Long> formInputs = new ArrayList<>();
    private Boolean markAsCompletedEnabled = false;
    private Boolean assignEnabled = true;
    private Boolean multipleStatuses = false;
    private Integer priority;
    private boolean needingAssessorScore;
    private boolean needingAssessorFeedback;
    private String assessorConfirmationQuestion;
    private Long competition;
    private Long section;
    private List<Long> questionStatuses;
    private List<Long> costs;
    private String questionNumber;

    public QuestionResource() {
        //default constructor
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

    public String getAssessorGuidanceQuestion() {
        return assessorGuidanceQuestion;
    }

    public void setAssessorGuidanceQuestion(String assessorGuidanceQuestion) {
        this.assessorGuidanceQuestion = assessorGuidanceQuestion;
    }

    public String getAssessorGuidanceAnswer() {
        return assessorGuidanceAnswer;
    }

    public void setAssessorGuidanceAnswer(String assessorGuidanceAnswer) {
        this.assessorGuidanceAnswer = assessorGuidanceAnswer;
    }

    public List<Long> getQuestionStatuses() {
        return this.questionStatuses;
    }

    public Long getCompetition() {
        return this.competition;
    }

    public Long getSection() {
        return this.section;
    }

    public void setQuestionStatuses(List<Long> questionStatusIds) {
        this.questionStatuses = questionStatusIds;
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

    public String getAssessorConfirmationQuestion() {
        return this.assessorConfirmationQuestion;
    }

    public String getQuestionNumber() {
        return this.questionNumber;
    }


    public List<Long> getFormInputs() {
        return this.formInputs;
    }

    public List<Long> getCosts() {
        return this.costs;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Boolean getAssignEnabled() {
        return this.assignEnabled;
    }

    public boolean isNeedingAssessorFeedback() {
        return needingAssessorFeedback;
    }

    public void setNeedingAssessorFeedback(boolean needingAssessorFeedback) {
        this.needingAssessorFeedback = needingAssessorFeedback;
    }

    public boolean isNeedingAssessorScore() {
        return needingAssessorScore;
    }

    public void setNeedingAssessorScore(boolean needingAssessorScore) {
        this.needingAssessorScore = needingAssessorScore;
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

    public void setAssessorConfirmationQuestion(String assessorConfirmationQuestion) {
        this.assessorConfirmationQuestion = assessorConfirmationQuestion;
    }

    public void setCompetition(Long competition) {
        this.competition = competition;
    }

    public void setSection(Long section) {
        this.section = section;
    }

    public void setCosts(List<Long> costs) {
        this.costs = costs;
    }

    public void setQuestionNumber(String questionNumber) {
        this.questionNumber = questionNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QuestionResource that = (QuestionResource) o;

        return new EqualsBuilder()
                .append(needingAssessorScore, that.needingAssessorScore)
                .append(needingAssessorFeedback, that.needingAssessorFeedback)
                .append(id, that.id)
                .append(name, that.name)
                .append(shortName, that.shortName)
                .append(description, that.description)
                .append(assessorGuidanceQuestion, that.assessorGuidanceQuestion)
                .append(assessorGuidanceAnswer, that.assessorGuidanceAnswer)
                .append(formInputs, that.formInputs)
                .append(markAsCompletedEnabled, that.markAsCompletedEnabled)
                .append(assignEnabled, that.assignEnabled)
                .append(multipleStatuses, that.multipleStatuses)
                .append(priority, that.priority)
                .append(assessorConfirmationQuestion, that.assessorConfirmationQuestion)
                .append(competition, that.competition)
                .append(section, that.section)
                .append(questionStatuses, that.questionStatuses)
                .append(costs, that.costs)
                .append(questionNumber, that.questionNumber)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(shortName)
                .append(description)
                .append(assessorGuidanceQuestion)
                .append(assessorGuidanceAnswer)
                .append(formInputs)
                .append(markAsCompletedEnabled)
                .append(assignEnabled)
                .append(multipleStatuses)
                .append(priority)
                .append(needingAssessorScore)
                .append(needingAssessorFeedback)
                .append(assessorConfirmationQuestion)
                .append(competition)
                .append(section)
                .append(questionStatuses)
                .append(costs)
                .append(questionNumber)
                .toHashCode();
    }
}
