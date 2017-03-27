package org.innovateuk.ifs.project.financecheck.viewmodel;

import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;

public class FinanceCheckViewModel {

    private Long competitionId;
    private String competitionName;
    private Long projectId;
    private Long organisationId;
    private String organisationName;
    private boolean isLeadPartner;
    private String financeContactName;
    private String financeContactEmail;
    private boolean isUsingJesFinances;
    private boolean financeChecksApproved;
    private String approverName;
    private LocalDate approvalDate;
    private FileDetailsViewModel jesFileDetails;

    public FinanceCheckViewModel(Long competitionId, String competitionName, String organisationName, boolean isLeadPartner,
                                 Long projectId, Long organisationId, String financeContactName, String financeContactEmail, boolean isUsingJesFinances,
                                 boolean financeChecksApproved, String approverName, LocalDate approvalDate, FileDetailsViewModel jesFileDetails) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.organisationName = organisationName;
        this.isLeadPartner = isLeadPartner;
        this.projectId = projectId;
        this.organisationId = organisationId;
        this.financeContactName = financeContactName;
        this.financeContactEmail = financeContactEmail;
        this.isUsingJesFinances = isUsingJesFinances;
        this.financeChecksApproved = financeChecksApproved;
        this.approverName = approverName;
        this.approvalDate = approvalDate;
        this.jesFileDetails = jesFileDetails;
    }

    public FinanceCheckViewModel(Long competitionId, String competitionName, String organisationName, boolean isLeadPartner,
                                 Long projectId, Long organisationId, boolean isUsingJesFinances,
                                 boolean financeChecksApproved, String approverName, LocalDate approvalDate, FileDetailsViewModel jesFileDetails) {
        this(competitionId, competitionName, organisationName, isLeadPartner,
                projectId, organisationId, null, null, isUsingJesFinances,
                financeChecksApproved, approverName, approvalDate, jesFileDetails);
    }

    public String getFinanceContactName() {
        return financeContactName;
    }

    public String getFinanceContactEmail() {
        return financeContactEmail;
    }

    public boolean isUsingJesFinances() {
        return isUsingJesFinances;
    }

    public boolean isFinanceChecksApproved() {
        return financeChecksApproved;
    }

    public String getApproverName() {
        return approverName;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public boolean isShowFinanceChecksApprovedMessage() {
        return financeChecksApproved;
    }

    public boolean isShowApproveButton() {
        return !financeChecksApproved;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public boolean isLeadPartner() {
        return isLeadPartner;
    }

    public FileDetailsViewModel getJesFileDetails() {
        return jesFileDetails;
    }

    public void setFinanceContactName(String financeContactName) {
        this.financeContactName = financeContactName;
    }

    public void setFinanceContactEmail(String financeContactEmail) {
        this.financeContactEmail = financeContactEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FinanceCheckViewModel that = (FinanceCheckViewModel) o;

        return new EqualsBuilder()
                .append(isLeadPartner, that.isLeadPartner)
                .append(isUsingJesFinances, that.isUsingJesFinances)
                .append(financeChecksApproved, that.financeChecksApproved)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(projectId, that.projectId)
                .append(organisationId, that.organisationId)
                .append(organisationName, that.organisationName)
                .append(financeContactName, that.financeContactName)
                .append(financeContactEmail, that.financeContactEmail)
                .append(approverName, that.approverName)
                .append(approvalDate, that.approvalDate)
                .append(jesFileDetails, that.jesFileDetails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(competitionName)
                .append(projectId)
                .append(organisationId)
                .append(organisationName)
                .append(isLeadPartner)
                .append(financeContactName)
                .append(financeContactEmail)
                .append(isUsingJesFinances)
                .append(financeChecksApproved)
                .append(approverName)
                .append(approvalDate)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("competitionId", competitionId)
                .append("competitionName", competitionName)
                .append("projectId", projectId)
                .append("organisationId", organisationId)
                .append("organisationName", organisationName)
                .append("isLeadPartner", isLeadPartner)
                .append("financeContactName", financeContactName)
                .append("financeContactEmail", financeContactEmail)
                .append("isUsingJesFinances", isUsingJesFinances)
                .append("financeChecksApproved", financeChecksApproved)
                .append("approverName", approverName)
                .append("approvalDate", approvalDate)
                .append("jesFileDetails", jesFileDetails)
                .toString();
    }
}
