package com.worth.ifs.domain;

import javax.persistence.*;
import java.util.List;

/**
 * Question defines database relations and a model to use client side and server side.
 */

@Entity
public class Question {
    public Question(String optionValues, long id, Competition competition, Section section, QuestionType questionType, List<Response> responses, String name, String description, String helpTitle, String helpText, String questionGuidanceText, String answerGuidanceText, long characterCount) {
        this.optionValues = optionValues;
        this.id = id;
        this.competition = competition;
        this.section = section;
        this.questionType = questionType;
        this.responses = responses;
        this.name = name;
        this.description = description;
        this.helpTitle = helpTitle;
        this.helpText = helpText;
        this.questionGuidanceText = questionGuidanceText;
        this.answerGuidanceText = answerGuidanceText;
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

    @OneToMany(mappedBy="question")
    private List<Response> responses;

    private String name;
    private String description;
    private String helpTitle;
    private String helpText;
    private String questionGuidanceText;
    private String answerGuidanceText;
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
}
