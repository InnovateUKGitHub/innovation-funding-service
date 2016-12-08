package com.worth.ifs.competitionsetup.viewmodel;

import com.worth.ifs.competition.resource.GuidanceRowResource;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form for guidance rows in order that the subject field can be split into score from and score to elements
 * and validated accordingly.
 */
public class GuidanceRowForm {

    @NotEmpty(message = "{validation.applicationquestionform.justification.required}")
    @Size(max=255, message = "{validation.applicationquestionform.justification.max}")
    private String justification;

    @Min(value=0, message = "{validation.applicationquestionform.scorefrom.min}")
    @NotNull(message = "{validation.applicationquestionform.scorefrom.required}")
    private Integer scoreFrom;

    @Min(value=0, message = "{validation.applicationquestionform.scoreto.min}")
    @NotNull(message = "{validation.applicationquestionform.scoreto.required}")
    private Integer scoreTo;

    private Integer priority;

    public GuidanceRowForm() {
    }

    public GuidanceRowForm(GuidanceRowResource guidanceRowResource) {

        this.setJustification(guidanceRowResource.getJustification());
        if (guidanceRowResource.getSubject() != null) {
            String[] score = guidanceRowResource.getSubject().split(",");
            try {
                this.setScoreFrom(Integer.parseInt(score[0]));
            } catch (NumberFormatException e) {
                //Cannot set from field.
            }
            try {
                this.setScoreTo(Integer.parseInt(score[1]));
            } catch (NumberFormatException e) {
                //Cannot set to field.
            }
        }
        this.setPriority(guidanceRowResource.getPriority());
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
