package com.worth.ifs.bankdetails.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.organisation.resource.OrganisationAddressResource;
import com.worth.ifs.user.resource.OrganisationTypeResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class BankDetailsResource {

    private Long id;

    @NotBlank (message = "Sort code is mandatory")
    @Pattern(regexp="\\d{6}", message = "Please enter a valid 6 digit sort code")
    private String sortCode;

    @NotBlank (message = "Account number is mandatory")
    @Pattern(regexp="\\d{8}", message = "Please enter a valid 8 digit account number")
    private String accountNumber;

    @NotNull(message = "Project id is mandatory")
    private Long project;

    @NotNull(message = "Organisation Address is mandatory")
    private OrganisationAddressResource organisationAddress;

    @NotNull(message = "Organisation id is mandatory")
    private Long organisation;

    private OrganisationTypeResource organisationType;

    private String companyName;

    private String registrationNumber;

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

    public OrganisationTypeResource getOrganisationType() {
        return organisationType;
    }

    public void setOrganisationType(OrganisationTypeResource organisationType) {
        this.organisationType = organisationType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
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
                .append(organisationType, that.organisationType)
                .append(companyName, that.companyName)
                .append(registrationNumber, that.registrationNumber)
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
                .append(organisationType)
                .append(companyName)
                .append(registrationNumber)
                .toHashCode();
    }
}
