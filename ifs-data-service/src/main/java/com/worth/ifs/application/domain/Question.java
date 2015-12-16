package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.form.domain.FormInput;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Question defines database relations and a model to use client side and server side.
 */
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String shortName;

    @Column(length=5000)
    private String description;

    @Column(length=5000)
    private String guidanceQuestion;

    @Column(length=5000)
    private String guidanceAnswer;

    private Boolean markAsCompletedEnabled = false;

    private Boolean assignEnabled = true;

    private Boolean multipleStatuses = false;

    private Integer priority;

    @Column(nullable = false)
    private boolean needingAssessorScore = false;

    @Column(nullable = false)
    private boolean needingAssessorFeedback = false;

    @OneToMany
    @JoinTable(name="question_form_input",
            joinColumns={@JoinColumn(name="question_id", referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="form_input_id", referencedColumnName="id")})
    @OrderColumn(name = "priority", nullable = false)
    private List<FormInput> formInputs = new ArrayList<>();

    private String assessorConfirmationQuestion;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sectionId", referencedColumnName="id")
    private Section section;

    @OneToMany(mappedBy="question")
    private List<Response> responses;

    @OneToMany(mappedBy="question")
    private List<QuestionStatus> questionStatuses;

    @OneToMany(mappedBy="question")
    private List<Cost> costs;

    private String questionNumber;

    public Question() {
        //default constructor
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Long getId() {
        return id;
    }

    public String getGuidanceAnswer() {
        return guidanceAnswer;
    }

    public String getGuidanceQuestion() {
        return guidanceQuestion;
    }

    public String getDescription() {
        return description;
    }

    public List<QuestionStatus> getQuestionStatuses() {
        return questionStatuses;
    }

    @JsonIgnore
    public List<Response> getResponses() {
        return responses;
    }

    @JsonIgnore
    public Competition getCompetition() {
        return competition;
    }

    @JsonIgnore
    public Section getSection() {
        return section;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public void setQuestionStatuses(List<QuestionStatus> questionStatuses) {
        this.questionStatuses = questionStatuses;
    }

    public Boolean isMarkAsCompletedEnabled() {
        return markAsCompletedEnabled == null ? false : markAsCompletedEnabled;
    }

    public Boolean hasMultipleStatuses() {
        return multipleStatuses;
    }

    public Boolean getMultipleStatuses() {
        return multipleStatuses;
    }

    public Boolean getMarkAsCompletedEnabled() {
        return markAsCompletedEnabled;
    }

    public Boolean isAssignEnabled() {
        // never return a null value.. it is enabled or disabled.
        return assignEnabled == null ? true : assignEnabled;
    }

    public void setAssignEnabled(Boolean assignEnabled) {
        this.assignEnabled = assignEnabled;
    }

    public Integer getPriority() {
        return priority;
    }

    public boolean getNeedingAssessorScore() {
        return needingAssessorScore;
    }

    public boolean getNeedingAssessorFeedback() {
        return needingAssessorFeedback;
    }

    public String getAssessorConfirmationQuestion() {
        return assessorConfirmationQuestion;
    }

    public String getQuestionNumber() {
        return questionNumber;
    }

    public List<FormInput> getFormInputs() {
        return formInputs;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != getClass()) {
            return false;
        }
        Question question = (Question) o;
        return new EqualsBuilder()
            .append(id, question.id)
            .append(name, question.name)
            .append(description, question.description)
            .append(guidanceQuestion, question.guidanceQuestion)
            .append(guidanceAnswer, question.guidanceAnswer)
            .append(markAsCompletedEnabled, question.markAsCompletedEnabled)
            .append(assignEnabled, question.assignEnabled)
            .append(multipleStatuses, question.multipleStatuses)
            .append(priority, question.priority)
            .append(needingAssessorScore, question.needingAssessorScore)
            .append(needingAssessorFeedback, question.needingAssessorFeedback)
            .append(assessorConfirmationQuestion, question.assessorConfirmationQuestion)
            .append(competition, question.competition)
            .append(responses, question.responses)
            .append(questionStatuses, question.questionStatuses)
            .append(costs, question.costs)
            .append(questionNumber, question.questionNumber)
            .isEquals();

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(id)
            .append(name)
            .append(description)
            .append(guidanceQuestion)
            .append(guidanceAnswer)
            .append(markAsCompletedEnabled)
            .append(assignEnabled)
            .append(multipleStatuses)
            .append(priority)
            .append(needingAssessorScore)
            .append(needingAssessorFeedback)
            .append(assessorConfirmationQuestion)
            .append(competition)
            .append(responses)
            .append(questionStatuses)
            .append(costs)
            .append(questionNumber)
            .toHashCode();
    }
}
