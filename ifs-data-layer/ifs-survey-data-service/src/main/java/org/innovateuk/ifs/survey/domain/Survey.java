package org.innovateuk.ifs.survey.domain;

import org.innovateuk.ifs.survey.Satisfaction;
import org.innovateuk.ifs.survey.SurveyTargetType;
import org.innovateuk.ifs.survey.SurveyType;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Entity
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "type")
    @Enumerated(STRING)
    private SurveyType surveyType;

    @Column(name = "target_type")
    @Enumerated(STRING)
    private SurveyTargetType targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "satisfaction")
    private Satisfaction satisfaction;

    @Column(name = "comments")
    private String comments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
