package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.consortiumoverview.viewmodel.ProjectConsortiumStatusViewModel;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.status.StatusService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.util.CollectionFunctions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;

/**
 * This controller will handle display of project activity status for all partners
 */
@Controller
@RequestMapping("/project/{projectId}/team-status")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = TeamStatusController.class)
@PreAuthorize("hasAnyAuthority('applicant', 'project_finance', 'comp_admin')")
public class TeamStatusController {

    @Autowired
    private StatusService statusService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_STATUS')")
    @GetMapping
    public String viewProjectTeamStatus(Model model, @PathVariable("projectId") final Long projectId) {
        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(projectId, Optional.empty());
        populateProjectTeamStatuses(teamStatus, projectId);

        model.addAttribute("model", new ProjectConsortiumStatusViewModel(projectId, teamStatus));
        return "project/consortium-status";
    }

    private void populateProjectTeamStatuses(ProjectTeamStatusResource teamStatus, final Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(project.getApplication());
        CompetitionResource competition = competitionService.getById(applicationResource.getCompetition());

        boolean partnerProjectLocationRequired = competition.isLocationPerPartner();

        setLeadPartnerProjectDetailsTeamStatus(teamStatus, partnerProjectLocationRequired);
        setOtherPartnersProjectDetailsTeamStatus(teamStatus, partnerProjectLocationRequired);
        setLeadPartnerMonitoringOfficerStatus(teamStatus, partnerProjectLocationRequired);
    }

    private void setLeadPartnerProjectDetailsTeamStatus(ProjectTeamStatusResource teamStatus, boolean partnerProjectLocationRequired) {

        boolean allFinanceContactSubmitted = teamStatus.getPartnerStatuses()
                .stream()
                .allMatch(projectPartnerStatusResource -> COMPLETE.equals(projectPartnerStatusResource.getFinanceContactStatus()));

        boolean allPartnerProjectLocationsSubmitted = teamStatus
                .checkForAllPartners(projectPartnerStatusResource -> COMPLETE.equals(projectPartnerStatusResource.getPartnerProjectLocationStatus()));

        boolean allRequiredDetailsComplete;
        if (partnerProjectLocationRequired) {
            allRequiredDetailsComplete = allFinanceContactSubmitted && allPartnerProjectLocationsSubmitted;
        } else {
            allRequiredDetailsComplete = allFinanceContactSubmitted;
        }

        ProjectPartnerStatusResource leadProjectPartnerStatusResource = teamStatus.getLeadPartnerStatus();
        if (!(COMPLETE.equals(leadProjectPartnerStatusResource.getProjectDetailsStatus()) && allRequiredDetailsComplete)) {
            leadProjectPartnerStatusResource.setProjectDetailsStatus(ACTION_REQUIRED);
        }
    }

    private void setOtherPartnersProjectDetailsTeamStatus(ProjectTeamStatusResource teamStatus, boolean partnerProjectLocationRequired) {

        CollectionFunctions.simpleMap(teamStatus.getOtherPartnersStatuses(),
                projectPartnerStatusResource -> setOtherPartnerProjectDetailsTeamStatus(projectPartnerStatusResource, partnerProjectLocationRequired));
    }

    private ProjectPartnerStatusResource setOtherPartnerProjectDetailsTeamStatus(ProjectPartnerStatusResource otherPartnerStatusResource, boolean partnerProjectLocationRequired) {

        ProjectActivityStates financeContactStatus = otherPartnerStatusResource.getFinanceContactStatus();
        ProjectActivityStates partnerProjectLocationStatus = otherPartnerStatusResource.getPartnerProjectLocationStatus();

        if (partnerProjectLocationRequired) {
            if (COMPLETE.equals(financeContactStatus) && COMPLETE.equals(partnerProjectLocationStatus)) {
                otherPartnerStatusResource.setProjectDetailsStatus(COMPLETE);
            } else {
                otherPartnerStatusResource.setProjectDetailsStatus(ACTION_REQUIRED);
            }
        }

        return otherPartnerStatusResource;
    }

    private void setLeadPartnerMonitoringOfficerStatus(ProjectTeamStatusResource teamStatus, boolean partnerProjectLocationRequired) {

        boolean allPartnerProjectLocationsSubmitted = teamStatus
                .checkForAllPartners(projectPartnerStatusResource -> COMPLETE.equals(projectPartnerStatusResource.getPartnerProjectLocationStatus()));

        ProjectPartnerStatusResource leadPartnerStatusResource = teamStatus.getLeadPartnerStatus();

        if (partnerProjectLocationRequired) {
            if (PENDING.equals(leadPartnerStatusResource.getMonitoringOfficerStatus()) && !allPartnerProjectLocationsSubmitted) {
                leadPartnerStatusResource.setMonitoringOfficerStatus(NOT_STARTED);
            }
        }
    }

}
