package org.innovateuk.ifs.application.forms.sections.yourfinances.viewmodel;

import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class YourFinancesViewModel {
    private final long applicationId;
    private final String applicationName;
    private final boolean h2020;
    private final boolean collaborativeProject;
    private final boolean fullyFunded;

    private final BigDecimal costs;
    private final Integer claimPercentage;
    private final BigDecimal fundingSought;
    private final BigDecimal otherFunding;
    private final BigDecimal contribution;

    private final List<YourFinancesRowViewModel> rows;

    public YourFinancesViewModel(long applicationId, String applicationName, CompetitionResource competition, ApplicationFinanceResource organisationFinance, List<YourFinancesRowViewModel> rows) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.h2020 = competition.isH2020();
        this.collaborativeProject = !CollaborationLevel.SINGLE.equals(competition.getCollaborationLevel());
        this.fullyFunded = competition.isFullyFunded();
        this.costs = Optional.ofNullable(organisationFinance.getTotal()).orElse(BigDecimal.ZERO);
        this.claimPercentage = Optional.ofNullable(organisationFinance.getGrantClaimPercentage()).orElse(0);
        this.fundingSought = Optional.ofNullable(organisationFinance.getTotalFundingSought()).orElse(BigDecimal.ZERO);
        this.otherFunding = Optional.ofNullable(organisationFinance.getTotalOtherFunding()).orElse(BigDecimal.ZERO);
        this.contribution = Optional.ofNullable(organisationFinance.getTotalContribution()).orElse(BigDecimal.ZERO);
        this.rows = rows;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public boolean isH2020() {
        return h2020;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isFullyFunded() {
        return fullyFunded;
    }


    public BigDecimal getCosts() {
        return costs;
    }

    public Integer getClaimPercentage() {
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

    public List<YourFinancesRowViewModel> getRows() {
        return rows;
    }
}
