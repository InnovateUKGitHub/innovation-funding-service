package org.innovateuk.ifs.project.status.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.finance.ProjectFinanceService;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.monitoringofficer.MonitoringOfficerService;
import org.innovateuk.ifs.project.monitoringofficer.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.project.sections.SectionStatus;
import org.innovateuk.ifs.project.status.StatusService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.security.SetupSectionAccessibilityHelper;
import org.innovateuk.ifs.project.status.viewmodel.SetupStatusViewModel;
import org.innovateuk.ifs.threads.resource.QueryResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;

/**
 * Populator for creating the {@link SetupStatusViewModel}
 */
@Service
public class SetupStatusViewModelPopulator {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectFinanceService projectFinanceService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private MonitoringOfficerService monitoringOfficerService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    public SetupStatusViewModel populateViewModel(Long projectId, UserResource loggedInUser) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(project.getApplication());
        CompetitionResource competition = competitionService.getById(applicationResource.getCompetition());

        boolean partnerProjectLocationRequired = competition.isLocationPerPartner();
        Optional<MonitoringOfficerResource> monitoringOfficer = monitoringOfficerService.getMonitoringOfficerForProject(projectId);
        OrganisationResource organisation = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(projectId, Optional.empty());
        ProjectPartnerStatusResource ownOrganisation = teamStatus.getPartnerStatusForOrganisation(organisation.getId()).get();
        SetupSectionAccessibilityHelper statusAccessor = new SetupSectionAccessibilityHelper(teamStatus);
        SetupSectionStatus sectionStatus = new SetupSectionStatus();

        boolean isLeadPartner = teamStatus.getLeadPartnerStatus().getOrganisationId().equals(organisation.getId());
        boolean isProjectManager = projectService.getProjectManager(projectId).map(pu -> pu.isUser(loggedInUser.getId())).orElse(false);
        boolean isProjectDetailsSubmitted = COMPLETE.equals(teamStatus.getLeadPartnerStatus().getProjectDetailsStatus());

        boolean isProjectDetailsProcessCompleted = isLeadPartner ? checkLeadPartnerProjectDetailsProcessCompleted(teamStatus, partnerProjectLocationRequired)
                : partnerProjectDetailsComplete(statusAccessor, organisation, partnerProjectLocationRequired);

        boolean awaitingProjectDetailsActionFromOtherPartners = isLeadPartner && awaitingProjectDetailsActionFromOtherPartners(teamStatus, partnerProjectLocationRequired);

        boolean requiredProjectDetailsForMonitoringOfficerComplete = requiredProjectDetailsForMonitoringOfficerComplete(partnerProjectLocationRequired, isProjectDetailsSubmitted, teamStatus);

        SectionAccess companiesHouseAccess = statusAccessor.canAccessCompaniesHouseSection(organisation);
        SectionAccess projectDetailsAccess = statusAccessor.canAccessProjectDetailsSection(organisation);
        SectionAccess monitoringOfficerAccess = statusAccessor.canAccessMonitoringOfficerSection(organisation, partnerProjectLocationRequired);
        SectionAccess bankDetailsAccess = statusAccessor.canAccessBankDetailsSection(organisation);
        SectionAccess financeChecksAccess = statusAccessor.canAccessFinanceChecksSection(organisation);
        SectionAccess spendProfileAccess = statusAccessor.canAccessSpendProfileSection(organisation);
        SectionAccess otherDocumentsAccess = statusAccessor.canAccessOtherDocumentsSection(organisation);
        SectionAccess grantOfferAccess = statusAccessor.canAccessGrantOfferLetterSection(organisation);

        SectionStatus projectDetailsStatus = sectionStatus.projectDetailsSectionStatus(isProjectDetailsProcessCompleted, awaitingProjectDetailsActionFromOtherPartners, isLeadPartner);
        SectionStatus monitoringOfficerStatus = sectionStatus.monitoringOfficerSectionStatus(monitoringOfficer.isPresent(), requiredProjectDetailsForMonitoringOfficerComplete);
        SectionStatus bankDetailsStatus = sectionStatus.bankDetailsSectionStatus(ownOrganisation.getBankDetailsStatus());
        SectionStatus financeChecksStatus = sectionStatus.financeChecksSectionStatus(ownOrganisation.getFinanceChecksStatus(), financeChecksAccess);
        SectionStatus spendProfileStatus= sectionStatus.spendProfileSectionStatus(ownOrganisation.getSpendProfileStatus());
        SectionStatus otherDocumentsStatus = sectionStatus.otherDocumentsSectionStatus(project, isProjectManager);
        SectionStatus grantOfferStatus = sectionStatus.grantOfferLetterSectionStatus(ownOrganisation.getGrantOfferLetterStatus(), isLeadPartner);

        int partnerOrganisationCount = projectService.getPartnerOrganisationsForProject(projectId).size();

        ProjectFinanceResource projectFinance = projectFinanceService.getProjectFinance(projectId, organisation.getId());

        ServiceResult<List<QueryResource>> queriesResult = financeCheckService.getQueries(projectFinance.getId());

        boolean pendingQueries = queriesResult.handleSuccessOrFailure(
                noQueries -> false,
                queries -> simpleAnyMatch(queries, query -> query.awaitingResponse));

        return new SetupStatusViewModel(project, competition, monitoringOfficer, organisation, isLeadPartner,
                companiesHouseAccess, projectDetailsAccess, monitoringOfficerAccess, bankDetailsAccess, financeChecksAccess, spendProfileAccess, otherDocumentsAccess, grantOfferAccess,
                projectDetailsStatus, monitoringOfficerStatus, bankDetailsStatus, financeChecksStatus, spendProfileStatus, otherDocumentsStatus, grantOfferStatus,
                partnerOrganisationCount > 1, isProjectManager, pendingQueries);
    }

    private boolean requiredProjectDetailsForMonitoringOfficerComplete(boolean partnerProjectLocationRequired, boolean isProjectDetailsSubmitted, ProjectTeamStatusResource teamStatus) {

        if (partnerProjectLocationRequired) {
            return isProjectDetailsSubmitted && allPartnersProjectLocationStatusComplete(teamStatus);
        } else {
            return isProjectDetailsSubmitted;
        }

    }

    private boolean partnerProjectDetailsComplete(SetupSectionAccessibilityHelper statusAccessor, OrganisationResource organisation, boolean partnerProjectLocationRequired) {
        boolean financeContactSubmitted = statusAccessor.isFinanceContactSubmitted(organisation);

        return partnerProjectLocationRequired ? financeContactSubmitted && statusAccessor.isPartnerProjectLocationSubmitted(organisation)
                : financeContactSubmitted;
    }

    public boolean checkLeadPartnerProjectDetailsProcessCompleted(ProjectTeamStatusResource teamStatus, boolean partnerProjectLocationRequired) {

        ProjectPartnerStatusResource leadPartnerStatus = teamStatus.getLeadPartnerStatus();

        boolean projectDetailsAndAllFinanceContactComplete =  COMPLETE.equals(leadPartnerStatus.getProjectDetailsStatus())
                && COMPLETE.equals(leadPartnerStatus.getFinanceContactStatus())
                && allOtherPartnersFinanceContactStatusComplete(teamStatus);

        return partnerProjectLocationRequired ? projectDetailsAndAllFinanceContactComplete
                                                && COMPLETE.equals(leadPartnerStatus.getPartnerProjectLocationStatus())
                                                && allOtherPartnersProjectLocationStatusComplete(teamStatus)
                : projectDetailsAndAllFinanceContactComplete;
    }

    private boolean awaitingProjectDetailsActionFromOtherPartners(ProjectTeamStatusResource teamStatus, boolean partnerProjectLocationRequired) {

        ProjectPartnerStatusResource leadPartnerStatus = teamStatus.getLeadPartnerStatus();

        return partnerProjectLocationRequired ? isAwaitingWhenProjectLocationRequired(teamStatus, leadPartnerStatus)
                : isAwaitingWhenProjectLocationNotRequired(teamStatus, leadPartnerStatus);
    }

    private boolean isAwaitingWhenProjectLocationRequired(ProjectTeamStatusResource teamStatus, ProjectPartnerStatusResource leadPartnerStatus) {
        return COMPLETE.equals(leadPartnerStatus.getProjectDetailsStatus())
                && COMPLETE.equals(leadPartnerStatus.getFinanceContactStatus())
                && COMPLETE.equals(leadPartnerStatus.getPartnerProjectLocationStatus())
                && (!allOtherPartnersFinanceContactStatusComplete(teamStatus) || !allOtherPartnersProjectLocationStatusComplete(teamStatus));
    }

    private boolean isAwaitingWhenProjectLocationNotRequired(ProjectTeamStatusResource teamStatus, ProjectPartnerStatusResource leadPartnerStatus) {
        return COMPLETE.equals(leadPartnerStatus.getProjectDetailsStatus())
                && COMPLETE.equals(leadPartnerStatus.getFinanceContactStatus())
                && !allOtherPartnersFinanceContactStatusComplete(teamStatus);
    }

    private boolean allOtherPartnersFinanceContactStatusComplete(ProjectTeamStatusResource teamStatus) {
        return teamStatus.checkForOtherPartners(status -> COMPLETE.equals(status.getFinanceContactStatus()));
    }

    private boolean allOtherPartnersProjectLocationStatusComplete(ProjectTeamStatusResource teamStatus) {
        return teamStatus.checkForOtherPartners(status -> COMPLETE.equals(status.getPartnerProjectLocationStatus()));
    }

    private boolean allPartnersProjectLocationStatusComplete(ProjectTeamStatusResource teamStatus) {
        return teamStatus.checkForAllPartners(status -> COMPLETE.equals(status.getPartnerProjectLocationStatus()));
    }
}
