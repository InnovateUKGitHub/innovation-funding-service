package com.worth.ifs.application.resource;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import java.util.List;

public class CompetitionSetupQuestionResource {
    private Long questionId;

    private String number;
    private String shortTitle;
    @NotBlank
    private String title;
    private String subTitle;

    @NotBlank
    private String guidanceTitle;

    @NotBlank
    private String guidance;

    @Min(1)
    private Integer maxWords;
    private Boolean appendix;

    private String assessmentGuidance;
    private Integer assessmentMaxWords;

    private Boolean scored;
    private Integer scoreTotal;
    private Boolean writtenFeedback;
    private Integer wordCount;
    private List<FormInputGuidanceRowResource> guidanceRows;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getGuidanceTitle() {
        return guidanceTitle;
    }

    public void setGuidanceTitle(String guidanceTitle) {
        this.guidanceTitle = guidanceTitle;
    }

    public String getGuidance() {
        return guidance;
    }

    public void setGuidance(String guidance) {
        this.guidance = guidance;
    }

    public Integer getMaxWords() {
        return maxWords;
    }

    public void setMaxWords(Integer maxWords) {
        this.maxWords = maxWords;
    }

    public Boolean getAppendix() {
        return appendix;
    }

    public void setAppendix(Boolean appendix) {
        this.appendix = appendix;
    }

    public String getAssessmentGuidance() {
        return assessmentGuidance;
    }

    public void setAssessmentGuidance(String assessmentGuidance) {
        this.assessmentGuidance = assessmentGuidance;
    }

    public Integer getAssessmentMaxWords() {
        return assessmentMaxWords;
    }

    public void setAssessmentMaxWords(Integer assessmentMaxWords) {
        this.assessmentMaxWords = assessmentMaxWords;
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

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public List<FormInputGuidanceRowResource> getGuidanceRows() {
        return guidanceRows;
    }

    public void setGuidanceRows(List<FormInputGuidanceRowResource> guidanceRows) {
        this.guidanceRows = guidanceRows;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }
}
