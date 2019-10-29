package org.innovateuk.ifs.project.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;

/**
 * Used for returning status of each partner (except lead, for which there is a more specific class with constructor)
 * There is a constructor here which is used by subclass but is package local.
 */
public class ProjectPartnerStatusResource {

    private Long organisationId;
    private String name;
    private OrganisationTypeEnum organisationType;

    private ProjectActivityStates projectDetailsStatus;
    private ProjectActivityStates projectTeamStatus;
    private ProjectActivityStates bankDetailsStatus;
    private ProjectActivityStates financeChecksStatus;
    private ProjectActivityStates spendProfileStatus;
    private ProjectActivityStates projectSetupCompleteStatus;

    private ProjectActivityStates financeContactStatus = COMPLETE;
    private ProjectActivityStates partnerProjectLocationStatus;
    private ProjectActivityStates companiesHouseStatus = COMPLETE;

    /* Following properties are only applicable to lead partner */
    private ProjectActivityStates monitoringOfficerStatus;
    private ProjectActivityStates documentsStatus;
    private ProjectActivityStates grantOfferLetterStatus;

    private boolean isGrantOfferLetterSent = false;
    private boolean isLead = false;
    private boolean pendingPartner = false;


    //Required for Json Mapping.
    ProjectPartnerStatusResource() {}

    public ProjectPartnerStatusResource(Long organisationId,
                                        String name,
                                        OrganisationTypeEnum organisationType,
                                        ProjectActivityStates projectDetailsStatus,
                                        ProjectActivityStates projectTeamStatus,
                                        ProjectActivityStates monitoringOfficerStatus,
                                        ProjectActivityStates bankDetailsStatus,
                                        ProjectActivityStates financeChecksStatus,
                                        ProjectActivityStates spendProfileStatus,
                                        ProjectActivityStates documentsStatus,
                                        ProjectActivityStates grantOfferLetterStatus,
                                        ProjectActivityStates financeContactStatus,
                                        ProjectActivityStates partnerProjectLocationStatus,
                                        ProjectActivityStates projectSetupCompleteStatus,
                                        Boolean isGrantOfferLetterSent, Boolean isLead, boolean pendingPartner) {
        this.organisationId = organisationId;
        this.name = name;
        this.organisationType = organisationType;
        this.projectDetailsStatus = projectDetailsStatus;
        this.projectTeamStatus = projectTeamStatus;
        this.monitoringOfficerStatus = monitoringOfficerStatus;
        this.bankDetailsStatus = bankDetailsStatus;
        this.financeChecksStatus = financeChecksStatus;
        this.spendProfileStatus = spendProfileStatus;
        this.documentsStatus = documentsStatus;
        this.grantOfferLetterStatus = grantOfferLetterStatus;
        this.financeContactStatus = financeContactStatus;
        this.partnerProjectLocationStatus = partnerProjectLocationStatus;
        this.isGrantOfferLetterSent = isGrantOfferLetterSent;
        this.projectSetupCompleteStatus = projectSetupCompleteStatus;
        this.isLead = isLead;
        this.pendingPartner = pendingPartner;
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

    public ProjectActivityStates getProjectTeamStatus() {
        return projectTeamStatus;
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

    public ProjectActivityStates getDocumentsStatus() {
        return documentsStatus;
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

    public void setProjectTeamStatus(ProjectActivityStates projectTeamStatus) {
        this.projectTeamStatus = projectTeamStatus;
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

    public void setDocumentsStatus(ProjectActivityStates documentsStatus) {
        this.documentsStatus = documentsStatus;
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

    public ProjectActivityStates getPartnerProjectLocationStatus() {
        return partnerProjectLocationStatus;
    }

    public void setPartnerProjectLocationStatus(ProjectActivityStates partnerProjectLocationStatus) {
        this.partnerProjectLocationStatus = partnerProjectLocationStatus;
    }

    public Boolean isGrantOfferLetterSent() { return isGrantOfferLetterSent; }

    public void setGrantOfferLetterSent(Boolean isGrantOfferLetterSent) { this.isGrantOfferLetterSent = isGrantOfferLetterSent; }

    public Boolean isLead() {
        return isLead;
    }

    public void setLead(Boolean lead) {
        isLead = lead;
    }

    public ProjectActivityStates getProjectSetupCompleteStatus() {
        return projectSetupCompleteStatus;
    }

    public void setProjectSetupCompleteStatus(ProjectActivityStates projectSetupCompleteStatus) {
        this.projectSetupCompleteStatus = projectSetupCompleteStatus;
    }

    public boolean isPendingPartner() {
        return pendingPartner;
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
                .append(projectTeamStatus, that.projectTeamStatus)
                .append(bankDetailsStatus, that.bankDetailsStatus)
                .append(financeChecksStatus, that.financeChecksStatus)
                .append(spendProfileStatus, that.spendProfileStatus)
                .append(monitoringOfficerStatus, that.monitoringOfficerStatus)
                .append(documentsStatus, that.documentsStatus)
                .append(grantOfferLetterStatus, that.grantOfferLetterStatus)
                .append(isGrantOfferLetterSent, that.isGrantOfferLetterSent)
                .append(financeContactStatus, that.financeContactStatus)
                .append(partnerProjectLocationStatus, that.partnerProjectLocationStatus)
                .append(projectSetupCompleteStatus, that.projectSetupCompleteStatus)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(organisationType)
                .append(projectDetailsStatus)
                .append(projectTeamStatus)
                .append(bankDetailsStatus)
                .append(financeChecksStatus)
                .append(spendProfileStatus)
                .append(monitoringOfficerStatus)
                .append(documentsStatus)
                .append(grantOfferLetterStatus)
                .append(isGrantOfferLetterSent)
                .append(financeContactStatus)
                .append(partnerProjectLocationStatus)
                .append(projectSetupCompleteStatus)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("organisationType", organisationType)
                .append("projectDetailsStatus", projectDetailsStatus)
                .append("projectTeamStatus", projectTeamStatus)
                .append("bankDetailsStatus", bankDetailsStatus)
                .append("financeChecksStatus", financeChecksStatus)
                .append("spendProfileStatus", spendProfileStatus)
                .append("monitoringOfficerStatus", monitoringOfficerStatus)
                .append("documentsStatus", documentsStatus)
                .append("grantOfferLetterStatus", grantOfferLetterStatus)
                .append("isGrantOfferLetterSent", isGrantOfferLetterSent)
                .append("financeContactStatus", financeContactStatus)
                .append("partnerProjectLocationStatus", partnerProjectLocationStatus)
                .append("projectSetupCompleteStatus", projectSetupCompleteStatus)
                .toString();
    }
}
