package com.worth.ifs.competitionsetup.viewmodel;

import com.worth.ifs.competition.resource.CompetitionSetupQuestionType;
import com.worth.ifs.competition.resource.GuidanceRowResource;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * View model for guidance rows in order that the subject field can be split into score from and score to elements
 * and validated accordingly.
 */
public class GuidanceRowViewModel {

    @NotEmpty(message = "{validation.applicationquestionform.justification.required}")
    @Size(max=255, message = "{validation.applicationquestionform.justification.max}")
    private String justification;

    @Min(value=0, message = "{validation.applicationquestionform.scorefrom.min}")
    @NotNull(message = "{validation.applicationquestionform.scorefrom.required}")
    private Integer scoreFrom;

    @Min(value=0, message = "{validation.applicationquestionform.scoreto.min}")
    @NotNull(message = "{validation.applicationquestionform.scoreto.required}")
    private Integer scoreTo;

    public GuidanceRowViewModel() {
    }

    public GuidanceRowViewModel(GuidanceRowResource guidanceRowResource) {

        this.setJustification(guidanceRowResource.getJustification());
        String[] score = guidanceRowResource.getSubject().split(",");
        this.setScoreFrom(Integer.parseInt(score[0]));
        this.setScoreTo(Integer.parseInt(score[1]));
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Integer getScoreFrom() {
        return scoreFrom;
    }

    public void setScoreFrom(Integer scoreFrom) {
        this.scoreFrom = scoreFrom;
    }

    public Integer getScoreTo() {
        return scoreTo;
    }

    public void setScoreTo(Integer scoreTo) {
        this.scoreTo = scoreTo;
    }
}
