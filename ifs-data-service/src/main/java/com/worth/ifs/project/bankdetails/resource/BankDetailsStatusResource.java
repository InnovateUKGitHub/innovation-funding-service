package com.worth.ifs.project.bankdetails.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.project.constant.ProjectActivityStates;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.worth.ifs.application.resource.ApplicationResource.formatter;

/**
 * A resource object for returning bank details status for individual partner organisation.  Usually wrapped within ProjectBankDetailsStatusSummary.
 */
public class BankDetailsStatusResource {
    private Long organisationId;
    private String organisationName;
    private ProjectActivityStates bankDetailsStatus;

    public BankDetailsStatusResource() {
    }

    public BankDetailsStatusResource(Long organisationId, String organisationName, ProjectActivityStates bankDetailsStatus) {
        this.organisationId = organisationId;
        this.organisationName = organisationName;
        this.bankDetailsStatus = bankDetailsStatus;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    @JsonIgnore
    public String getFormattedOrganisationId(){
        return formatter.format(organisationId);
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public ProjectActivityStates getBankDetailsStatus() {
        return bankDetailsStatus;
    }

    public void setBankDetailsStatus(ProjectActivityStates bankDetailsStatus) {
        this.bankDetailsStatus = bankDetailsStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BankDetailsStatusResource that = (BankDetailsStatusResource) o;

        return new EqualsBuilder()
                .append(organisationId, that.organisationId)
                .append(organisationName, that.organisationName)
                .append(bankDetailsStatus, that.bankDetailsStatus)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationId)
                .append(organisationName)
                .append(bankDetailsStatus)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("organisationId", organisationId)
                .append("organisationName", organisationName)
                .append("bankDetailsStatus", bankDetailsStatus)
                .toString();
    }
}
