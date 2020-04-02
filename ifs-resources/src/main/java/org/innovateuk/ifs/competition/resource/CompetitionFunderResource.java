package org.innovateuk.ifs.competition.resource;

import java.math.BigInteger;

public class CompetitionFunderResource {

    private Long id;
    private Funder funder;
    private BigInteger funderBudget;
    private Boolean coFunder;
    private Long competitionId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }
}
