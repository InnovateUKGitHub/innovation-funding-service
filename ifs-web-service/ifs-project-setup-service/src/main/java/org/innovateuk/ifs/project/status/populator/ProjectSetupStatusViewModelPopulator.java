package org.innovateuk.ifs.project.status.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.sections.ProjectSetupSectionsAccessibilityHelper;
import org.innovateuk.ifs.project.sections.ProjectSetupSectionStatus;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.project.sections.SectionStatus;
import org.innovateuk.ifs.project.status.viewmodel.ProjectSetupStatusViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;

/**
 * Populator for creating the {@link ProjectSetupStatusViewModel}
 */
@Service
public class ProjectSetupStatusViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    public ProjectSetupStatusViewModel populateViewModel(Long projectId, UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(project.getApplication());
        CompetitionResource competition = competitionService.getById(applicationResource.getCompetition());

        Optional<MonitoringOfficerResource> monitoringOfficer = projectService.getMonitoringOfficerForProject(projectId);
        OrganisationResource organisation = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        ProjectTeamStatusResource teamStatus = projectService.getProjectTeamStatus(projectId, Optional.empty());
        ProjectPartnerStatusResource ownOrganisation = teamStatus.getPartnerStatusForOrganisation(organisation.getId()).get();
        ProjectSetupSectionsAccessibilityHelper statusAccessor = new ProjectSetupSectionsAccessibilityHelper(teamStatus);
        ProjectSetupSectionStatus sectionStatus = new ProjectSetupSectionStatus();

        boolean isLeadPartner = teamStatus.getLeadPartnerStatus().getOrganisationId().equals(organisation.getId());
        boolean isProjectManager = projectService.getProjectManager(projectId).map(pu -> pu.isUser(loggedInUser.getId())).orElse(false);
        boolean isProjectDetailsSubmitted = COMPLETE.equals(teamStatus.getLeadPartnerStatus().getProjectDetailsStatus());
        boolean isProjectDetailsProcessCompleted = isLeadPartner ? checkLeadPartnerProjectDetailsProcessCompleted(teamStatus) : statusAccessor.isFinanceContactSubmitted(organisation);
        boolean awaitingProjectDetailsActionFromOtherPartners = isLeadPartner && awaitingProjectDetailsActionFromOtherPartners(teamStatus);

        SectionAccess companiesHouseAccess = statusAccessor.canAccessCompaniesHouseSection(organisation);
        SectionAccess projectDetailsAccess = statusAccessor.canAccessProjectDetailsSection(organisation);
        SectionAccess monitoringOfficerAccess = statusAccessor.canAccessMonitoringOfficerSection(organisation);
        SectionAccess bankDetailsAccess = statusAccessor.canAccessBankDetailsSection(organisation);
        SectionAccess financeChecksAccess = statusAccessor.canAccessFinanceChecksSection(organisation);
        SectionAccess spendProfileAccess = statusAccessor.canAccessSpendProfileSection(organisation);
        SectionAccess otherDocumentsAccess = statusAccessor.canAccessOtherDocumentsSection(organisation);
        SectionAccess grantOfferAccess = statusAccessor.canAccessGrantOfferLetterSection(organisation);

        SectionStatus projectDetailsStatus = sectionStatus.projectDetailsSectionStatus(isProjectDetailsProcessCompleted, awaitingProjectDetailsActionFromOtherPartners, isLeadPartner);
        SectionStatus monitoringOfficerStatus = sectionStatus.monitoringOfficerSectionStatus(monitoringOfficer.isPresent(), isProjectDetailsSubmitted);
        SectionStatus bankDetailsStatus = sectionStatus.bankDetailsSectionStatus(ownOrganisation.getBankDetailsStatus());
        SectionStatus financeChecksStatus = sectionStatus.financeChecksSectionStatus(ownOrganisation.getFinanceChecksStatus(), checkAllFinanceChecksApproved(teamStatus), financeChecksAccess);
        SectionStatus spendProfileStatus= sectionStatus.spendProfileSectionStatus(ownOrganisation.getSpendProfileStatus());
        SectionStatus otherDocumentsStatus = sectionStatus.otherDocumentsSectionStatus(project, isProjectManager);
        SectionStatus grantOfferStatus = sectionStatus.grantOfferLetterSectionStatus(ownOrganisation.getGrantOfferLetterStatus(), isLeadPartner);

        return new ProjectSetupStatusViewModel(project, competition, monitoringOfficer, organisation, isLeadPartner,
                companiesHouseAccess, projectDetailsAccess, monitoringOfficerAccess, bankDetailsAccess, financeChecksAccess, spendProfileAccess, otherDocumentsAccess, grantOfferAccess,
                projectDetailsStatus, monitoringOfficerStatus, bankDetailsStatus, financeChecksStatus, spendProfileStatus, otherDocumentsStatus, grantOfferStatus);
    }

    private boolean checkAllFinanceChecksApproved(ProjectTeamStatusResource teamStatus) {
        return teamStatus.checkForAllPartners(status -> COMPLETE.equals(status.getFinanceChecksStatus()));
    }

    private boolean checkLeadPartnerProjectDetailsProcessCompleted(ProjectTeamStatusResource teamStatus) {

        ProjectPartnerStatusResource leadPartnerStatus = teamStatus.getLeadPartnerStatus();

        return COMPLETE.equals(leadPartnerStatus.getProjectDetailsStatus())
                && COMPLETE.equals(leadPartnerStatus.getFinanceContactStatus())
                && allOtherPartnersFinanceContactStatusComplete(teamStatus);
    }

    private boolean awaitingProjectDetailsActionFromOtherPartners(ProjectTeamStatusResource teamStatus) {

        ProjectPartnerStatusResource leadPartnerStatus = teamStatus.getLeadPartnerStatus();

        return COMPLETE.equals(leadPartnerStatus.getProjectDetailsStatus())
                && COMPLETE.equals(leadPartnerStatus.getFinanceContactStatus())
                && !allOtherPartnersFinanceContactStatusComplete(teamStatus);
    }

    private boolean allOtherPartnersFinanceContactStatusComplete(ProjectTeamStatusResource teamStatus) {
        return teamStatus.checkForOtherPartners(status -> COMPLETE.equals(status.getFinanceContactStatus()));
    }
}
