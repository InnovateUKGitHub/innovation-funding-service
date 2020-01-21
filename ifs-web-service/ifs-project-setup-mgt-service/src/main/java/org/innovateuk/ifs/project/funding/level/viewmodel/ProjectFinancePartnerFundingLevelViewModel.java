package org.innovateuk.ifs.project.funding.level.viewmodel;

import org.innovateuk.ifs.finance.resource.OrganisationSize;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProjectFinancePartnerFundingLevelViewModel {
    private final long id;
    private final String name;
    private final boolean lead;
    private final int maximumFundingLevel;
    private final OrganisationSize organisationSize;
    private final BigDecimal costs;
    private final BigDecimal fundingLevel;
    private final BigDecimal otherFunding;
    private final BigDecimal totalGrant;

    public ProjectFinancePartnerFundingLevelViewModel(long id,
                                                      String name,
                                                      boolean lead,
                                                      int maximumFundingLevel,
                                                      OrganisationSize organisationSize,
                                                      BigDecimal costs,
                                                      BigDecimal fundingLevel,
                                                      BigDecimal otherFunding,
                                                      BigDecimal totalGrant) {
        this.id = id;
        this.name = name;
        this.lead = lead;
        this.maximumFundingLevel = maximumFundingLevel;
        this.organisationSize = organisationSize;
        this.costs = costs;
        this.fundingLevel = fundingLevel;
        this.otherFunding = otherFunding;
        this.totalGrant = totalGrant;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getCosts() {
        return costs;
    }

    public int getMaximumFundingLevel() {
        return maximumFundingLevel;
    }

    public BigDecimal getFundingLevel() {
        return fundingLevel;
    }

    public BigDecimal getOtherFunding() {
        return otherFunding;
    }

    public BigDecimal getTotalGrant() {
        return totalGrant;
    }

    /* view logic */
    public BigDecimal getFundingSought() {
        return calculateFundingSought(fundingLevel);
    }

    public BigDecimal getPercentageOfTotalGrant() {
        return calculatePercentageTotalGrant(fundingLevel);
    }

    public BigDecimal calculateFundingSought(BigDecimal calcFundingLevel) {
        if (calcFundingLevel == null) {
            return BigDecimal.valueOf(0);
        }

        return costs.multiply(calcFundingLevel)
                .divide(new BigDecimal(100), RoundingMode.HALF_UP)
                .subtract(otherFunding)
                .max(BigDecimal.ZERO);
    }

    public BigDecimal calculatePercentageTotalGrant(BigDecimal calcFundingLevel) {
        return calculateFundingSought(calcFundingLevel)
                .multiply(new BigDecimal(100))
                .divide(totalGrant, 10, RoundingMode.HALF_UP);
    }

    public String getSubtitle() {
        String part = organisationSize != null ? organisationSize.getDescription() : "Academic";
        String text = String.format("%s, %d%%", part, maximumFundingLevel);
        if (lead) {
            text = "Lead, " + text.toLowerCase();
        }
        return text;
    }
}