package com.worth.ifs.project.status.resource;

import com.worth.ifs.project.constant.ProjectActivityStates;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ProjectStatusResource {
    private String projectTitle;
    private String projectNumber;
    private Integer numberOfPartners;
    private String projectLeadOrganisationName;

    private ProjectActivityStates projectDetailsStatus;
    private ProjectActivityStates bankDetailsStatus;
    private ProjectActivityStates financeChecksStatus;
    private ProjectActivityStates spendProfileStatus;
    private ProjectActivityStates monitoringOfficerStatus;
    private ProjectActivityStates otherDocumentsStatus;
    private ProjectActivityStates grantOfferLetterStatus;

    public ProjectStatusResource(String projectTitle, String projectNumber, Integer numberOfPartners, String projectLeadOrganisationName, ProjectActivityStates projectDetailsStatus, ProjectActivityStates bankDetailsStatus, ProjectActivityStates financeChecksStatus, ProjectActivityStates spendProfileStatus, ProjectActivityStates monitoringOfficerStatus, ProjectActivityStates otherDocumentsStatus, ProjectActivityStates grantOfferLetterStatus) {
        this.projectTitle = projectTitle;
        this.projectNumber = projectNumber;
        this.numberOfPartners = numberOfPartners;
        this.projectLeadOrganisationName = projectLeadOrganisationName;
        this.projectDetailsStatus = projectDetailsStatus;
        this.bankDetailsStatus = bankDetailsStatus;
        this.financeChecksStatus = financeChecksStatus;
        this.spendProfileStatus = spendProfileStatus;
        this.monitoringOfficerStatus = monitoringOfficerStatus;
        this.otherDocumentsStatus = otherDocumentsStatus;
        this.grantOfferLetterStatus = grantOfferLetterStatus;
    }

    // Required for JSON mapping
    public ProjectStatusResource() {
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public Integer getNumberOfPartners() {
        return numberOfPartners;
    }

    public void setNumberOfPartners(Integer numberOfPartners) {
        this.numberOfPartners = numberOfPartners;
    }

    public String getProjectLeadOrganisationName() {
        return projectLeadOrganisationName;
    }

    public void setProjectLeadOrganisationName(String projectLeadOrganisationName) {
        this.projectLeadOrganisationName = projectLeadOrganisationName;
    }

    public ProjectActivityStates getProjectDetailsStatus() {
        return projectDetailsStatus;
    }

    public void setProjectDetailsStatus(ProjectActivityStates projectDetailsStatus) {
        this.projectDetailsStatus = projectDetailsStatus;
    }

    public ProjectActivityStates getBankDetailsStatus() {
        return bankDetailsStatus;
    }

    public void setBankDetailsStatus(ProjectActivityStates bankDetailsStatus) {
        this.bankDetailsStatus = bankDetailsStatus;
    }

    public ProjectActivityStates getFinanceChecksStatus() {
        return financeChecksStatus;
    }

    public void setFinanceChecksStatus(ProjectActivityStates financeChecksStatus) {
        this.financeChecksStatus = financeChecksStatus;
    }

    public ProjectActivityStates getSpendProfileStatus() {
        return spendProfileStatus;
    }

    public void setSpendProfileStatus(ProjectActivityStates spendProfileStatus) {
        this.spendProfileStatus = spendProfileStatus;
    }

    public ProjectActivityStates getMonitoringOfficerStatus() {
        return monitoringOfficerStatus;
    }

    public void setMonitoringOfficerStatus(ProjectActivityStates monitoringOfficerStatus) {
        this.monitoringOfficerStatus = monitoringOfficerStatus;
    }

    public ProjectActivityStates getOtherDocumentsStatus() {
        return otherDocumentsStatus;
    }

    public void setOtherDocumentsStatus(ProjectActivityStates otherDocumentsStatus) {
        this.otherDocumentsStatus = otherDocumentsStatus;
    }

    public ProjectActivityStates getGrantOfferLetterStatus() {
        return grantOfferLetterStatus;
    }

    public void setGrantOfferLetterStatus(ProjectActivityStates grantOfferLetterStatus) {
        this.grantOfferLetterStatus = grantOfferLetterStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectStatusResource that = (ProjectStatusResource) o;

        return new EqualsBuilder()
                .append(projectTitle, that.projectTitle)
                .append(projectNumber, that.projectNumber)
                .append(numberOfPartners, that.numberOfPartners)
                .append(projectLeadOrganisationName, that.projectLeadOrganisationName)
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
                .append(projectTitle)
                .append(projectNumber)
                .append(numberOfPartners)
                .append(projectLeadOrganisationName)
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
                .append("projectTitle", projectTitle)
                .append("projectNumber", projectNumber)
                .append("numberOfPartners", numberOfPartners)
                .append("projectLeadOrganisationName", projectLeadOrganisationName)
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
