package com.worth.ifs.competitionsetup.viewmodel.application;

import com.worth.ifs.application.resource.QuestionAssessmentResource;
import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.form.resource.FormInputResource;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * View viewmodel where all information is for editing questions
 */
public class QuestionViewModel {
    private Long id;
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

    private Boolean scored;
    private Integer scoreTotal;
    private Boolean writtenFeedback;
    private String assessmentGuidance;
    private Integer assessmentMaxWords;

    @Valid
    private List<ScoreRowModel> scores;

    public QuestionViewModel() {

    }

    public QuestionViewModel(QuestionResource questionResource, FormInputResource formInputResource, Boolean appendix, QuestionAssessmentResource assessment) {
        this.setId(questionResource.getId());
        this.setNumber(questionResource.getQuestionNumber());
        this.setShortTitle(questionResource.getShortName());
        this.setTitle(questionResource.getName());
        this.setSubTitle(questionResource.getDescription());

        this.setGuidanceTitle(formInputResource.getGuidanceQuestion());
        this.setGuidance(formInputResource.getGuidanceAnswer());
        if (formInputResource.getWordCount() > 0) {
            this.setMaxWords(formInputResource.getWordCount());
        } else {
            this.setMaxWords(400);
        }

        this.setScored(scored);
        this.setAppendix(appendix);

        this.scored = assessment.getScored();
        this.scoreTotal = assessment.getScoreTotal();
        this.writtenFeedback = assessment.getWrittenFeedback();
        this.assessmentGuidance = assessment.getGuidance();
        this.assessmentMaxWords = assessment.getWordCount();

        this.scores = assessment.getScoreRows().stream()
                .map(row -> new ScoreRowModel(row.getId(), row.getStart(), row.getEnd(), row.getJustification()))
                .collect(Collectors.toList());

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<ScoreRowModel> getScores() {
        return scores;
    }

    public void setScores(List<ScoreRowModel> scores) {
        this.scores = scores;
    }
}
