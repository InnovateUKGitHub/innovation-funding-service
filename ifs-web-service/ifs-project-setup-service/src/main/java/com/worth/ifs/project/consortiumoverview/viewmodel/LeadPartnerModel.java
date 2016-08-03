package com.worth.ifs.project.consortiumoverview.viewmodel;

public class LeadPartnerModel extends ConsortiumMemberStatusModel {

    public LeadPartnerModel(
        final String partnerName,
        final ConsortiumPartnerStatus projectDetailsStatus,
        final ConsortiumPartnerStatus monitoringOfficerStatus,
        final ConsortiumPartnerStatus bankDetailsStatus,
        final ConsortiumPartnerStatus financeChecksStatus,
        final ConsortiumPartnerStatus spendProfileStatus,
        final ConsortiumPartnerStatus otherDocumentsStatus,
        final ConsortiumPartnerStatus grantOfferLetterStatus) {

        this.name = partnerName;
        this.projectDetailsStatus = projectDetailsStatus;
        this.monitoringOfficerStatus = monitoringOfficerStatus;
        this.bankDetailsStatus = bankDetailsStatus;
        this.financeChecksStatus = financeChecksStatus;
        this.spendProfileStatus = spendProfileStatus;
        this.otherDocumentsStatus = otherDocumentsStatus;
        this.grantOfferLetterStatus = grantOfferLetterStatus;
    }
}

