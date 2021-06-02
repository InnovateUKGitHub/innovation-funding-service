package org.innovateuk.ifs.competition.domain;

import org.innovateuk.ifs.assessment.period.domain.AssessmentPeriod;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.function.Consumer;

import static org.innovateuk.ifs.competition.resource.MilestoneType.assessmentPeriodValues;

/**
 * Represents a {@link Competition} Milestone, with or without a preset date.
 * {@link Milestone}s may have an assessment period {@link AssessmentPeriod} attached to them.
 */
@Entity
public class Milestone {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private MilestoneType type;

    private ZonedDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="competition_id", referencedColumnName="id")
    private Competition competition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id", referencedColumnName="id")
    private AssessmentPeriod assessmentPeriod;

    private Milestone() {
    }

    public Milestone(MilestoneType type, Competition competition) {
        if (type == null) { throw new NullPointerException("type cannot be null"); }
        if (competition == null) { throw new NullPointerException("competition cannot be null"); }

        this.type = type;
        this.competition = competition;
    }

    public Milestone(MilestoneType type, ZonedDateTime date, Competition competition) {
        if (type == null) { throw new NullPointerException("type cannot be null"); }
        if (competition == null) { throw new NullPointerException("competition cannot be null"); }
        if (date == null) { throw new NullPointerException("date cannot be null"); }

        this.type = type;
        this.date = date;
        this.competition = competition;
    }

    public Milestone(MilestoneType type, Competition competition, AssessmentPeriod assessmentPeriod) {
        if (type == null) { throw new IllegalArgumentException("type cannot be null"); }
        if (competition == null) { throw new IllegalArgumentException("competition cannot be null"); }
        if (assessmentPeriod != null) {
            if (!assessmentPeriodValues().contains(type)) {
                throw new IllegalArgumentException("Only assessment period milestones can be linked to an assessment period");
            }
        }

        this.type = type;
        this.competition = competition;
        this.assessmentPeriod = assessmentPeriod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Competition getCompetition() {
        return competition;
    }

    public MilestoneType getType() {
        return type;
    }

    public void setType(MilestoneType type) {
        this.type = type;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public AssessmentPeriod getAssessmentPeriod() {
        return assessmentPeriod;
    }

    public void setAssessmentPeriod(AssessmentPeriod assessmentPeriod) {
        this.assessmentPeriod = assessmentPeriod;
    }

    public boolean isSet() {
        assert date != null || !type.isPresetDate();
        return date != null;
    }

    public void ifSet(Consumer<ZonedDateTime> consumer) {
        if (date != null) {
            consumer.accept(date);
        }
    }

    public boolean isReached(ZonedDateTime now) {
        return date != null && !date.isAfter(now);
    }
}
