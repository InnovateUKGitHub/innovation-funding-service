package org.innovateuk.ifs.competition.domain;

import javax.persistence.*;

/**
 * An Assessment Period.
 */
@Entity
public class AssessmentPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rank")
    private Integer index;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competition_id", referencedColumnName = "id")
    private Competition competition;

    public AssessmentPeriod() {
        // default constructor
    }

    public AssessmentPeriod(Competition competition, Integer index) {
        this.competition = competition;
        this.index = index;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }
}
