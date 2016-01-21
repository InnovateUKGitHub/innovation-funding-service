package com.worth.ifs.application.resource;

import com.worth.ifs.application.domain.Section;
import com.worth.ifs.competition.domain.Competition;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.hateoas.core.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * Question defines database relations and a model to use client side and server side.
 */
@Relation(value="question", collectionRelation="questions")
public class QuestionResource {
    private Long id;
    private String name;
    private String shortName;
    private String description;
    private String guidanceQuestion;
    private String guidanceAnswer;
    private final List<Long> formInputs = new ArrayList<>();
    private final Boolean markAsCompletedEnabled = false;
    private Boolean assignEnabled = true;
    private final Boolean multipleStatuses = false;
    private Integer priority;
    private boolean needingAssessorScore;
    private boolean needingAssessorFeedback;
    private String assessorConfirmationQuestion;
    private Long competition;
    private Long section;
    private List<Long> responses;
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

    public void setSection(Section section) {
        this.section = section.getId();
    }

    public void setCompetition(Competition competition) {
        this.competition = competition.getId();
    }

    public Long getId() {
        return this.id;
    }

    public String getGuidanceAnswer() {
        return this.guidanceAnswer;
    }

    public String getGuidanceQuestion() {
        return this.guidanceQuestion;
    }

    public String getDescription() {
        return this.description;
    }

    public List<Long> getQuestionStatuses() {
        return this.questionStatuses;
    }

    public List<Long> getResponses() {
        return this.responses;
    }

    public Long getCompetition() {
        return this.competition;
    }

    public Long getSection() {
        return this.section;
    }

    public void setResponses(List<Long> responses) {
        this.responses = responses;
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

    public boolean getNeedingAssessorScore() {
        return this.needingAssessorScore;
    }

    public boolean getNeedingAssessorFeedback() {
        return this.needingAssessorFeedback;
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
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        QuestionResource question = (QuestionResource) o;
        return new EqualsBuilder()
            .append(this.id, question.id)
            .append(this.name, question.name)
            .append(this.description, question.description)
            .append(this.guidanceQuestion, question.guidanceQuestion)
            .append(this.guidanceAnswer, question.guidanceAnswer)
            .append(this.formInputs, question.formInputs)
            .append(this.markAsCompletedEnabled, question.markAsCompletedEnabled)
            .append(this.assignEnabled, question.assignEnabled)
            .append(this.multipleStatuses, question.multipleStatuses)
            .append(this.priority, question.priority)
            .append(this.needingAssessorScore, question.needingAssessorScore)
            .append(this.needingAssessorFeedback, question.needingAssessorFeedback)
            .append(this.assessorConfirmationQuestion, question.assessorConfirmationQuestion)
            .append(this.competition, question.competition)
            .append(this.responses, question.responses)
            .append(this.questionStatuses, question.questionStatuses)
            .append(this.costs, question.costs)
            .append(this.questionNumber, question.questionNumber)
            .isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(this.id)
            .append(this.name)
            .append(this.description)
            .append(this.guidanceQuestion)
            .append(this.guidanceAnswer)
            .append(this.formInputs)
            .append(this.markAsCompletedEnabled)
            .append(this.assignEnabled)
            .append(this.multipleStatuses)
            .append(this.priority)
            .append(this.needingAssessorScore)
            .append(this.needingAssessorFeedback)
            .append(this.assessorConfirmationQuestion)
            .append(this.competition)
            .append(this.responses)
            .append(this.questionStatuses)
            .append(this.costs)
            .append(this.questionNumber)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", this.id)
            .append("name", this.name)
            .append("shortName", this.shortName)
            .append("description", this.description)
            .append("guidanceQuestion", this.guidanceQuestion)
            .append("guidanceAnswer", this.guidanceAnswer)
            .append("formInputIds", this.formInputs)
            .append("markAsCompletedEnabled", this.markAsCompletedEnabled)
            .append("assignEnabled", this.assignEnabled)
            .append("multipleStatuses", this.multipleStatuses)
            .append("priority", this.priority)
            .append("needingAssessorScore", this.needingAssessorScore)
            .append("needingAssessorFeedback", this.needingAssessorFeedback)
            .append("assessorConfirmationQuestion", this.assessorConfirmationQuestion)
            .append("competitionId", this.competition)
            .append("sectionId", this.section)
            .append("responseIds", this.responses)
            .append("questionStatusIds", this.questionStatuses)
            .append("costIds", this.costs)
            .append("questionNumber", this.questionNumber)
            .toString();
    }

    public Boolean getAssignEnabled() {
        return this.assignEnabled;
    }

    public boolean isNeedingAssessorScore() {
        return this.needingAssessorScore;
    }

    public boolean isNeedingAssessorFeedback() {
        return this.needingAssessorFeedback;
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

    public void setGuidanceQuestion(String guidanceQuestion) {
        this.guidanceQuestion = guidanceQuestion;
    }

    public void setGuidanceAnswer(String guidanceAnswer) {
        this.guidanceAnswer = guidanceAnswer;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setNeedingAssessorScore(boolean needingAssessorScore) {
        this.needingAssessorScore = needingAssessorScore;
    }

    public void setNeedingAssessorFeedback(boolean needingAssessorFeedback) {
        this.needingAssessorFeedback = needingAssessorFeedback;
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
}
