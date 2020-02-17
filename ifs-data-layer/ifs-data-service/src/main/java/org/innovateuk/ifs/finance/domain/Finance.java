package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.resource.FundingLevel;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import javax.persistence.*;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
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

    public Finance(Organisation organisation, OrganisationSize organisationSize,  GrowthTable growthTable, EmployeesAndTurnover employeesAndTurnover) {
        this.organisation = organisation;
        this.organisationSize = organisationSize;
        this.growthTable = growthTable;
        this.employeesAndTurnover = employeesAndTurnover;
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

        if (isMaximumFundingLevelOverridden()) {
            // The same maximum funding level is set for all GrantClaimMaximums when overriding
            return getCompetition().getGrantClaimMaximums().stream().findAny().map(GrantClaimMaximum::getMaximum).get();
        }

        return getCompetition().getGrantClaimMaximums()
                .stream()
                .filter(this::isMatchingGrantClaimMaximum)
                .findFirst()
                .map(GrantClaimMaximum::getMaximum)
                .orElse(0);
    }
    private boolean isMatchingGrantClaimMaximum(GrantClaimMaximum grantClaimMaximum) {
        return isMatchingResearchCategory(grantClaimMaximum) && isMatchingOrganisationSize(grantClaimMaximum);
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

    private boolean isMaximumFundingLevelOverridden() {
        Set<Long> competitionGrantClaimMaximumIds = getCompetition().getGrantClaimMaximums().stream()
                .map(GrantClaimMaximum::getId)
                .collect(toSet());
        Set<Long> templateGrantClaimMaximumIds = getCompetition().getCompetitionType().getTemplate()
                .getGrantClaimMaximums().stream().map(GrantClaimMaximum::getId).collect(toSet());
        return !competitionGrantClaimMaximumIds.equals(templateGrantClaimMaximumIds);
    }

}
