package com.worth.ifs.project.consortiumoverview.viewmodel;

public class RegularPartnerModel extends ConsortiumMemberStatusModel {

    public RegularPartnerModel(
        final String partnerName,
        final ConsortiumPartnerStatus projectDetailsStatus,
        final ConsortiumPartnerStatus bankDetailsStatus,
        final ConsortiumPartnerStatus financeChecksStatus,
        final ConsortiumPartnerStatus spendProfileStatus) {

        this.name = partnerName;
        this.projectDetailsStatus = projectDetailsStatus;
        this.bankDetailsStatus = bankDetailsStatus;
        this.financeChecksStatus = financeChecksStatus;
        this.spendProfileStatus = spendProfileStatus;

        this.monitoringOfficerStatus = ConsortiumPartnerStatus.NOT_REQUIRED;
        this.otherDocumentsStatus = ConsortiumPartnerStatus.NOT_REQUIRED;
        this.grantOfferLetterStatus = ConsortiumPartnerStatus.NOT_REQUIRED;
    }
}
