package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.form.domain.FormInput;

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

    @Column(length=5000)
    private String description;

    @Column(length=5000)
    private String guidanceQuestion;

    @Column(length=5000)
    private String guidanceAnswer;

    @Column(length=5000)
    private Integer wordCount;

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
    }

    public String getName() {
        return name;
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

    public Integer getWordCount() {
        return wordCount != null ? wordCount : 0;
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
        return (markAsCompletedEnabled == null ? false : markAsCompletedEnabled);
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
        return (assignEnabled == null ? true : assignEnabled);
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        if (needingAssessorScore != question.needingAssessorScore) return false;
        if (needingAssessorFeedback != question.needingAssessorFeedback) return false;
        if (id != null ? !id.equals(question.id) : question.id != null) return false;
        if (name != null ? !name.equals(question.name) : question.name != null) return false;
        if (description != null ? !description.equals(question.description) : question.description != null)
            return false;
        if (guidanceQuestion != null ? !guidanceQuestion.equals(question.guidanceQuestion) : question.guidanceQuestion != null)
            return false;
        if (guidanceAnswer != null ? !guidanceAnswer.equals(question.guidanceAnswer) : question.guidanceAnswer != null)
            return false;
        if (wordCount != null ? !wordCount.equals(question.wordCount) : question.wordCount != null) return false;
        if (markAsCompletedEnabled != null ? !markAsCompletedEnabled.equals(question.markAsCompletedEnabled) : question.markAsCompletedEnabled != null)
            return false;
        if (assignEnabled != null ? !assignEnabled.equals(question.assignEnabled) : question.assignEnabled != null)
            return false;
        if (multipleStatuses != null ? !multipleStatuses.equals(question.multipleStatuses) : question.multipleStatuses != null)
            return false;
        if (priority != null ? !priority.equals(question.priority) : question.priority != null) return false;
        if (assessorConfirmationQuestion != null ? !assessorConfirmationQuestion.equals(question.assessorConfirmationQuestion) : question.assessorConfirmationQuestion != null)
            return false;
        if (competition != null ? !competition.equals(question.competition) : question.competition != null)
            return false;
        if (section != null ? !section.equals(question.section) : question.section != null) return false;
        if (responses != null ? !responses.equals(question.responses) : question.responses != null) return false;
        if (questionStatuses != null ? !questionStatuses.equals(question.questionStatuses) : question.questionStatuses != null)
            return false;
        if (costs != null ? !costs.equals(question.costs) : question.costs != null) return false;
        return !(questionNumber != null ? !questionNumber.equals(question.questionNumber) : question.questionNumber != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (guidanceQuestion != null ? guidanceQuestion.hashCode() : 0);
        result = 31 * result + (guidanceAnswer != null ? guidanceAnswer.hashCode() : 0);
        result = 31 * result + (wordCount != null ? wordCount.hashCode() : 0);
        result = 31 * result + (markAsCompletedEnabled != null ? markAsCompletedEnabled.hashCode() : 0);
        result = 31 * result + (assignEnabled != null ? assignEnabled.hashCode() : 0);
        result = 31 * result + (multipleStatuses != null ? multipleStatuses.hashCode() : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        result = 31 * result + (needingAssessorScore ? 1 : 0);
        result = 31 * result + (needingAssessorFeedback ? 1 : 0);
        result = 31 * result + (assessorConfirmationQuestion != null ? assessorConfirmationQuestion.hashCode() : 0);
        result = 31 * result + (competition != null ? competition.hashCode() : 0);
        result = 31 * result + (section != null ? section.hashCode() : 0);
        result = 31 * result + (responses != null ? responses.hashCode() : 0);
        result = 31 * result + (questionStatuses != null ? questionStatuses.hashCode() : 0);
        result = 31 * result + (costs != null ? costs.hashCode() : 0);
        result = 31 * result + (questionNumber != null ? questionNumber.hashCode() : 0);
        return result;
    }
}
