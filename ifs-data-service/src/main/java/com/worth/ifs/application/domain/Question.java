package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.Cost;

import javax.persistence.*;
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

    @Column(length=5000)
    private String optionValues;

    private Boolean markAsCompletedEnabled = false;

    private Boolean assignEnabled = true;

    private Boolean multipleStatuses = false;

    private Integer priority;

    // TODO DW - added for Alpha phase to determine which questions are scorable by the Assessor - in Beta probably need an
    // "assessment_type" table like the question_type table that allows better configuration of the types of responses the
    // assessor can provide, thereby removing the need for these various "needingAssessor..." columns
    @Column(nullable = false)
    private boolean needingAssessorScore = false;

    // TODO DW - added for Alpha phase to determine which questions need feedback from the Assessor - in Beta probably need an
    // "assessment_type" table like the question_type table that allows better configuration of the types of responses the
    // assessor can provide, thereby removing the need for these various "needingAssessor..." columns
    @Column(nullable = false)
    private boolean needingAssessorFeedback = false;

    // TODO DW - added for Alpha phase to determine which questions need Yes / No responses from assessor and the text - in Beta probably need an
    // "assessment_type" table like the question_type table that allows better configuration of the types of responses the
    // assessor can provide, thereby removing the need for these various "needingAssessor..." columns
    // that, if present, will be asked of them with a Yes / No answer
    private String assessorConfirmationQuestion;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sectionId", referencedColumnName="id")
    private Section section;

    @ManyToOne
    @JoinColumn(name="questionTypeId", referencedColumnName="id")
    private QuestionType questionType;

    @OneToMany(mappedBy="question")
    private List<Response> responses;

    @OneToMany(mappedBy="question")
    private List<QuestionStatus> questionStatuses;

    @OneToMany(mappedBy="question")
    private List<Cost> costs;

    @ManyToOne
    @JoinColumn(name="childQuestionId", referencedColumnName="id")
    @JsonBackReference
    private Question childQuestion;

    @OneToOne(mappedBy="childQuestion")
    @JsonManagedReference
    @OrderBy("priority ASC")
    private Question parentQuestion;

    private String questionNumber;

    public Question(String optionValues, Long id, Competition competition, Section section, QuestionType questionType, List<Response> responses, String name, String questionNumber, String description, String guidanceQuestion, String guidanceAnswer, Integer wordCount, Integer priority) {
        this.optionValues = optionValues;
        this.id = id;
        this.competition = competition;
        this.section = section;
        this.questionType = questionType;
        this.responses = responses;
        this.name = name;
        this.questionNumber = questionNumber;
        this.description = description;
        this.guidanceQuestion = guidanceQuestion;
        this.guidanceAnswer = guidanceAnswer;
        this.wordCount = wordCount;
        this.priority = priority;
    }

    public Question(Long id, Competition competition, Section section, String name) {
        this.id = id;
        this.competition = competition;
        this.section = section;
        this.name = name;
    }

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

    public String getOptionValues() {
        return optionValues;
    }

    public Integer getWordCount() {
        return (wordCount != null ? wordCount : Integer.valueOf(0)) ;
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

    public QuestionType getQuestionType() {
        return questionType;
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

    public Question getChildQuestion() {
        return childQuestion;
    }

    public Question getParentQuestion() {
        return parentQuestion;
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
        if (optionValues != null ? !optionValues.equals(question.optionValues) : question.optionValues != null)
            return false;
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
        if (questionType != null ? !questionType.equals(question.questionType) : question.questionType != null)
            return false;
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
        result = 31 * result + (optionValues != null ? optionValues.hashCode() : 0);
        result = 31 * result + (markAsCompletedEnabled != null ? markAsCompletedEnabled.hashCode() : 0);
        result = 31 * result + (assignEnabled != null ? assignEnabled.hashCode() : 0);
        result = 31 * result + (multipleStatuses != null ? multipleStatuses.hashCode() : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        result = 31 * result + (needingAssessorScore ? 1 : 0);
        result = 31 * result + (needingAssessorFeedback ? 1 : 0);
        result = 31 * result + (assessorConfirmationQuestion != null ? assessorConfirmationQuestion.hashCode() : 0);
        result = 31 * result + (competition != null ? competition.hashCode() : 0);
        result = 31 * result + (section != null ? section.hashCode() : 0);
        result = 31 * result + (questionType != null ? questionType.hashCode() : 0);
        result = 31 * result + (responses != null ? responses.hashCode() : 0);
        result = 31 * result + (questionStatuses != null ? questionStatuses.hashCode() : 0);
        result = 31 * result + (costs != null ? costs.hashCode() : 0);
        result = 31 * result + (questionNumber != null ? questionNumber.hashCode() : 0);
        return result;
    }
}
