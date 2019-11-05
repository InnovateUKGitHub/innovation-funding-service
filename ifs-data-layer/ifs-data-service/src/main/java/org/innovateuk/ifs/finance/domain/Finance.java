package org.innovateuk.ifs.finance.domain;

import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.resource.OrganisationSize;
import org.innovateuk.ifs.organisation.domain.Organisation;

import javax.persistence.*;

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

    public abstract Competition getCompetition();
}
