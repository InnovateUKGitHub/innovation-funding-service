package org.innovateuk.ifs.project.consortiumoverview.viewmodel;

import org.innovateuk.ifs.commons.OtherDocsWindDown;

import static org.innovateuk.ifs.project.consortiumoverview.viewmodel.ConsortiumPartnerStatus.NOT_REQUIRED;

public class RegularPartnerModel extends ConsortiumMemberStatusModel {

    @OtherDocsWindDown(additionalComments = "Remove refrences from other documents to base class")
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

        this.monitoringOfficerStatus = NOT_REQUIRED;
        this.otherDocumentsStatus = NOT_REQUIRED;
        this.grantOfferLetterStatus = NOT_REQUIRED;
    }
}
