package org.innovateuk.ifs.assessment.period.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.Milestone;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.time.ZonedDateTime.now;
import static java.util.stream.Stream.empty;
import static org.innovateuk.ifs.competition.resource.MilestoneType.ASSESSMENT_CLOSED;
import static org.innovateuk.ifs.competition.resource.MilestoneType.ASSESSORS_NOTIFIED;

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
    public List<Milestone> milestones = new ArrayList<>();

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
        return !isAssessmentClosed() && reached().anyMatch(milestone -> ASSESSORS_NOTIFIED.equals(milestone.getType()));
    }

    public boolean isAssessmentClosed(){
        return reached().anyMatch(milestone -> ASSESSMENT_CLOSED.equals(milestone.getType()));
    }

    private Stream<Milestone> reached(){
        return milestones == null ? empty():
                milestones.stream()
                        .filter(milestone -> milestone.getDate() != null)
                        .filter(milestone -> milestone.isReached(now()));
    }

    public boolean isOpen() {
        return milestones.stream().anyMatch(m -> m.getType().equals(ASSESSORS_NOTIFIED))
                && milestones.stream().noneMatch(m -> m.getType().equals(ASSESSMENT_CLOSED));
    }
}
