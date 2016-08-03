package com.worth.ifs.project.consortiumoverview.viewmodel;

abstract class ConsortiumMemberStatusModel {
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
}