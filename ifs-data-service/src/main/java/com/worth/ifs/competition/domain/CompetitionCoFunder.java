package com.worth.ifs.competition.domain;


import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by skistapur on 18/07/2016.
 *
 * Entity model to store the Competition Co-Funders.
 */
@Entity
public class CompetitionCoFunder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="competition_id", referencedColumnName="id")
    private Competition competition;

    @Column(name = "co_funder")
    private String coFunder;

    @Column(name = "co_funder_budget")
    private BigDecimal coFunderBudget;

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

    public String getCoFunder() {
        return coFunder;
    }

    public void setCoFunder(String coFunder) {
        this.coFunder = coFunder;
    }

    public BigDecimal getCoFunderBudget() {
        return coFunderBudget;
    }

    public void setCoFunderBudget(BigDecimal coFunderBudget) {
        this.coFunderBudget = coFunderBudget;
    }

    @Override
    public String toString() {
        return "CompetitionCoFunder{" +
                "id=" + id +
                ", competition=" + competition +
                ", coFunder='" + coFunder + '\'' +
                ", coFunderBudget=" + coFunderBudget +
                '}';
    }
}
