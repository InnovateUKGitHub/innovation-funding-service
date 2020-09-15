package org.innovateuk.ifs.project.status.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ProjectState;

public class ProjectStatusResource {
    private String projectTitle;
    private Long projectNumber;
    private String formattedProjectNumber;
    private Long applicationNumber;
    private String formattedApplicationNumber;
    private Integer numberOfPartners;
    private String projectLeadOrganisationName;

    private ProjectActivityStates projectDetailsStatus;
    private ProjectActivityStates projectTeamStatus;
    private ProjectActivityStates bankDetailsStatus;
    private ProjectActivityStates financeChecksStatus;
    private ProjectActivityStates spendProfileStatus;
    private ProjectActivityStates monitoringOfficerStatus;
    private ProjectActivityStates documentsStatus;
    private ProjectActivityStates grantOfferLetterStatus;
    private ProjectActivityStates projectSetupCompleteStatus;
    private boolean grantOfferLetterSent;
    private ProjectState projectState;
    private boolean sentToIfsPa;

    public ProjectStatusResource(String projectTitle,
                                 Long projectNumber,
                                 String formattedProjectNumber,
                                 Long applicationNumber,
                                 String formattedApplicationNumber,
                                 Integer numberOfPartners,
                                 String projectLeadOrganisationName,
                                 ProjectActivityStates projectDetailsStatus,
                                 ProjectActivityStates projectTeamStatus,
                                 ProjectActivityStates bankDetailsStatus,
                                 ProjectActivityStates financeChecksStatus,
                                 ProjectActivityStates spendProfileStatus,
                                 ProjectActivityStates monitoringOfficerStatus,
                                 ProjectActivityStates documentsStatus,
                                 ProjectActivityStates grantOfferLetterStatus,
                                 ProjectActivityStates projectSetupCompleteStatus,
                                 boolean grantOfferLetterSent,
                                 ProjectState projectState,
                                 boolean sentToIfsPa) {
        this.projectTitle = projectTitle;
        this.projectNumber = projectNumber;
        this.formattedProjectNumber = formattedProjectNumber;
        this.applicationNumber = applicationNumber;
        this.formattedApplicationNumber = formattedApplicationNumber;
        this.numberOfPartners = numberOfPartners;
        this.projectLeadOrganisationName = projectLeadOrganisationName;
        this.projectDetailsStatus = projectDetailsStatus;
        this.projectTeamStatus = projectTeamStatus;
        this.bankDetailsStatus = bankDetailsStatus;
        this.financeChecksStatus = financeChecksStatus;
        this.spendProfileStatus = spendProfileStatus;
        this.monitoringOfficerStatus = monitoringOfficerStatus;
        this.documentsStatus = documentsStatus;
        this.grantOfferLetterStatus = grantOfferLetterStatus;
        this.projectSetupCompleteStatus = projectSetupCompleteStatus;
        this.grantOfferLetterSent = grantOfferLetterSent;
        this.projectState = projectState;
        this.sentToIfsPa = sentToIfsPa;
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

    public Long getProjectNumber() {
        return projectNumber;
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

    public ProjectActivityStates getProjectTeamStatus() {
        return projectTeamStatus;
    }

    public void setProjectTeamStatus(ProjectActivityStates projectTeamStatus) {
        this.projectTeamStatus = projectTeamStatus;
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

    public ProjectActivityStates getDocumentsStatus() {
        return documentsStatus;
    }

    public void setDocumentsStatus(ProjectActivityStates documentsStatus) {
        this.documentsStatus = documentsStatus;
    }

    public ProjectActivityStates getGrantOfferLetterStatus() {
        return grantOfferLetterStatus;
    }

    public void setGrantOfferLetterStatus(ProjectActivityStates grantOfferLetterStatus) {
        this.grantOfferLetterStatus = grantOfferLetterStatus;
    }

    public ProjectActivityStates getProjectSetupCompleteStatus() {
        return projectSetupCompleteStatus;
    }

    public void setProjectSetupCompleteStatus(ProjectActivityStates projectSetupCompleteStatus) {
        this.projectSetupCompleteStatus = projectSetupCompleteStatus;
    }

    public boolean getGrantOfferLetterSent() { return grantOfferLetterSent; }

    public void setGrantOfferLetterSent(boolean grantOfferLetterSent) { this.grantOfferLetterSent = grantOfferLetterSent; }

    public void setProjectNumber(Long projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getFormattedProjectNumber() {
        return formattedProjectNumber;
    }

    public void setFormattedProjectNumber(String formattedProjectNumber) {
        this.formattedProjectNumber = formattedProjectNumber;
    }

    public Long getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(Long applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getFormattedApplicationNumber() {
        return formattedApplicationNumber;
    }

    public void setFormattedApplicationNumber(String formattedApplicationNumber) {
        this.formattedApplicationNumber = formattedApplicationNumber;
    }

    public ProjectState getProjectState() {
        return projectState;
    }

    public void setProjectState(ProjectState projectState) {
        this.projectState = projectState;
    }

    public boolean isSentToIfsPa() {
        return sentToIfsPa;
    }

    public void setSentToIfsPa(boolean sentToIfsPa) {
        this.sentToIfsPa = sentToIfsPa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectStatusResource that = (ProjectStatusResource) o;

        return new EqualsBuilder()
                .append(grantOfferLetterSent, that.grantOfferLetterSent)
                .append(projectTitle, that.projectTitle)
                .append(projectNumber, that.projectNumber)
                .append(formattedProjectNumber, that.formattedProjectNumber)
                .append(applicationNumber, that.applicationNumber)
                .append(formattedApplicationNumber, that.formattedApplicationNumber)
                .append(numberOfPartners, that.numberOfPartners)
                .append(projectLeadOrganisationName, that.projectLeadOrganisationName)
                .append(projectDetailsStatus, that.projectDetailsStatus)
                .append(projectTeamStatus, that.projectTeamStatus)
                .append(bankDetailsStatus, that.bankDetailsStatus)
                .append(financeChecksStatus, that.financeChecksStatus)
                .append(spendProfileStatus, that.spendProfileStatus)
                .append(monitoringOfficerStatus, that.monitoringOfficerStatus)
                .append(documentsStatus, that.documentsStatus)
                .append(grantOfferLetterStatus, that.grantOfferLetterStatus)
                .append(projectSetupCompleteStatus, that.projectSetupCompleteStatus)
                .append(projectState, that.projectState)
                .append(sentToIfsPa, that.sentToIfsPa)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectTitle)
                .append(projectNumber)
                .append(formattedProjectNumber)
                .append(applicationNumber)
                .append(formattedApplicationNumber)
                .append(numberOfPartners)
                .append(projectLeadOrganisationName)
                .append(projectDetailsStatus)
                .append(projectTeamStatus)
                .append(bankDetailsStatus)
                .append(financeChecksStatus)
                .append(spendProfileStatus)
                .append(monitoringOfficerStatus)
                .append(documentsStatus)
                .append(grantOfferLetterStatus)
                .append(projectSetupCompleteStatus)
                .append(grantOfferLetterSent)
                .append(projectState)
                .append(sentToIfsPa)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("projectTitle", projectTitle)
                .append("projectNumber", projectNumber)
                .append("formattedProjectNumber", formattedProjectNumber)
                .append("applicationNumber", applicationNumber)
                .append("formattedApplicationNumber", formattedApplicationNumber)
                .append("numberOfPartners", numberOfPartners)
                .append("projectLeadOrganisationName", projectLeadOrganisationName)
                .append("projectDetailsStatus", projectDetailsStatus)
                .append("projectTeamStatus", projectTeamStatus)
                .append("bankDetailsStatus", bankDetailsStatus)
                .append("financeChecksStatus", financeChecksStatus)
                .append("spendProfileStatus", spendProfileStatus)
                .append("monitoringOfficerStatus", monitoringOfficerStatus)
                .append("documentsStatus", documentsStatus)
                .append("grantOfferLetterStatus", grantOfferLetterStatus)
                .append("projectSetupCompleteStatus", projectSetupCompleteStatus)
                .append("grantOfferLetterSent", grantOfferLetterSent)
                .append("projectState", projectState)
                .append("sentToIfsPa", sentToIfsPa)
                .toString();
    }
}
