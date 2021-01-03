package org.innovateuk.ifs.competition.domain;

import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.DiscriminatorOptions;
import org.innovateuk.ifs.competition.resource.MilestoneType;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.function.Consumer;

/**
 * Abstract MilestoneBase.
 */
@Entity
@DiscriminatorFormula(
        "CASE WHEN type IN ('ASSESSMENT_PERIOD') THEN 'ASSESSMENT_PERIOD' ELSE 'MILESTONE' end"
)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorOptions(force = true)
@Table(name = "milestone")
public abstract class MilestoneBase {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private MilestoneType type;

    private ZonedDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="competition_id", referencedColumnName="id")
    private Competition competition;

    MilestoneBase() {

    }

    protected MilestoneBase(MilestoneType type, Competition competition) {
        if (type == null) { throw new NullPointerException("type cannot be null"); }
        if (competition == null) { throw new NullPointerException("competition cannot be null"); }

        this.type = type;
        this.competition = competition;
    }

    protected MilestoneBase(MilestoneType type, ZonedDateTime date, Competition competition) {
        if (type == null) { throw new NullPointerException("type cannot be null"); }
        if (competition == null) { throw new NullPointerException("competition cannot be null"); }
        if (date == null) { throw new NullPointerException("date cannot be null"); }

        this.type = type;
        this.date = date;
        this.competition = competition;
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
