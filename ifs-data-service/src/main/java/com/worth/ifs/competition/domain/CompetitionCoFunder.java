package com.worth.ifs.competition.domain;

import com.worth.ifs.commons.domain.DomainObject;

import javax.persistence.*;

/**
 * Created by skistapur on 18/07/2016.
 */
@Entity
public class CompetitionCoFunder extends DomainObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name="competitionId", referencedColumnName="id")
    private Competition competition;

    @Column(name = "co_funder")
    private String coFunder;

    @Column(name = "co_funder_budget")
    private Double coFunderBudget;

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

    public Double getCoFunderBudget() {
        return coFunderBudget;
    }

    public void setCoFunderBudget(Double coFunderBudget) {
        this.coFunderBudget = coFunderBudget;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompetitionCoFunder that = (CompetitionCoFunder) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (coFunder != null ? !coFunder.equals(that.coFunder) : that.coFunder != null) return false;
        return coFunderBudget != null ? coFunderBudget.equals(that.coFunderBudget) : that.coFunderBudget == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (coFunder != null ? coFunder.hashCode() : 0);
        result = 31 * result + (coFunderBudget != null ? coFunderBudget.hashCode() : 0);
        return result;
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
