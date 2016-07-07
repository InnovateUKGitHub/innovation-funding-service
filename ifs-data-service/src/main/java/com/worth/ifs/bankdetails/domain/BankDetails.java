package com.worth.ifs.bankdetails.domain;

import com.worth.ifs.organisation.domain.OrganisationAddress;
import com.worth.ifs.project.domain.Project;
import com.worth.ifs.user.domain.Organisation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;

/**
 * Entity for persisting Bank Details for organisations associated with a project.
 */
@Entity
public class BankDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String sortCode;

    private String accountNumber;

    @ManyToOne
    @JoinColumn(name = "projectId" , referencedColumnName = "id")
    private Project project;

    @OneToOne (cascade = CascadeType.ALL, orphanRemoval = true)   // Using cascade because entries with type "BANK_DETAILS" should be added/removed via bank details
    @JoinColumn(name = "organisationAddressId", referencedColumnName = "id")
    private OrganisationAddress organisationAddress;

    @OneToOne
    @JoinColumn(name = "organisationId", referencedColumnName = "id")
    private Organisation organisation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public OrganisationAddress getOrganisationAddress() {
        return organisationAddress;
    }

    public void setOrganisationAddress(OrganisationAddress organisationAddress) {
        this.organisationAddress = organisationAddress;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BankDetails that = (BankDetails) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(sortCode, that.sortCode)
                .append(accountNumber, that.accountNumber)
                .append(project, that.project)
                .append(organisationAddress, that.organisationAddress)
                .append(organisation, that.organisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(sortCode)
                .append(accountNumber)
                .append(project)
                .append(organisationAddress)
                .append(organisation)
                .toHashCode();
    }
}
