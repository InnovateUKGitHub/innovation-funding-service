package org.innovateuk.ifs.project.status.viewmodel;


import org.innovateuk.ifs.sections.SectionAccess;

/**
 * A convenient container for multiple Project Setup sections' access levels
 */
public class SectionAccessList {

    private SectionAccess companiesHouseSection;
    private SectionAccess projectDetailsSection;
    private SectionAccess projectTeamSection;
    private SectionAccess monitoringOfficerSection;
    private SectionAccess bankDetailsSection;
    private SectionAccess financeChecksSection;
    private SectionAccess spendProfileSection;
    private SectionAccess documentsSection;
    private SectionAccess grantOfferLetterSection;

    public SectionAccessList(SectionAccess companiesHouseSection,
                             SectionAccess projectDetailsSection,
                             SectionAccess projectTeamSection,
                             SectionAccess monitoringOfficerSection,
                             SectionAccess bankDetailsSection,
                             SectionAccess financeChecksSection,
                             SectionAccess spendProfileSection,
                             SectionAccess documentsSection,
                             SectionAccess grantOfferLetterSection) {
        this.companiesHouseSection = companiesHouseSection;
        this.projectDetailsSection = projectDetailsSection;
        this.projectTeamSection = projectTeamSection;
        this.monitoringOfficerSection = monitoringOfficerSection;
        this.bankDetailsSection = bankDetailsSection;
        this.financeChecksSection = financeChecksSection;
        this.spendProfileSection = spendProfileSection;
        this.documentsSection = documentsSection;
        this.grantOfferLetterSection = grantOfferLetterSection;
    }

    public SectionAccess getCompaniesHouseSection() {
        return companiesHouseSection;
    }

    public SectionAccess getProjectDetailsSection() {
        return projectDetailsSection;
    }

    public SectionAccess getProjectTeamSection() {
        return projectTeamSection;
    }

    public SectionAccess getMonitoringOfficerSection() {
        return monitoringOfficerSection;
    }

    public SectionAccess getBankDetailsSection() {
        return bankDetailsSection;
    }

    public SectionAccess getFinanceChecksSection() {
        return financeChecksSection;
    }

    public SectionAccess getSpendProfileSection() {
        return spendProfileSection;
    }

    public SectionAccess getDocumentsSection() {
        return documentsSection;
    }

    public SectionAccess getGrantOfferLetterSection() {
        return grantOfferLetterSection;
    }
}
