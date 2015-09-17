package com.worth.ifs.workflow.domain;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.user.domain.User;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Created by nunoalexandre on 17/09/15.
 */
@Entity
@Table(name = "AssessmentProcess",
        uniqueConstraints = @UniqueConstraint(columnNames = {"assessor", "application", "event"}))
public class AssessmentProcess extends Process {

    @ManyToOne
    @JoinColumn(name="assessor", referencedColumnName="id")
    private User assessor;

    @ManyToOne
    @JoinColumn(name="application", referencedColumnName="id")
    private Application application;

    public AssessmentProcess(){}

    public AssessmentProcess(ProcessStatus status, User assessor, Application application, LocalDate startDate, LocalDate endDate, String observations) {
        super(ProcessEvent.ASSESSMENT_INVITATION, status, startDate, endDate, observations);
        this.assessor = assessor;
        this.application = application;
    }

    public User getAssessor() {
        return assessor;
    }

    public void setAssessor(User user) {
        this.assessor = user;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
