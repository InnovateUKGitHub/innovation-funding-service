package com.worth.ifs.application.domain;

import javax.persistence.*;
import java.util.List;

/**
 * QuestionAssessment defines database relations and a model to use client side and server side.
 */
@Entity
public class QuestionAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;

    private Boolean scored;

    private Integer scoreTotal;

    private Boolean writtenFeedback;

    private String guidance;

    private Integer wordCount;

    @OneToMany(mappedBy = "questionAssessment")
    private List<AssessmentScoreRow> scoreRows;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Boolean getScored() {
        return scored;
    }

    public void setScored(Boolean scored) {
        this.scored = scored;
    }

    public Integer getScoreTotal() {
        return scoreTotal;
    }

    public void setScoreTotal(Integer scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public Boolean getWrittenFeedback() {
        return writtenFeedback;
    }

    public void setWrittenFeedback(Boolean writtenFeedback) {
        this.writtenFeedback = writtenFeedback;
    }

    public String getGuidance() {
        return guidance;
    }

    public void setGuidance(String guidance) {
        this.guidance = guidance;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public List<AssessmentScoreRow> getScoreRows() {
        return scoreRows;
    }

    public void setScoreRows(List<AssessmentScoreRow> scoreRows) {
        this.scoreRows = scoreRows;
    }
}
