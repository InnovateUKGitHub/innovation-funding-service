package org.innovateuk.ifs.application.forms.sections.yourprojectfinances.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class YourProjectFinancesViewModel implements BaseAnalyticsViewModel {
    private final long applicationId;
    private final String competitionName;
    private final String applicationName;
    private final boolean h2020;
    private final boolean collaborativeProject;
    private final boolean fullyFunded;

    private final BigDecimal costs;
    private final BigDecimal claimPercentage;
    private final BigDecimal fundingSought;
    private final BigDecimal otherFunding;
    private final BigDecimal contribution;
    private final boolean fundingLevelFirst;

    private final List<YourFinancesRowViewModel> rows;

    public YourProjectFinancesViewModel(long applicationId, String applicationName, CompetitionResource competition, ApplicationFinanceResource organisationFinance, List<YourFinancesRowViewModel> rows) {
        this.applicationId = applicationId;
        this.competitionName = competition.getName();
        this.applicationName = applicationName;
        this.h2020 = competition.isH2020();
        this.collaborativeProject = !CollaborationLevel.SINGLE.equals(competition.getCollaborationLevel());
        this.fullyFunded = competition.isFullyFunded();
        this.costs = Optional.ofNullable(organisationFinance).map(ApplicationFinanceResource::getTotal).orElse(BigDecimal.ZERO);
        this.claimPercentage = Optional.ofNullable(organisationFinance).map(ApplicationFinanceResource::getGrantClaimPercentage).orElse(BigDecimal.ZERO);
        this.fundingSought = Optional.ofNullable(organisationFinance).map(ApplicationFinanceResource::getTotalFundingSought).orElse(BigDecimal.ZERO);
        this.otherFunding = Optional.ofNullable(organisationFinance).map(ApplicationFinanceResource::getTotalOtherFunding).orElse(BigDecimal.ZERO);
        this.contribution = Optional.ofNullable(organisationFinance).map(ApplicationFinanceResource::getTotalContribution).orElse(BigDecimal.ZERO);
        this.fundingLevelFirst = competition.getFinanceRowTypes().contains(FinanceRowType.FINANCE);
        this.rows = rows;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
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

    public boolean isFundingLevelFirst() {
        return fundingLevelFirst;
    }

    public boolean isFundingSoughtFirst() {
        return !isFundingLevelFirst();
    }


    public List<YourFinancesRowViewModel> getRows() {
        return rows;
    }
}
