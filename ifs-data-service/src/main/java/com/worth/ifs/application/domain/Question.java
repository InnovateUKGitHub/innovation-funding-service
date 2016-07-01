package com.worth.ifs.application.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.form.domain.FormInput;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static java.util.stream.Collectors.toList;

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

    private String assessorGuidanceQuestion;

    @Lob
    private String assessorGuidanceAnswer;

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

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="sectionId", referencedColumnName="id")
    private Section section;

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

    public String getDescription() {
        return description;
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

    public List<QuestionStatus> getQuestionStatuses() {
        return questionStatuses;
    }

    @JsonIgnore
    public Competition getCompetition() {
        return competition;
    }

    @JsonIgnore
    public Section getSection() {
        return section;
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

    public Boolean isMarkedAsCompleteForApplication(Application application) {
        List<QuestionStatus> questionStatuses = this.getQuestionStatuses().stream()
            .filter(qs -> qs.getApplication().getId().equals(application.getId()))
            .collect(toList());
        if (questionStatuses != null && !questionStatuses.isEmpty()) {
            return questionStatuses.get(0).getMarkedAsComplete();
        }
        return Boolean.TRUE;
    }

    public Boolean isMarkedAsCompleteForApplicationAndOrganisation(Application application, Long organisationId){
        return this.getQuestionStatuses().stream()
            .filter(qs -> qs.getApplication().getId().equals(application.getId()))
            .filter(qs -> qs.getMarkedAsCompleteBy() != null)
            .filter(qs -> organisationId.equals(qs.getMarkedAsCompleteBy().getOrganisation().getId()))
            .map(QuestionStatus::getMarkedAsComplete)
            .map(Boolean.TRUE::equals)
            .reduce(Boolean.FALSE, (a , b) -> a || b);
    }

    public void setAssignEnabled(Boolean assignEnabled) {
        this.assignEnabled = assignEnabled;
    }

    public Integer getPriority() {
        return priority;
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

    @JsonIgnore
    public List<Cost> getCosts() {
        return costs;
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

    public void setMarkAsCompletedEnabled(Boolean markAsCompletedEnabled) {
        this.markAsCompletedEnabled = markAsCompletedEnabled;
    }

    public void setMultipleStatuses(Boolean multipleStatuses) {
        this.multipleStatuses = multipleStatuses;
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

    public void setFormInputs(List<FormInput> formInputs) {
        this.formInputs = formInputs;
    }

    public void setAssessorConfirmationQuestion(String assessorConfirmationQuestion) {
        this.assessorConfirmationQuestion = assessorConfirmationQuestion;
    }

    public void setCosts(List<Cost> costs) {
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

        Question question = (Question) o;

        return new EqualsBuilder()
                .append(needingAssessorScore, question.needingAssessorScore)
                .append(needingAssessorFeedback, question.needingAssessorFeedback)
                .append(id, question.id)
                .append(name, question.name)
                .append(shortName, question.shortName)
                .append(description, question.description)
                .append(assessorGuidanceQuestion, question.assessorGuidanceQuestion)
                .append(assessorGuidanceAnswer, question.assessorGuidanceAnswer)
                .append(markAsCompletedEnabled, question.markAsCompletedEnabled)
                .append(assignEnabled, question.assignEnabled)
                .append(multipleStatuses, question.multipleStatuses)
                .append(priority, question.priority)
                .append(formInputs, question.formInputs)
                .append(assessorConfirmationQuestion, question.assessorConfirmationQuestion)
                .append(competition, question.competition)
                .append(section, question.section)
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
                .append(shortName)
                .append(description)
                .append(assessorGuidanceQuestion)
                .append(assessorGuidanceAnswer)
                .append(markAsCompletedEnabled)
                .append(assignEnabled)
                .append(multipleStatuses)
                .append(priority)
                .append(needingAssessorScore)
                .append(needingAssessorFeedback)
                .append(formInputs)
                .append(assessorConfirmationQuestion)
                .append(competition)
                .append(section)
                .append(questionStatuses)
                .append(costs)
                .append(questionNumber)
                .toHashCode();
    }
}
