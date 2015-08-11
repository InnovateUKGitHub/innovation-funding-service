package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Question defines database relations and a model to use client side and server side.
 */

@Entity
public class Question {
    public Question(String optionValues, long id, Competition competition, Section section, QuestionType questionType, List<Response> responses, String name, String description, String guidanceTitle, String guidanceQuestion, String guidanceQuestionText, String guidanceAnswerText, long characterCount) {
        this.optionValues = optionValues;
        this.id = id;
        this.competition = competition;
        this.section = section;
        this.questionType = questionType;
        this.responses = responses;
        this.name = name;
        this.description = description;
        this.guidanceTitle = guidanceTitle;
        this.guidanceQuestion = guidanceQuestion;
        this.guidanceQuestionText = guidanceQuestionText;
        this.guidanceAnswerText = guidanceAnswerText;
        this.characterCount = characterCount;
    }

    public Question(long id, Competition competition, Section section, String name) {
        this.id = id;
        this.competition = competition;
        this.section = section;
        this.name = name;
    }

    public Question() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @ManyToOne
    @JoinColumn(name="sectionId", referencedColumnName="id")
    private Section section;

    @ManyToOne
    @JoinColumn(name="questionTypeId", referencedColumnName="id")
    private QuestionType questionType;

    @OneToMany(mappedBy="question", fetch = FetchType.LAZY)
    private List<Response> responses;

    private String name;
    private String description;
    private String guidanceTitle;
    private String guidanceQuestion;
    private String guidanceQuestionText;
    private String guidanceAnswerText;
    private long characterCount;
    private String optionValues;

    public String getName() {
        return name;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public long getId() {
        return id;
    }


    public String getOptionValues() {
        return optionValues;
    }

    public long getCharacterCount() {
        return characterCount;
    }

    public String getGuidanceAnswerText() {
        return guidanceAnswerText;
    }

    public String getGuidanceQuestionText() {
        return guidanceQuestionText;
    }

    public String getGuidanceQuestion() {
        return guidanceQuestion;
    }

    public String getGuidanceTitle() {
        return guidanceTitle;
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
}
