package com.worth.ifs.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Boolean needingAssessorScore;

    // TODO DW - added for Alpha phase to determine which questions need feedback from the Assessor - in Beta probably need an
    // "assessment_type" table like the question_type table that allows better configuration of the types of responses the
    // assessor can provide, thereby removing the need for these various "needingAssessor..." columns
    private Boolean needingAssessorFeedback;

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

    public Question(String optionValues, Long id, Competition competition, Section section, QuestionType questionType, List<Response> responses, String name, String description, String guidanceQuestion, String guidanceAnswer, Integer wordCount, Integer priority) {
        this.optionValues = optionValues;
        this.id = id;
        this.competition = competition;
        this.section = section;
        this.questionType = questionType;
        this.responses = responses;
        this.name = name;
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

    public Boolean getNeedingAssessorScore() {
        return needingAssessorScore;
    }

    public Boolean getNeedingAssessorFeedback() {
        return needingAssessorFeedback;
    }

    public String getAssessorConfirmationQuestion() {
        return assessorConfirmationQuestion;
    }
}
