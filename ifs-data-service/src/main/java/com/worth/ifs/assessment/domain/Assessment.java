package com.worth.ifs.assessment.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.workflow.domain.Process;
import com.worth.ifs.workflow.domain.ProcessEvent;
import com.worth.ifs.workflow.domain.ProcessStates;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;
import org.hibernate.annotations.Type;
import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "Assessment", uniqueConstraints = @UniqueConstraint(columnNames = {"assessor", "application"}))
@Polymorphism(type= PolymorphismType.EXPLICIT)
@PrimaryKeyJoinColumn(name="process_id", referencedColumnName="id")
public class Assessment extends Process {


    @ManyToOne
    @JoinColumn(name = "assessor", referencedColumnName = "id")
    private User assessor;

    @ManyToOne
    @JoinColumn(name = "application", referencedColumnName = "id")
    private Application application;

    //@OneToMany
    @Transient
    private Map<Long, ResponseAssessment> responseAssessments;


    /******* TEMPORARY ********/

    @Column(name="temp_totalScore")
    private Double overallScore;

    @Enumerated(EnumType.STRING)
    @Column(name="temp_RecommendedValue")
    private RecommendedValue recommendedValue;

    @Column(name="recommendation_feedback")
    private String suitableFeedback;
    @Column(name="comments")
    private String comments;

    /******* END TEMPORARY ********/


    @Type(type = "yes_no")
    private Boolean submitted;


    public Assessment() {
        if (responseAssessments == null)
            responseAssessments = new LinkedHashMap<>();
    }

    public Assessment(User assessor, Application application) {
        super(ProcessEvent.ASSESSMENT.name(), AssessmentStates.PENDING.name());
        this.assessor = assessor;
        this.application = application;
        responseAssessments = new HashMap<>();
        recommendedValue = RecommendedValue.EMPTY;
    }

    public void setSummary(RecommendedValue value, String feedback, String comments) {
        this.recommendedValue = value;
        this.suitableFeedback = feedback;
        this.comments = comments;
    }

    public void submit() {
        submitted = true;
    }

    @Override
    public String getProcessStatus() {
        return status;
    }

    public Map<Long, ResponseAssessment> getResponseAssessments() {
        return responseAssessments;
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
    /******* TEMPORARY ********/

    public Double getOverallScore() {
        // nItems cant be 0 - math indetermination
        //Integer nItems = Math.max(1, responseAssessments.size());
        //Double.valueOf(getTotalScore() / nItems);
        return overallScore;
    }

//    private Integer getTotalScore() {
//        return responseAssessments.values().stream().mapToInt(i -> i.getScore()).sum();
//    }

    public void addResponseAssessment(ResponseAssessment responseAssessment) {
        this.responseAssessments.put(responseAssessment.getResponseId(), responseAssessment);
    }

    public boolean hasAssessments() {
        return this.responseAssessments != null && this.responseAssessments.size() > 0;
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

