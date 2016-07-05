package com.worth.ifs.bankdetails.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class BankDetailsResource {

    private Long id;

    @NotBlank
    @Pattern(regexp="\\d{6}")
    private String sortCode;

    @NotBlank
    @Pattern(regexp="\\d{8}")
    private String accountNumber;

    @NotNull
    private Long project;

    @NotNull
    private OrganisationAddressResource organisationAddress;

    @NotNull
    private Long organisation;

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

    public Long getProject() {
        return project;
    }

    public void setProject(Long project) {
        this.project = project;
    }

    public OrganisationAddressResource getOrganisationAddress() {
        return organisationAddress;
    }

    public void setOrganisationAddress(OrganisationAddressResource organisationAddressResource) {
        this.organisationAddress = organisationAddressResource;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    @JsonIgnore
    public boolean isApproved(){
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BankDetailsResource that = (BankDetailsResource) o;

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
