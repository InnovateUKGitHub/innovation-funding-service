package org.innovateuk.ifs.project;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.financecheck.FinanceCheckService;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.project.sections.ProjectSetupSectionPartnerAccessor;
import org.innovateuk.ifs.project.sections.ProjectSetupSectionStatus;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.project.sections.SectionStatus;
import org.innovateuk.ifs.project.viewmodel.ProjectSetupStatusViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This controller will handle all requests that are related to a project.
 */
@Controller
@RequestMapping("/project")
@PreAuthorize("hasAuthority('applicant')")
public class ProjectSetupStatusController {

    public static final String PROJECT_SETUP_PAGE = "project/setup-status";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private FinanceCheckService financeCheckService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    public String viewProjectSetupStatus(Model model, @PathVariable("projectId") final Long projectId,
                                         @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                         NativeWebRequest springRequest) {

        HttpServletRequest request = springRequest.getNativeRequest(HttpServletRequest.class);
        String dashboardUrl = request.getScheme() + "://" +
            request.getServerName() +
            ":" + request.getServerPort() +
            "/applicant/dashboard";

        ProjectSetupStatusViewModel projectSetupStatusViewModel = getProjectSetupStatusViewModel(projectId, loggedInUser);
        model.addAttribute("model", projectSetupStatusViewModel);
        model.addAttribute("url", dashboardUrl);
        return PROJECT_SETUP_PAGE;
    }

    private ProjectSetupStatusViewModel getProjectSetupStatusViewModel(Long projectId, UserResource loggedInUser) {

        ProjectResource project = projectService.getById(projectId);
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);
        ApplicationResource applicationResource = applicationService.getById(project.getApplication());
        CompetitionResource competition = competitionService.getById(applicationResource.getCompetition());

        Optional<MonitoringOfficerResource> monitoringOfficer = projectService.getMonitoringOfficerForProject(projectId);
        OrganisationResource organisation = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        ProjectTeamStatusResource teamStatus = projectService.getProjectTeamStatus(projectId, Optional.empty());
        ProjectPartnerStatusResource ownOrganisation = teamStatus.getPartnerStatusForOrganisation(organisation.getId()).get();
        ProjectSetupSectionPartnerAccessor statusAccessor = new ProjectSetupSectionPartnerAccessor(teamStatus);
        ProjectSetupSectionStatus sectionStatus = new ProjectSetupSectionStatus();

        boolean leadPartner = teamStatus.getLeadPartnerStatus().getOrganisationId().equals(organisation.getId());
        boolean isProjectManager = projectService.getProjectManager(projectId).map(pu -> pu.isUser(loggedInUser.getId())).orElse(false);
        boolean isFinanceContact = projectUsers.stream().anyMatch(pu -> pu.isUser(loggedInUser.getId()) && pu.isFinanceContact());
        boolean projectDetailsSubmitted = COMPLETE.equals(teamStatus.getLeadPartnerStatus().getProjectDetailsStatus());
        boolean projectDetailsProcessCompleted = leadPartner ? checkLeadPartnerProjectDetailsProcessCompleted(teamStatus) : statusAccessor.isFinanceContactSubmitted(organisation);
        boolean awaitingProjectDetailsActionFromOtherPartners = leadPartner && awaitingProjectDetailsActionFromOtherPartners(teamStatus);

        SectionAccess companiesHouseAccess = statusAccessor.canAccessCompaniesHouseSection(organisation);
        SectionAccess projectDetailsAccess = statusAccessor.canAccessProjectDetailsSection(organisation);
        SectionAccess monitoringOfficerAccess = statusAccessor.canAccessMonitoringOfficerSection(organisation);
        SectionAccess bankDetailsAccess = statusAccessor.canAccessBankDetailsSection(organisation);
        SectionAccess financeChecksAccess = isFinanceContact ? statusAccessor.canAccessFinanceChecksSection(organisation) : SectionAccess.NOT_ACCESSIBLE;
        SectionAccess spendProfileAccess = statusAccessor.canAccessSpendProfileSection(organisation);
        SectionAccess otherDocumentsAccess = statusAccessor.canAccessOtherDocumentsSection(organisation);
        SectionAccess grantOfferAccess = statusAccessor.canAccessGrantOfferLetterSection(organisation);

        SectionStatus projectDetailsStatus = sectionStatus.projectDetailsSectionStatus(projectDetailsProcessCompleted, awaitingProjectDetailsActionFromOtherPartners, leadPartner);
        SectionStatus monitoringOfficerStatus = sectionStatus.monitoringOfficerSectionStatus(monitoringOfficer.isPresent(), projectDetailsSubmitted);
        SectionStatus bankDetailsStatus = sectionStatus.bankDetailsSectionStatus(ownOrganisation.getBankDetailsStatus());
        SectionStatus financeChecksStatus = sectionStatus.financeChecksSectionStatus(ownOrganisation.getFinanceChecksStatus(), checkAllFinanceChecksApproved(teamStatus), financeChecksAccess);
        SectionStatus spendProfileStatus= sectionStatus.spendProfileSectionStatus(ownOrganisation.getSpendProfileStatus());
        SectionStatus otherDocumentsStatus = sectionStatus.otherDocumentsSectionStatus(project, isProjectManager);
        SectionStatus grantOfferStatus = sectionStatus.grantOfferLetterSectionStatus(ownOrganisation.getGrantOfferLetterStatus(), leadPartner);

        return new ProjectSetupStatusViewModel(project, competition, monitoringOfficer, organisation, leadPartner,
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
