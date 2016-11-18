package com.worth.ifs.competition.resource;

import java.math.BigDecimal;

public class CompetitionFunderResource {

    private Long id;
    private String funder;
    private BigDecimal funderBudget;
    private Boolean coFunder;
    private Long competitionId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
}
