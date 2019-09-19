package org.innovateuk.ifs.finance.resource;

import java.math.BigDecimal;

/**
 * A class used to capture "Your organisation" information without a growth table
 */
public class OrganisationFinancesWithoutGrowthTableResource {

    private OrganisationSize organisationSize;
    private BigDecimal turnover;
    private Integer headCount;

    private Boolean stateAidAgreed;

    public OrganisationFinancesWithoutGrowthTableResource(
            OrganisationSize organisationSize,
            BigDecimal turnover,
            Integer headCount,
            Boolean stateAidAgreed) {

        this.organisationSize = organisationSize;
        this.turnover = turnover;
        this.headCount = headCount;
        this.stateAidAgreed = stateAidAgreed;
    }

    OrganisationFinancesWithoutGrowthTableResource() {
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }

    public Integer getHeadCount() {
        return headCount;
    }

    public void setHeadCount(Integer headCount) {
        this.headCount = headCount;
    }

    public Boolean getStateAidAgreed() {
        return stateAidAgreed;
    }

    public void setStateAidAgreed(Boolean stateAidAgreed) {
        this.stateAidAgreed = stateAidAgreed;
    }
}