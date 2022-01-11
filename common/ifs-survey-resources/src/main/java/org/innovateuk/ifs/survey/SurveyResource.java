package org.innovateuk.ifs.survey;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class SurveyResource {

    private SurveyType surveyType;

    private SurveyTargetType targetType;

    private Long targetId;

    private Satisfaction satisfaction;

    private String comments;

    public SurveyResource() {
    }

    public SurveyResource(SurveyType surveyType,
                          SurveyTargetType targetType,
                          Long targetId,
                          Satisfaction satisfaction,
                          String comments) {
        this.surveyType = surveyType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.satisfaction = satisfaction;
        this.comments = comments;
    }

    public SurveyType getSurveyType() {
        return surveyType;
    }

    public void setSurveyType(SurveyType surveyType) {
        this.surveyType = surveyType;
    }

    public SurveyTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(SurveyTargetType targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Satisfaction getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(Satisfaction satisfaction) {
        this.satisfaction = satisfaction;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
