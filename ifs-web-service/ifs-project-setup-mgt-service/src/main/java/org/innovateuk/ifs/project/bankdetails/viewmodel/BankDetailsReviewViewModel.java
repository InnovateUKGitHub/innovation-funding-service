package org.innovateuk.ifs.project.bankdetails.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.project.resource.ProjectResource;

import static org.innovateuk.ifs.project.constant.ProjectConstants.EXPERIAN_AUTOMATIC_APPROVAL_THRESHOLD_ADDRESS;
import static org.innovateuk.ifs.project.constant.ProjectConstants.EXPERIAN_AUTOMATIC_APPROVAL_THRESHOLD_COMPANY_NAME;

public class BankDetailsReviewViewModel {
    private long projectId;
    private long applicationId;
    private long competitionId;
    private String projectName;
    private String financeContactName;
    private String financeContactEmail;
    private String financeContactPhoneNumber;
    private long organisationId;
    private String organisationName;
    private String registrationNumber;
    private String bankAccountNumber;
    private String sortCode;
    private String organisationAddress;
    private Boolean verified;
    private Short companyNameScore;
    private Boolean registrationNumberMatched;
    private Short addressScore;
    private Boolean approved;
    private Boolean approvedManually;
    private boolean projectActive;
    private boolean collaborativeProject;

    public BankDetailsReviewViewModel(ProjectResource project,
                                      String financeContactName,
                                      String financeContactEmail,
                                      String financeContactPhoneNumber,
                                      long organisationId,
                                      String organisationName,
                                      String registrationNumber,
                                      String bankAccountNumber,
                                      String sortCode,
                                      String organisationAddress,
                                      Boolean verified,
                                      Short companyNameScore,
                                      Boolean registrationNumberMatched,
                                      Short addressScore,
                                      Boolean approved,
                                      Boolean approvedManually) {
        this.projectId = project.getId();
        this.applicationId = project.getApplication();
        this.projectName = project.getName();
        this.financeContactName = financeContactName;
        this.financeContactEmail = financeContactEmail;
        this.financeContactPhoneNumber = financeContactPhoneNumber;
        this.organisationId = organisationId;
        this.organisationName = organisationName;
        this.registrationNumber = registrationNumber;
        this.bankAccountNumber = bankAccountNumber;
        this.sortCode = sortCode;
        this.organisationAddress = organisationAddress;
        this.verified = verified;
        this.companyNameScore = companyNameScore;
        this.registrationNumberMatched = registrationNumberMatched;
        this.addressScore = addressScore;
        this.approved = approved;
        this.approvedManually = approvedManually;
        this.projectActive = project.getProjectState().isActive();
        this.collaborativeProject = project.isCollaborativeProject();
        this.competitionId = project.getCompetition();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getFinanceContactName() {
        return financeContactName;
    }

    public void setFinanceContactName(String financeContactName) {
        this.financeContactName = financeContactName;
    }

    public String getFinanceContactEmail() {
        return financeContactEmail;
    }

    public void setFinanceContactEmail(String financeContactEmail) {
        this.financeContactEmail = financeContactEmail;
    }

    public String getFinanceContactPhoneNumber() {
        return financeContactPhoneNumber;
    }

    public void setFinanceContactPhoneNumber(String financeContactPhoneNumber) {
        this.financeContactPhoneNumber = financeContactPhoneNumber;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getOrganisationAddress() {
        return organisationAddress;
    }

    public void setOrganisationAddress(String organisationAddress) {
        this.organisationAddress = organisationAddress;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Short getCompanyNameScore() {
        return companyNameScore;
    }

    public void setCompanyNameScore(Short companyNameScore) {
        this.companyNameScore = companyNameScore;
    }

    public Boolean getRegistrationNumberMatched() {
        return registrationNumberMatched;
    }

    public void setRegistrationNumberMatched(Boolean registrationNumberMatched) {
        this.registrationNumberMatched = registrationNumberMatched;
    }

    public Short getAddressScore() {
        return addressScore;
    }

    public void setAddressScore(Short addressScore) {
        this.addressScore = addressScore;
    }

    public Boolean getApprovedManually() {
        return approvedManually;
    }

    public void setApprovedManually(Boolean approvedManually) {
        this.approvedManually = approvedManually;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public boolean getCompanyNameVerified(){
        return companyNameScore > EXPERIAN_AUTOMATIC_APPROVAL_THRESHOLD_COMPANY_NAME;
    }

    public boolean getAddressScoreVerified(){
        return addressScore > EXPERIAN_AUTOMATIC_APPROVAL_THRESHOLD_ADDRESS;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public boolean isProjectActive() {
        return projectActive;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isEditable() {
        return projectActive && !approved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BankDetailsReviewViewModel that = (BankDetailsReviewViewModel) o;

        return new EqualsBuilder()
                .append(projectId, that.projectId)
                .append(applicationId, that.applicationId)
                .append(projectName, that.projectName)
                .append(financeContactName, that.financeContactName)
                .append(financeContactEmail, that.financeContactEmail)
                .append(financeContactPhoneNumber, that.financeContactPhoneNumber)
                .append(organisationId, that.organisationId)
                .append(organisationName, that.organisationName)
                .append(registrationNumber, that.registrationNumber)
                .append(bankAccountNumber, that.bankAccountNumber)
                .append(sortCode, that.sortCode)
                .append(organisationAddress, that.organisationAddress)
                .append(verified, that.verified)
                .append(companyNameScore, that.companyNameScore)
                .append(registrationNumberMatched, that.registrationNumberMatched)
                .append(addressScore, that.addressScore)
                .append(approved, that.approved)
                .append(approvedManually, that.approvedManually)
                .append(projectActive, that.projectActive)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(applicationId)
                .append(projectName)
                .append(financeContactName)
                .append(financeContactEmail)
                .append(financeContactPhoneNumber)
                .append(organisationId)
                .append(organisationName)
                .append(registrationNumber)
                .append(bankAccountNumber)
                .append(sortCode)
                .append(organisationAddress)
                .append(verified)
                .append(companyNameScore)
                .append(registrationNumberMatched)
                .append(addressScore)
                .append(approved)
                .append(approvedManually)
                .append(projectActive)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("projectId", projectId)
                .append("applicationId", applicationId)
                .append("projectName", projectName)
                .append("financeContactName", financeContactName)
                .append("financeContactEmail", financeContactEmail)
                .append("financeContactPhoneNumber", financeContactPhoneNumber)
                .append("organisationId", organisationId)
                .append("organisationName", organisationName)
                .append("registrationNumber", registrationNumber)
                .append("bankAccountNumber", bankAccountNumber)
                .append("sortCode", sortCode)
                .append("organisationAddress", organisationAddress)
                .append("verified", verified)
                .append("companyNameScore", companyNameScore)
                .append("registrationNumberMatched", registrationNumberMatched)
                .append("addressScore", addressScore)
                .append("approved", approved)
                .append("approvedManually", approvedManually)
                .append("projectActive", projectActive)
                .toString();
    }
}
