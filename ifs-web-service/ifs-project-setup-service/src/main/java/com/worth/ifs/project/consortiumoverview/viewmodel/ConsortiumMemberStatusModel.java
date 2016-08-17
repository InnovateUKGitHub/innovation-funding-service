package com.worth.ifs.project.consortiumoverview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public abstract class ConsortiumMemberStatusModel {
    String name;
    ConsortiumPartnerStatus projectDetailsStatus;
    ConsortiumPartnerStatus monitoringOfficerStatus;
    ConsortiumPartnerStatus bankDetailsStatus;
    ConsortiumPartnerStatus financeChecksStatus;
    ConsortiumPartnerStatus spendProfileStatus;
    ConsortiumPartnerStatus otherDocumentsStatus;
    ConsortiumPartnerStatus grantOfferLetterStatus;

    public String getName() {
        return name;
    }

    public ConsortiumPartnerStatus getProjectDetailsStatus() {
        return projectDetailsStatus;
    }

    public ConsortiumPartnerStatus getMonitoringOfficerStatus() {
        return monitoringOfficerStatus;
    }

    public ConsortiumPartnerStatus getBankDetailsStatus() {
        return bankDetailsStatus;
    }

    public ConsortiumPartnerStatus getFinanceChecksStatus() {
        return financeChecksStatus;
    }

    public ConsortiumPartnerStatus getSpendProfileStatus() {
        return spendProfileStatus;
    }

    public ConsortiumPartnerStatus getOtherDocumentsStatus() {
        return otherDocumentsStatus;
    }

    public ConsortiumPartnerStatus getGrantOfferLetterStatus() {
        return grantOfferLetterStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        ConsortiumMemberStatusModel rhs = (ConsortiumMemberStatusModel) obj;
        return new EqualsBuilder()
            .append(this.name, rhs.name)
            .append(this.projectDetailsStatus, rhs.projectDetailsStatus)
            .append(this.monitoringOfficerStatus, rhs.monitoringOfficerStatus)
            .append(this.bankDetailsStatus, rhs.bankDetailsStatus)
            .append(this.financeChecksStatus, rhs.financeChecksStatus)
            .append(this.spendProfileStatus, rhs.spendProfileStatus)
            .append(this.otherDocumentsStatus, rhs.otherDocumentsStatus)
            .append(this.grantOfferLetterStatus, rhs.grantOfferLetterStatus)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(name)
            .append(projectDetailsStatus)
            .append(monitoringOfficerStatus)
            .append(bankDetailsStatus)
            .append(financeChecksStatus)
            .append(spendProfileStatus)
            .append(otherDocumentsStatus)
            .append(grantOfferLetterStatus)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("\n\tname", name)
            .append("\n\tprojectDetailsStatus", projectDetailsStatus)
            .append("\n\tmonitoringOfficerStatus", monitoringOfficerStatus)
            .append("\n\tbankDetailsStatus", bankDetailsStatus)
            .append("\n\tfinanceChecksStatus", financeChecksStatus)
            .append("\n\tspendProfileStatus", spendProfileStatus)
            .append("\n\totherDocumentsStatus", otherDocumentsStatus)
            .append("\n\tgrantOfferLetterStatus", grantOfferLetterStatus)
            .append("\n","")
            .toString();
    }
}