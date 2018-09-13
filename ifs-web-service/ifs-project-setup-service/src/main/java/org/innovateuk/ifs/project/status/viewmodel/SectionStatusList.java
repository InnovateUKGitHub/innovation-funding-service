package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.sections.SectionStatus;

import static org.innovateuk.ifs.sections.SectionStatus.TICK;

/**
 * A convenient container for multiple Project Setup sections' current statuses
 */
public class SectionStatusList {

    private SectionStatus projectDetailsStatus;
    private SectionStatus monitoringOfficerStatus;
    private SectionStatus bankDetailsStatus;
    private SectionStatus financeChecksStatus;
    private SectionStatus spendProfileStatus;
    private SectionStatus otherDocumentsStatus;
    private SectionStatus grantOfferLetterStatus;

    public SectionStatusList(SectionStatus projectDetailsStatus, SectionStatus monitoringOfficerStatus, SectionStatus bankDetailsStatus, SectionStatus financeChecksStatus, SectionStatus spendProfileStatus, SectionStatus otherDocumentsStatus, SectionStatus grantOfferLetterStatus) {
        this.projectDetailsStatus = projectDetailsStatus;
        this.monitoringOfficerStatus = monitoringOfficerStatus;
        this.bankDetailsStatus = bankDetailsStatus;
        this.financeChecksStatus = financeChecksStatus;
        this.spendProfileStatus = spendProfileStatus;
        this.otherDocumentsStatus = otherDocumentsStatus;
        this.grantOfferLetterStatus = grantOfferLetterStatus;
    }

    public SectionStatus getProjectDetailsStatus() {
        return projectDetailsStatus;
    }

    public SectionStatus getMonitoringOfficerStatus() {
        return monitoringOfficerStatus;
    }

    public SectionStatus getBankDetailsStatus() {
        return bankDetailsStatus;
    }

    public SectionStatus getFinanceChecksStatus() {
        return financeChecksStatus;
    }

    public SectionStatus getSpendProfileStatus() {
        return spendProfileStatus;
    }

    public SectionStatus getOtherDocumentsStatus() {
        return otherDocumentsStatus;
    }

    public SectionStatus getGrantOfferLetterStatus() {
        return grantOfferLetterStatus;
    }

    public boolean isProjectComplete() {
        return projectDetailsStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus())
                && monitoringOfficerStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus())
                && financeChecksStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus())
                && spendProfileStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus())
                && grantOfferLetterStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus());
    }
}
