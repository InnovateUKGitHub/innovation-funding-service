package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.project.sections.SectionStatus;
import org.innovateuk.ifs.project.projectdetails.viewmodel.BasicProjectDetailsViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.Optional;

import static org.innovateuk.ifs.project.sections.SectionStatus.TICK;

/**
 * A view model that backs the Project Status page
 */
public class ProjectSetupStatusViewModel implements BasicProjectDetailsViewModel {

    private Long projectId;
    private String projectName;
    private Long applicationId;
    private String competitionName;
    private Long competitionId;
    private boolean monitoringOfficerAssigned;
    private boolean leadPartner;
    private boolean hasCompanyHouse;
    private boolean projectComplete;
    private String monitoringOfficerName;
    private Long organisationId;
    private SectionAccess companiesHouseSection;
    private SectionAccess projectDetailsSection;
    private SectionAccess monitoringOfficerSection;
    private SectionAccess bankDetailsSection;
    private SectionAccess financeChecksSection;
    private SectionAccess spendProfileSection;
    private SectionAccess otherDocumentsSection;
    private SectionAccess grantOfferLetterSection;
    private SectionStatus projectDetailsStatus;
    private SectionStatus monitoringOfficerStatus;
    private SectionStatus bankDetailsStatus;
    private SectionStatus financeChecksStatus;
    private SectionStatus spendProfileStatus;
    private SectionStatus otherDocumentsStatus;
    private SectionStatus grantOfferLetterStatus;

    public ProjectSetupStatusViewModel() {};

    public ProjectSetupStatusViewModel(ProjectResource project, CompetitionResource competition,
                                       Optional<MonitoringOfficerResource> monitoringOfficerResource, OrganisationResource organisation, boolean leadPartner,
                                       SectionAccess companiesHouseSection, SectionAccess projectDetailsSection,
                                       SectionAccess monitoringOfficerSection, SectionAccess bankDetailsSection,
                                       SectionAccess financeChecksSection, SectionAccess spendProfileSection,
                                       SectionAccess otherDocumentsSection, SectionAccess grantOfferLetterSection,
                                       SectionStatus projectDetailsStatus, SectionStatus monitoringOfficerStatus,
                                       SectionStatus bankDetailsStatus, SectionStatus financeChecksStatus,
                                       SectionStatus spendProfileStatus, SectionStatus otherDocumentsStatus,
                                       SectionStatus grantOfferLetterStatus) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.applicationId = project.getApplication();
        this.competitionName = competition.getName();
        this.competitionId = competition.getId();
        this.leadPartner = leadPartner;
        this.hasCompanyHouse = organisation.getCompanyHouseNumber() != null && !organisation.getCompanyHouseNumber().isEmpty();
        this.monitoringOfficerAssigned = monitoringOfficerResource.isPresent();
        this.monitoringOfficerName = monitoringOfficerResource.map(mo -> mo.getFullName()).orElse("");
        this.organisationId = organisation.getId();
        this.companiesHouseSection = companiesHouseSection;
        this.projectDetailsSection = projectDetailsSection;
        this.monitoringOfficerSection = monitoringOfficerSection;
        this.bankDetailsSection = bankDetailsSection;
        this.financeChecksSection = financeChecksSection;
        this.spendProfileSection = spendProfileSection;
        this.otherDocumentsSection = otherDocumentsSection;
        this.grantOfferLetterSection = grantOfferLetterSection;
        this.projectDetailsStatus = projectDetailsStatus;
        this.monitoringOfficerStatus = monitoringOfficerStatus;
        this.bankDetailsStatus = bankDetailsStatus;
        this.financeChecksStatus = financeChecksStatus;
        this.spendProfileStatus = spendProfileStatus;
        this.otherDocumentsStatus = otherDocumentsStatus;
        this.grantOfferLetterStatus = grantOfferLetterStatus;
        this.projectComplete = projectDetailsStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus())
                && monitoringOfficerStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus())
                && financeChecksStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus())
                && spendProfileStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus())
                && grantOfferLetterStatus.getSectionStatus().equalsIgnoreCase(TICK.getSectionStatus());
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isMonitoringOfficerAssigned() {
        return monitoringOfficerAssigned;
    }

    public String getMonitoringOfficerName() {
        return monitoringOfficerName;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public boolean isLeadPartner() {
        return leadPartner;
    }

    public boolean isNonLeadPartner() {
        return !isLeadPartner();
    }

    public SectionAccess getCompaniesHouseSection() {
        return companiesHouseSection;
    }

    public SectionAccess getProjectDetailsSection() {
        return projectDetailsSection;
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

    public SectionAccess getOtherDocumentsSection() {
        return otherDocumentsSection;
    }

    public SectionAccess getGrantOfferLetterSection() {
        return grantOfferLetterSection;
    }

    public SectionStatus getProjectDetailsStatus() { return projectDetailsStatus; }

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

    public Long getCompetitionId() {
        return competitionId;
    }

    public boolean isHasCompanyHouse() {
        return hasCompanyHouse;
    }

    public boolean isProjectComplete() {
        return projectComplete;
    }
}
