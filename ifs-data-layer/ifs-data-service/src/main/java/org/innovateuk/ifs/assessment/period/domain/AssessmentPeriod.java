package org.innovateuk.ifs.assessment.period.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import javax.persistence.*;
import java.util.List;

/**
 * An Assessment Period.
 */
@Entity
public class AssessmentPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competition_id", referencedColumnName = "id")
    private Competition competition;

    @OneToMany(mappedBy="assessmentPeriod")
    public List<Milestone> milestones;

    @OneToMany(mappedBy="assessmentPeriod")
    public List<Application> applications;

    public AssessmentPeriod() {
        // default constructor
    }

    public AssessmentPeriod(Competition competition) {
        this.competition = competition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public boolean isInAssessment(){
        return !isAssessmentClosed() && milestones.stream().anyMatch(milestone -> MilestoneType.ASSESSORS_NOTIFIED.equals(milestone.getType()));
    }

    public boolean isAssessmentClosed(){
        return milestones.stream().anyMatch(milestone -> MilestoneType.ASSESSMENT_CLOSED.equals(milestone.getType()));
    }
}
