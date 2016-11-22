package com.worth.ifs.competitionsetup.viewmodel;

import com.worth.ifs.competition.resource.CompetitionSetupQuestionType;
import com.worth.ifs.competition.resource.GuidanceRowResource;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by simon on 22/11/16.
 */
public class GuidanceRowViewModel {

    @NotEmpty(message = "{validation.applicationquestionform.justification.required}")
    private String justification;

    @NotEmpty(message = "{validation.applicationquestionform.subject.required}")
    private String subject;

    @Min(value=0, message = "{validation.applicationquestionform.scorefrom.min}")
    @NotNull(message = "{validation.applicationquestionform.scorefrom.required}")
    private Integer scoreFrom;

    @Min(value=0, message = "{validation.applicationquestionform.scoreto.min}")
    @NotNull(message = "{validation.applicationquestionform.scoreto.required}")
    private Integer scoreTo;

    public GuidanceRowViewModel() {
    }

    public GuidanceRowViewModel(CompetitionSetupQuestionType questionType, GuidanceRowResource guidanceRowResource) {

        this.setJustification(guidanceRowResource.getJustification());

        if (questionType.equals(CompetitionSetupQuestionType.ASSESSED_QUESTION)) {
            String[] score = guidanceRowResource.getSubject().split(",");
            this.setScoreFrom(Integer.parseInt(score[0]));
            this.setScoreTo(Integer.parseInt(score[1]));
            this.setSubject("dummy value");
        } else {
            this.setSubject(guidanceRowResource.getSubject());
            // Dummy score values so validation passes
            this.setScoreFrom(1);
            this.setScoreTo(10);
        }
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
