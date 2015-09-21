package com.worth.ifs.assessment.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.assessment.constant.AssessmentStatus;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.workflow.domain.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


@NamedQueries({

        @NamedQuery(
                name = "findByProcessAssessor",
                query = "from Assessment a, Process p where a.process = p and p.assessor = :assessor"
        ),
        @NamedQuery(
                name = "findByProcessAssessorAndProcessApplicationCompetition",
                query = "from Assessment a, Process p where a.process = p and p.assessor = :assessor and p.application.competition = :competition"
        )

})
@Entity
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="process_id", referencedColumnName="id")
    private AssessmentProcess process;

    @OneToMany
    private Set<ResponseAssessment> responseAssessments;

    @Type(type="yes_no")
    private boolean submitted;


    public Assessment(){
        if ( responseAssessments == null )
            responseAssessments = new LinkedHashSet<>();
    }

    @PostConstruct
    private void init() {
        if ( responseAssessments == null )
            responseAssessments = new LinkedHashSet<>();
    }

    public Assessment(AssessmentProcess process) {
        this.process = process;
        this.submitted = false;
        responseAssessments = new LinkedHashSet<>();
    }

    public void setSubmitted() {
        submitted = true;
    }

    public Long getId() {
        return this.id;
    }

    public AssessmentStatus getStatus() {
        return solveStatus();
    }

    public Set<ResponseAssessment> getResponseAssessments() {
        return responseAssessments;
    }

    public void addResponseAssessment(ResponseAssessment assessment) {
        //TODO - must replace to update if exists
        this.responseAssessments.add(assessment);
    }

    public void respondToAssessmentInvitation(boolean hasAccepted, String reason, String observations) {
        process.setStatus( hasAccepted ? ProcessStatus.ACCEPTED : ProcessStatus.REJECTED );
        process.setDecisionReason(reason);
        process.setObservations(observations);
    }

    public boolean hasAssessments() {
        return this.responseAssessments != null && this.responseAssessments.size() > 0;
    }

    public boolean isSubmitted() {
        return this.submitted;
    }


    public User getAssessor() {
        return process.getAssessor();
    }
    public Application getApplication() {
        return process.getApplication();
    }

    public AssessmentProcess getProcess() {
        return process;
    }


    //
    private AssessmentStatus solveStatus() {

        AssessmentStatus status = AssessmentStatus.INVALID;

        if ( this.process == null || this.process.getStatus().equals(ProcessStatus.REJECTED) )
            status = AssessmentStatus.INVALID;

        else if ( this.process.getStatus().equals(ProcessStatus.PENDING) )
            status = AssessmentStatus.PENDING;

        else if ( this.process.getStatus().equals(ProcessStatus.ACCEPTED) )
            status = isSubmitted() ? AssessmentStatus.SUBMITTED : AssessmentStatus.OPEN;


        return status;
    }


}
