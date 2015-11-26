package com.worth.ifs.assessment.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.workflow.domain.Process;
import com.worth.ifs.workflow.domain.ProcessEvent;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Recommendation defines database relations and a model to use client side and server side.
 */
@Entity
@Table(name = "Recommendation", uniqueConstraints = @UniqueConstraint(columnNames = {"assessor", "application"}))
@Polymorphism(type= PolymorphismType.EXPLICIT)
@PrimaryKeyJoinColumn(name="process_id", referencedColumnName="id")
public class Recommendation {


    @ManyToOne
    @JoinColumn(name = "assessor", referencedColumnName = "id")
    private User assessor;

    @ManyToOne
    @JoinColumn(name = "application", referencedColumnName = "id")
    private Application application;

    @Column(name="temp_totalScore")
    private Double overallScore;

    @Enumerated(EnumType.STRING)
    @Column
    private RecommendedValue recommendedValue;

    @Column
    private String suitableFeedback;
    @Column(name="comments")
    private String comments;

    @Type(type = "yes_no")
    private Boolean submitted = false;

    public Recommendation() {
    }

    public Recommendation(User assessor, Application application) {
        this.assessor = assessor;
        this.application = application;
        recommendedValue = RecommendedValue.EMPTY;
    }

    public void setSummary(RecommendedValue value, String feedback, String comments, Double overallScore) {
        this.recommendedValue = value;
        this.suitableFeedback = feedback;
        this.comments = comments;
        this.overallScore = overallScore;
    }

    public void submit() {
        submitted = true;
    }

    public Boolean hasAssessmentStarted() {
        return  ! recommendedValue.equals(RecommendedValue.EMPTY) ;
    }

    public RecommendedValue getRecommendedValue() {
        return recommendedValue;
    }

    public String getSuitableFeedback() {
        return suitableFeedback;
    }
    public String getComments() {
        return comments;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public Boolean isSubmitted() {
        return this.submitted;
    }

    public User getAssessor() {
        return assessor;
    }

    public Application getApplication() {
        return application;
    }

}

