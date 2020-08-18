package org.innovateuk.ifs.application.finance.viewmodel;

import java.math.BigDecimal;

public class FinanceSummaryTableRow {

    private final Long organisationId;
    private final String organisationName;
    private final String status;

    private final BigDecimal costs;
    private final BigDecimal claimPercentage;
    private final BigDecimal fundingSought;
    private final BigDecimal otherFunding;
    private final BigDecimal contribution;

    private final boolean complete;
    private final boolean showViewFinancesLink;
    private final String url;

    public FinanceSummaryTableRow(Long organisationId,
                                  String organisationName,
                                  String status,
                                  BigDecimal costs,
                                  BigDecimal claimPercentage,
                                  BigDecimal fundingSought,
                                  BigDecimal otherFunding,
                                  BigDecimal contribution,
                                  boolean complete,
                                  boolean showViewFinancesLink,
                                  String url) {
        this.organisationId = organisationId;
        this.organisationName = organisationName;
        this.status = status;
        this.costs = costs;
        this.claimPercentage = claimPercentage;
        this.fundingSought = fundingSought;
        this.otherFunding = otherFunding;
        this.contribution = contribution;
        this.complete = complete;
        this.showViewFinancesLink = showViewFinancesLink;
        this.url = url;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getCosts() {
        return costs;
    }

    public BigDecimal getClaimPercentage() {
        return claimPercentage;
    }

    public BigDecimal getFundingSought() {
        return fundingSought;
    }

    public BigDecimal getOtherFunding() {
        return otherFunding;
    }

    public BigDecimal getContribution() {
        return contribution;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isShowViewFinancesLink() {
        return showViewFinancesLink;
    }

    public String getUrl() {
        return url;
    }

    /* view logic */
    public boolean isPendingOrganisation() {
        return organisationId == null;
    }

    public static FinanceSummaryTableRow pendingOrganisation(String organisationName) {
        return new FinanceSummaryTableRow(
                null,
                organisationName,
                "(pending)",
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                false,
                false,
                null
        );
    }
}
