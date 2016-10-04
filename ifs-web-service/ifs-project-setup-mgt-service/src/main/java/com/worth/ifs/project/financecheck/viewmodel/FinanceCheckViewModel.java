package com.worth.ifs.project.financecheck.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;

public class FinanceCheckViewModel {
    private String formattedCompetitionNumber;
    private String competitionName;
    private String organisationName;
    private boolean isLeadPartner;
    private String financeContactName;
    private String financeContactEmail;
    private boolean isResearch;
    private boolean isApproved;
    private String approverName;
    private LocalDate approvalDate;

    public FinanceCheckViewModel() {
    }

    public FinanceCheckViewModel(String formattedCompetitionNumber, String competitionName, String organisationName, boolean isLeadPartner, String financeContactName, String financeContactEmail, boolean isResearch, boolean isApproved, String approverName, LocalDate approvalDate) {
        this.formattedCompetitionNumber = formattedCompetitionNumber;
        this.competitionName = competitionName;
        this.organisationName = organisationName;
        this.isLeadPartner = isLeadPartner;
        this.financeContactName = financeContactName;
        this.financeContactEmail = financeContactEmail;
        this.isResearch = isResearch;
        this.isApproved = isApproved;
        this.approverName = approverName;
        this.approvalDate = approvalDate;
    }

    public FinanceCheckViewModel(String formattedCompetitionNumber, String competitionName, String organisationName, boolean isLeadPartner, String financeContactName, String financeContactEmail, boolean isResearch) {
        this(formattedCompetitionNumber, competitionName, organisationName, isLeadPartner, financeContactName, financeContactEmail, isResearch, false, null, null);
    }

    public FinanceCheckViewModel(String formattedCompetitionNumber, String competitionName, String organisationName, boolean isLeadPartner, boolean isResearch) {
        this(formattedCompetitionNumber, competitionName, organisationName, isLeadPartner, null, null, isResearch, false, null, null);
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

    public boolean isResearch() {
        return isResearch;
    }

    public void setResearch(boolean research) {
        this.isResearch = research;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getFormattedCompetitionNumber() {
        return formattedCompetitionNumber;
    }

    public void setFormattedCompetitionNumber(String formattedCompetitionNumber) {
        this.formattedCompetitionNumber = formattedCompetitionNumber;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public boolean isLeadPartner() {
        return isLeadPartner;
    }

    public void setLeadPartner(boolean leadPartner) {
        isLeadPartner = leadPartner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FinanceCheckViewModel that = (FinanceCheckViewModel) o;

        return new EqualsBuilder()
                .append(isLeadPartner, that.isLeadPartner)
                .append(isResearch, that.isResearch)
                .append(isApproved, that.isApproved)
                .append(formattedCompetitionNumber, that.formattedCompetitionNumber)
                .append(competitionName, that.competitionName)
                .append(organisationName, that.organisationName)
                .append(financeContactName, that.financeContactName)
                .append(financeContactEmail, that.financeContactEmail)
                .append(approverName, that.approverName)
                .append(approvalDate, that.approvalDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(formattedCompetitionNumber)
                .append(competitionName)
                .append(organisationName)
                .append(isLeadPartner)
                .append(financeContactName)
                .append(financeContactEmail)
                .append(isResearch)
                .append(isApproved)
                .append(approverName)
                .append(approvalDate)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("formattedCompetitionNumber", formattedCompetitionNumber)
                .append("competitionName", competitionName)
                .append("organisationName", organisationName)
                .append("isLeadPartner", isLeadPartner)
                .append("financeContactName", financeContactName)
                .append("financeContactEmail", financeContactEmail)
                .append("isResearch", isResearch)
                .append("isApproved", isApproved)
                .append("approverName", approverName)
                .append("approvalDate", approvalDate)
                .toString();
    }
}
