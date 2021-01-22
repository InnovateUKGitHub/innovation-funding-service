package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.FundingRules;
import org.innovateuk.ifs.finance.resource.FundingLevel;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import javax.persistence.*;
import java.math.BigDecimal;

import static javax.persistence.CascadeType.REMOVE;

/**
 * Base class for high-level Organisational Finances belonging to different aspects of the IFS application
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public abstract class Finance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="organisationId", referencedColumnName="id")
    private Organisation organisation;

    @Column(name = "organisation_size_id")
    private OrganisationSize organisationSize;

    @OneToOne(fetch = FetchType.LAZY, cascade = REMOVE)
    @JoinColumn(name = "employeesAndTurnoverId")
    private EmployeesAndTurnover employeesAndTurnover;

    @OneToOne(fetch = FetchType.LAZY, cascade = REMOVE)
    @JoinColumn(name = "growthTableId")
    private GrowthTable growthTable;

    @OneToOne(fetch = FetchType.LAZY, cascade = REMOVE)
    @JoinColumn(name = "ktpFinancialYearsId")
    private KtpFinancialYears ktpFinancialYears;

    private Boolean northernIrelandDeclaration;

    public Finance(Organisation organisation, OrganisationSize organisationSize,  GrowthTable growthTable, EmployeesAndTurnover employeesAndTurnover, KtpFinancialYears ktpFinancialYears, Boolean northernIrelandDeclaration) {
        this.organisation = organisation;
        this.organisationSize = organisationSize;
        this.growthTable = growthTable;
        this.employeesAndTurnover = employeesAndTurnover;
        this.ktpFinancialYears = ktpFinancialYears;
        this.northernIrelandDeclaration = northernIrelandDeclaration;
    }

    public Finance(Organisation organisation) {
        this.organisation = organisation;
    }

    public Finance(Long id, Organisation organisation) {
        this.id = id;
        this.organisation = organisation;
    }

    public Finance() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public String getOrganisationName() {
        return organisation.getName();
    }

    public FinancialYearAccounts getFinancialYearAccounts() {
        if (getGrowthTable() != null) {
            return getGrowthTable();
        } else if (getKtpFinancialYears() != null) {
            return getKtpFinancialYears();
        }
        return getEmployeesAndTurnover();
    }

    public EmployeesAndTurnover getEmployeesAndTurnover() {
        return employeesAndTurnover;
    }

    public void setEmployeesAndTurnover(EmployeesAndTurnover employeesAndTurnover) {
        this.employeesAndTurnover = employeesAndTurnover;
    }

    public GrowthTable getGrowthTable() {
        return growthTable;
    }

    public void setGrowthTable(GrowthTable growthTable) {
        this.growthTable = growthTable;
    }

    public KtpFinancialYears getKtpFinancialYears() {
        return ktpFinancialYears;
    }

    public void setKtpFinancialYears(KtpFinancialYears ktpFinancialYears) {
        this.ktpFinancialYears = ktpFinancialYears;
    }

    public Boolean getNorthernIrelandDeclaration() {
        return northernIrelandDeclaration;
    }

    public void setNorthernIrelandDeclaration(Boolean northernIrelandDeclaration) {
        this.northernIrelandDeclaration = northernIrelandDeclaration;
    }

    public Competition getCompetition() {
        return getApplication().getCompetition();
    }

    public abstract Application getApplication();

    public int getMaximumFundingLevel() {
        if (!isBusinessOrganisationType()) {
            return FundingLevel.HUNDRED.getPercentage();
        }

        if (getCompetition().isFullyFunded()) {
            return FundingLevel.HUNDRED.getPercentage();
        }

        boolean allMaximumsTheSame =
                getCompetition().getGrantClaimMaximums().stream()
                .filter(this::isMatchingFundingRules)
                .map(GrantClaimMaximum::getMaximum)
                .distinct()
                .count() == 1;
        if (allMaximumsTheSame) {
            return getCompetition().getGrantClaimMaximums().stream()
                    .filter(this::isMatchingFundingRules)
                    .findFirst()
                    .map(GrantClaimMaximum::getMaximum)
                    .orElse(0);
        }

        return getCompetition().getGrantClaimMaximums()
                .stream()
                .filter(this::isMatchingGrantClaimMaximum)
                .findFirst()
                .map(GrantClaimMaximum::getMaximum)
                .orElse(0);
    }

    public BigDecimal getMaximumFundingAmount() {
        return getCompetition().getCompetitionApplicationConfig().getMaximumFundingSought();
    }

    private boolean isMatchingGrantClaimMaximum(GrantClaimMaximum grantClaimMaximum) {
        return isMatchingResearchCategory(grantClaimMaximum) && isMatchingOrganisationSize(grantClaimMaximum)
                && (isMatchingFundingRules(grantClaimMaximum));
    }

    private boolean isMatchingFundingRules(GrantClaimMaximum grantClaimMaximum) {
        FundingRules ruleThatApplies = northernIrelandDeclaration == Boolean.TRUE && getCompetition().getFundingRules() == FundingRules.SUBSIDY_CONTROL
                ? FundingRules.STATE_AID
                : getCompetition().getFundingRules();
        return grantClaimMaximum.getFundingRules() == null || grantClaimMaximum.getFundingRules() == ruleThatApplies;
    }

    private boolean isMatchingOrganisationSize(GrantClaimMaximum grantClaimMaximum) {
        OrganisationSize organisationSize = getOrganisationSize();
        if (organisationSize == null) {
            return grantClaimMaximum.getOrganisationSize() == null;
        }
        return organisationSize == grantClaimMaximum.getOrganisationSize();
    }

    private boolean isMatchingResearchCategory(GrantClaimMaximum grantClaimMaximum) {
        return getApplication().getResearchCategory() != null &&
                grantClaimMaximum.getResearchCategory().getId().equals(getApplication().getResearchCategory().getId());
    }

    private boolean isBusinessOrganisationType() {
        return getOrganisation().getOrganisationType().getId().equals(OrganisationTypeEnum.BUSINESS.getId());
    }
}
