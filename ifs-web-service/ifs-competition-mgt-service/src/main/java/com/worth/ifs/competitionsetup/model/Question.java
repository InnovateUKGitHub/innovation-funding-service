package com.worth.ifs.competitionsetup.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;

/**
 * View model where all information is for editing questions
 */
public class Question {
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
}
