package org.innovateuk.ifs.assessment.period.domain;

import org.innovateuk.ifs.competition.domain.Competition;

import javax.persistence.*;

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
}
