package com.worth.ifs.competition.resource;

import java.math.BigDecimal;

/**
 * Created by skistapur on 18/07/2016.
 */
public class CompetitionCoFunderResource {

    private Long id;
    private String coFunder;
    private BigDecimal coFunderBudget;
    private Long competitionId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
}
