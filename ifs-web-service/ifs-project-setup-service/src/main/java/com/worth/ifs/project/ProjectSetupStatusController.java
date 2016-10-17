package com.worth.ifs.project;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.bankdetails.service.BankDetailsRestService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.project.resource.*;
import com.worth.ifs.project.sections.ProjectSetupSectionPartnerAccessor;
import com.worth.ifs.project.sections.SectionAccess;
import com.worth.ifs.project.viewmodel.ProjectSetupStatusViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
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

import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * This controller will handle all requests that are related to a project.
 */
@Controller
@RequestMapping("/project")
public class ProjectSetupStatusController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private BankDetailsRestService bankDetailsRestService;
	
    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    public String viewProjectSetupStatus(Model model, @PathVariable("projectId") final Long projectId,
                                         @ModelAttribute("loggedInUser") UserResource loggedInUser,
                                         NativeWebRequest springRequest) {

        HttpServletRequest request = springRequest.getNativeRequest(HttpServletRequest.class);
        String dashboardUrl = request.getScheme() + "://" +
            request.getServerName() +
            ":" + request.getServerPort() +
            "/applicant/dashboard";


        model.addAttribute("model", getProjectSetupStatusViewModel(projectId, loggedInUser));
        model.addAttribute("url", dashboardUrl);
        return "project/setup-status";
    }

    private ProjectSetupStatusViewModel getProjectSetupStatusViewModel(Long projectId, UserResource loggedInUser) {

        ProjectResource project = projectService.getById(projectId);
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(projectId);

        ApplicationResource applicationResource = applicationService.getById(project.getApplication());
        CompetitionResource competition = competitionService.getById(applicationResource.getCompetition());

        Optional<MonitoringOfficerResource> monitoringOfficer = projectService.getMonitoringOfficerForProject(projectId);

        OrganisationResource organisation = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());


        ProjectTeamStatusResource teamStatus = projectService.getProjectTeamStatus(projectId, Optional.empty());
        ProjectSetupSectionPartnerAccessor statusAccessor = new ProjectSetupSectionPartnerAccessor(teamStatus);
        boolean projectDetailsSubmitted = statusAccessor.isProjectDetailsSubmitted();
        boolean grantOfferLetterSubmitted = project.getOfferSubmittedDate() != null;
        boolean spendProfilesSubmitted = project.getSpendProfileSubmittedDate() != null;

        ProjectUserResource loggedInUserPartner = simpleFindFirst(projectUsers, pu ->
                pu.getUser().equals(loggedInUser.getId()) &&
                pu.getRoleName().equals(UserRoleType.PARTNER.getName())).get();

        boolean leadPartner = teamStatus.getLeadPartnerStatus().getOrganisationId().equals(loggedInUserPartner.getOrganisation());

        Optional<ProjectActivityStates> bankDetailsState = teamStatus.getPartnerStatusForOrganisation(organisation.getId()).map(ProjectPartnerStatusResource::getBankDetailsStatus);

        return new ProjectSetupStatusViewModel(project, competition, monitoringOfficer, bankDetailsState,
                organisation.getId(), projectDetailsSubmitted, leadPartner, grantOfferLetterSubmitted, spendProfilesSubmitted,
                statusAccessor.canAccessCompaniesHouseSection(organisation),
                statusAccessor.canAccessProjectDetailsSection(organisation),
                statusAccessor.canAccessMonitoringOfficerSection(organisation),
                statusAccessor.canAccessBankDetailsSection(organisation),
                statusAccessor.canAccessFinanceChecksSection(organisation),
                statusAccessor.canAccessSpendProfileSection(organisation),
                statusAccessor.canAccessOtherDocumentsSection(organisation),
                SectionAccess.ACCESSIBLE);
    }
}
