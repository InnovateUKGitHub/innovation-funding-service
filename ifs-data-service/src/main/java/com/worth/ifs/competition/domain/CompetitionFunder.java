package com.worth.ifs.competition.domain;


import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Entity model to store the Competition Co-Funders.
 */
@Entity
public class CompetitionFunder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="competition_id", referencedColumnName="id")
    private Competition competition;

    private String funder;
    private BigDecimal funderBudget;
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

    public String getFunder() {
        return funder;
    }

    public void setFunder(String funder) {
        this.funder = funder;
    }

    public BigDecimal getFunderBudget() {
        return funderBudget;
    }

    public void setFunderBudget(BigDecimal funderBudget) {
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
