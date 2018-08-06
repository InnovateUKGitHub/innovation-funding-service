package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.projectdetails.viewmodel.BasicProjectDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.sections.SectionStatus;

import java.util.Optional;

/**
 * A view model that backs the Project Status page
 */
public class SetupStatusViewModel implements BasicProjectDetailsViewModel {

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
    private SectionAccessList sectionAccesses;
    private SectionStatusList sectionStatuses;
    private boolean collaborationAgreementRequired;
    private boolean projectManager;
    private boolean pendingQuery;
    private String originQuery;

    public SetupStatusViewModel() {}

    public SetupStatusViewModel(ProjectResource project,
                                CompetitionResource competition,
                                Optional<MonitoringOfficerResource> monitoringOfficerResource,
                                OrganisationResource organisation,
                                boolean leadPartner,
                                SectionAccessList sectionAccesses,
                                SectionStatusList sectionStatuses,
                                boolean collaborationAgreementRequired,
                                boolean projectManager,
                                boolean pendingQuery,
                                String originQuery) {

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
        this.sectionAccesses = sectionAccesses;
        this.sectionStatuses = sectionStatuses;
        this.projectComplete = sectionStatuses.isProjectComplete();
        this.collaborationAgreementRequired = collaborationAgreementRequired;
        this.projectManager = projectManager;
        this.pendingQuery = pendingQuery;
        this.originQuery = originQuery;
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
        return sectionAccesses.getCompaniesHouseSection();
    }

    public SectionAccess getProjectDetailsSection() {
        return sectionAccesses.getProjectDetailsSection();
    }

    public SectionAccess getMonitoringOfficerSection() {
        return sectionAccesses.getMonitoringOfficerSection();
    }

    public SectionAccess getBankDetailsSection() {
        return sectionAccesses.getBankDetailsSection();
    }

    public SectionAccess getFinanceChecksSection() {
        return sectionAccesses.getFinanceChecksSection();
    }

    public SectionAccess getSpendProfileSection() {
        return sectionAccesses.getSpendProfileSection();
    }

    public SectionAccess getOtherDocumentsSection() {
        return sectionAccesses.getOtherDocumentsSection();
    }

    public SectionAccess getGrantOfferLetterSection() {
        return sectionAccesses.getGrantOfferLetterSection();
    }

    public SectionStatus getProjectDetailsStatus() { return sectionStatuses.getProjectDetailsStatus(); }

    public SectionStatus getMonitoringOfficerStatus() {
        return sectionStatuses.getMonitoringOfficerStatus();
    }

    public SectionStatus getBankDetailsStatus() {
        return sectionStatuses.getBankDetailsStatus();
    }

    public SectionStatus getFinanceChecksStatus() {
        return sectionStatuses.getFinanceChecksStatus();
    }

    public SectionStatus getSpendProfileStatus() {
        return sectionStatuses.getSpendProfileStatus();
    }

    public SectionStatus getOtherDocumentsStatus() {
        return sectionStatuses.getOtherDocumentsStatus();
    }

    public SectionStatus getGrantOfferLetterStatus() {
        return sectionStatuses.getGrantOfferLetterStatus();
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

    public boolean isCollaborationAgreementRequired() { return collaborationAgreementRequired; }

    public boolean isProjectManager() { return projectManager; }

    public boolean isShowFinanceChecksPendingQueryWarning() {
        return pendingQuery;
    }

    public String getOriginQuery() {
        return originQuery;
    }
}
