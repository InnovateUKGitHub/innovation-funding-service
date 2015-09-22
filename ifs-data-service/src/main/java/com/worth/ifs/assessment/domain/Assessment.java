package com.worth.ifs.assessment.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.assessment.constant.AssessmentStatus;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.workflow.domain.Process;
import com.worth.ifs.workflow.domain.ProcessEvent;
import com.worth.ifs.workflow.domain.ProcessStatus;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;
import org.hibernate.annotations.Type;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
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

    @OneToMany
    private Map<Long,ResponseAssessment> responseAssessments;

    @Column(name = "submitted", columnDefinition = "boolean default false")
    @Type(type = "yes_no")
    private boolean submitted;


    public Assessment() {
        if (responseAssessments == null)
            responseAssessments = new LinkedHashMap<>();
    }

    public Assessment(User assessor, Application application) {
        super(ProcessEvent.ASSESSMENT, ProcessStatus.PENDING);
        this.assessor = assessor;
        this.application = application;
        responseAssessments = new HashMap<>();
    }

    public void setSubmitted() {
        submitted = true;
    }

    @Override
    public ProcessStatus getStatus() {
        return status;
    }

    public AssessmentStatus getAssessmentStatus() {
        return solveStatus();
    }

    public Map<Long, ResponseAssessment> getResponseAssessments() {
        return responseAssessments;
    }

    public void addResponseAssessment(ResponseAssessment responseAssessment) {
        this.responseAssessments.put(responseAssessment.getResponseId(), responseAssessment);
    }

    public void respondToAssessmentInvitation(boolean hasAccepted, String reason, String observations) {
        setStatus(hasAccepted ? ProcessStatus.ACCEPTED : ProcessStatus.REJECTED);
        setDecisionReason(reason);
        setObservations(observations);
    }

    public boolean hasAssessments() {
        return this.responseAssessments != null && this.responseAssessments.size() > 0;
    }

    public boolean isSubmitted() {
        return this.submitted;
    }

    public User getAssessor() {
        return assessor;
    }

    public Application getApplication() {
        return application;
    }


    // follows a concrete criteria
    private AssessmentStatus solveStatus() {

        AssessmentStatus status = AssessmentStatus.INVALID;

        if (super.getStatus().equals(ProcessStatus.REJECTED))
            status = AssessmentStatus.INVALID;

        else if (super.getStatus().equals(ProcessStatus.PENDING))
            status = AssessmentStatus.PENDING;

        else if (super.getStatus().equals(ProcessStatus.ACCEPTED))
            status = isSubmitted() ? AssessmentStatus.SUBMITTED : AssessmentStatus.OPEN;


        return status;
    }


}
