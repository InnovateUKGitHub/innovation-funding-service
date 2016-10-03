package com.worth.ifs.project.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import static com.worth.ifs.project.constant.ProjectActivityStates.COMPLETE;

/**
 * Used for returning status of each partner (except lead, for which there is a more specific class with constructor)
 * There is a constructor here which is used by subclass but is package local.
 */

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({
        @JsonSubTypes.Type(value=ProjectLeadStatusResource.class, name="projectLeadStatusResource")
})
public class ProjectPartnerStatusResource {

    private Long organisationId;
    private String name;
    private OrganisationTypeEnum organisationType;

    private ProjectActivityStates projectDetailsStatus;
    private ProjectActivityStates bankDetailsStatus;
    private ProjectActivityStates financeChecksStatus;
    private ProjectActivityStates spendProfileStatus;

    private ProjectActivityStates financeContactStatus = COMPLETE;

    // TODO DW - INFUND-4915 - blocked until Companies House Project Setup work tackled
    private ProjectActivityStates companiesHouseStatus = COMPLETE;

    /* Following properties are only applicable to lead partner */
    private ProjectActivityStates monitoringOfficerStatus;
    private ProjectActivityStates otherDocumentsStatus;
    private ProjectActivityStates grantOfferLetterStatus;


    //Required for Json Mapping.
    ProjectPartnerStatusResource() {}

    public ProjectPartnerStatusResource(Long organisationId, String name, OrganisationTypeEnum organisationType, ProjectActivityStates projectDetailsStatus, ProjectActivityStates monitoringOfficerStatus, ProjectActivityStates bankDetailsStatus, ProjectActivityStates financeChecksStatus, ProjectActivityStates spendProfileStatus, ProjectActivityStates otherDocumentsStatus, ProjectActivityStates grantOfferLetterStatus, ProjectActivityStates financeContactStatus) {
        this.organisationId = organisationId;
        this.name = name;
        this.organisationType = organisationType;
        this.projectDetailsStatus = projectDetailsStatus;
        this.monitoringOfficerStatus = monitoringOfficerStatus;
        this.bankDetailsStatus = bankDetailsStatus;
        this.financeChecksStatus = financeChecksStatus;
        this.spendProfileStatus = spendProfileStatus;
        this.otherDocumentsStatus = otherDocumentsStatus;
        this.grantOfferLetterStatus = grantOfferLetterStatus;
        this.financeContactStatus = financeContactStatus;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String getName() {
        return name;
    }

    public OrganisationTypeEnum getOrganisationType() {
        return organisationType;
    }

    public ProjectActivityStates getProjectDetailsStatus() {
        return projectDetailsStatus;
    }

    public ProjectActivityStates getBankDetailsStatus() {
        return bankDetailsStatus;
    }

    public ProjectActivityStates getFinanceChecksStatus() {
        return financeChecksStatus;
    }

    public ProjectActivityStates getSpendProfileStatus() {
        return spendProfileStatus;
    }

    public ProjectActivityStates getMonitoringOfficerStatus() {
        return monitoringOfficerStatus;
    }

    public ProjectActivityStates getOtherDocumentsStatus() {
        return otherDocumentsStatus;
    }

    public ProjectActivityStates getGrantOfferLetterStatus() {
        return grantOfferLetterStatus;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrganisationType(OrganisationTypeEnum organisationType) {
        this.organisationType = organisationType;
    }

    public void setProjectDetailsStatus(ProjectActivityStates projectDetailsStatus) {
        this.projectDetailsStatus = projectDetailsStatus;
    }

    public void setBankDetailsStatus(ProjectActivityStates bankDetailsStatus) {
        this.bankDetailsStatus = bankDetailsStatus;
    }

    public void setFinanceChecksStatus(ProjectActivityStates financeChecksStatus) {
        this.financeChecksStatus = financeChecksStatus;
    }

    public void setSpendProfileStatus(ProjectActivityStates spendProfileStatus) {
        this.spendProfileStatus = spendProfileStatus;
    }

    public void setMonitoringOfficerStatus(ProjectActivityStates monitoringOfficerStatus) {
        this.monitoringOfficerStatus = monitoringOfficerStatus;
    }

    public void setOtherDocumentsStatus(ProjectActivityStates otherDocumentsStatus) {
        this.otherDocumentsStatus = otherDocumentsStatus;
    }

    public void setGrantOfferLetterStatus(ProjectActivityStates grantOfferLetterStatus) {
        this.grantOfferLetterStatus = grantOfferLetterStatus;
    }

    public ProjectActivityStates getCompaniesHouseStatus() {
        return companiesHouseStatus;
    }

    public void setCompaniesHouseStatus(ProjectActivityStates companiesHouseStatus) {
        this.companiesHouseStatus = companiesHouseStatus;
    }

    public ProjectActivityStates getFinanceContactStatus() {
        return financeContactStatus;
    }

    public void setFinanceContactStatus(ProjectActivityStates financeContactStatus) {
        this.financeContactStatus = financeContactStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectPartnerStatusResource that = (ProjectPartnerStatusResource) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(organisationType, that.organisationType)
                .append(projectDetailsStatus, that.projectDetailsStatus)
                .append(bankDetailsStatus, that.bankDetailsStatus)
                .append(financeChecksStatus, that.financeChecksStatus)
                .append(spendProfileStatus, that.spendProfileStatus)
                .append(monitoringOfficerStatus, that.monitoringOfficerStatus)
                .append(otherDocumentsStatus, that.otherDocumentsStatus)
                .append(grantOfferLetterStatus, that.grantOfferLetterStatus)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(organisationType)
                .append(projectDetailsStatus)
                .append(bankDetailsStatus)
                .append(financeChecksStatus)
                .append(spendProfileStatus)
                .append(monitoringOfficerStatus)
                .append(otherDocumentsStatus)
                .append(grantOfferLetterStatus)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("organisationType", organisationType)
                .append("projectDetailsStatus", projectDetailsStatus)
                .append("bankDetailsStatus", bankDetailsStatus)
                .append("financeChecksStatus", financeChecksStatus)
                .append("spendProfileStatus", spendProfileStatus)
                .append("monitoringOfficerStatus", monitoringOfficerStatus)
                .append("otherDocumentsStatus", otherDocumentsStatus)
                .append("grantOfferLetterStatus", grantOfferLetterStatus)
                .toString();
    }
}
