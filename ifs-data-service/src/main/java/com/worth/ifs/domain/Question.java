package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private Long wordCount;

    @Column(length=5000)
    private String optionValues;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="sectionId", referencedColumnName="id")
    private Section section;

    @ManyToOne
    @JoinColumn(name="questionTypeId", referencedColumnName="id")
    private QuestionType questionType;

    @OneToMany(mappedBy="question", fetch = FetchType.LAZY)
    private List<Response> responses;

    @OneToOne(mappedBy="question", fetch = FetchType.LAZY)
    private CostCategory costCategory;

    public Question(String optionValues, Long id, Competition competition, Section section, QuestionType questionType, List<Response> responses, String name, String description, String guidanceQuestion, String guidanceAnswer, Long wordCount) {
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

    public Long getWordCount() {
        return (wordCount != null ? wordCount : Long.valueOf(0)) ;
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

    public CostCategory getCostCategory() {
        return costCategory;
    }
}
