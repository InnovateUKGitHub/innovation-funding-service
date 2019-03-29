package org.innovateuk.ifs.competition.domain;


import org.innovateuk.ifs.competition.resource.Funder;

import javax.persistence.*;
import java.math.BigInteger;

import static javax.persistence.EnumType.*;

/**
 * Entity model to store the Competition Co-Funders.
 */
@Entity
public class CompetitionFunder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="competition_id", referencedColumnName="id")
    private Competition competition;

    @Enumerated(STRING)
    private Funder funder;

    private BigInteger funderBudget;

    private Boolean coFunder;

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

    public Funder getFunder() {
        return funder;
    }

    public void setFunder(Funder funder) {
        this.funder = funder;
    }

    public BigInteger getFunderBudget() {
        return funderBudget;
    }

    public void setFunderBudget(BigInteger funderBudget) {
        this.funderBudget = funderBudget;
    }

    public Boolean getCoFunder() {
        return coFunder;
    }

    public void setCoFunder(Boolean coFunder) {
        this.coFunder = coFunder;
    }

    @Override
    public String toString() {
        return "CompetitionFunder{" +
                "id=" + id +
                ", competition=" + competition +
                ", funder='" + funder + '\'' +
                ", funderBudget=" + funderBudget +
                ", coFunder =" + coFunder +
                '}';
    }
}
