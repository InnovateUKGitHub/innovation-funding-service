package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.competition.resource.MilestoneType;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Represents a {@link Competition} Milestone, with or without a preset date.
 * {@link Milestone}s may have an assessment period {@link AssessmentPeriod} attached to them.
 */
@Entity
@DiscriminatorValue("MILESTONE")
public class Milestone extends MilestoneBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private AssessmentPeriod assessmentPeriod;

    Milestone() {
        // default constructor
    }

    public Milestone(MilestoneType type, Competition competition) {
        super(type, competition);
    }

    public Milestone(MilestoneType type, ZonedDateTime date, Competition competition) {
        super(type, date, competition);
    }

    public Milestone(MilestoneType type, ZonedDateTime date, Competition competition, AssessmentPeriod assessmentPeriod) {
        super(type, date, competition);
        if (assessmentPeriod == null) {
            throw new NullPointerException("assessment period cannot be null");
        }
    }

    public AssessmentPeriod getAssessmentPeriod() {
        return assessmentPeriod;
    }

    public void setAssessmentPeriod(AssessmentPeriod assessmentPeriod) {
        this.assessmentPeriod = assessmentPeriod;
    }
}
