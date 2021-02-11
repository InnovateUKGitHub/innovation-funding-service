package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.consortiumoverview.viewmodel.ProjectConsortiumStatusViewModel;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.status.StatusService;
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
@PreAuthorize("hasAnyAuthority('applicant', 'comp_admin')")
public class TeamStatusController {

    @Autowired
    private StatusService statusService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectCompositeId', 'ACCESS_PROJECT_TEAM_STATUS')")
    @GetMapping
    public String viewProjectTeamStatus(Model model, @PathVariable("projectId") final Long projectId) {
        ProjectTeamStatusResource teamStatus = statusService.getProjectTeamStatus(projectId, Optional.empty());
        CompetitionResource competitionResource = populateProjectTeamStatuses(teamStatus, projectId);

        model.addAttribute("model", new ProjectConsortiumStatusViewModel(projectId, teamStatus, competitionResource.getProjectSetupStages()));
        return "project/consortium-status";
    }

    private CompetitionResource populateProjectTeamStatuses(ProjectTeamStatusResource consortiumStatus, final Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(project.getApplication());
        CompetitionResource competition = competitionRestService.getCompetitionById(applicationResource.getCompetition()).getSuccess();

        setLeadPartnerProjectDetailsTeamStatus(consortiumStatus);
        setOtherPartnersProjectDetailsTeamStatus(consortiumStatus);
        setLeadPartnerProjectTeamStatus(consortiumStatus);
        setLeadPartnerMonitoringOfficerStatus(consortiumStatus);
        return competition;
    }

    private void setLeadPartnerProjectTeamStatus(ProjectTeamStatusResource consortiumStatus) {
        boolean projectManagerSet = consortiumStatus.isProjectManagerAssigned();
        ProjectPartnerStatusResource leadPartner = consortiumStatus.getLeadPartnerStatus();
        boolean leadFinanceContactSet = leadPartner.getFinanceContactStatus().equals(COMPLETE);
        if(projectManagerSet && leadFinanceContactSet) {
            leadPartner.setProjectTeamStatus(COMPLETE);
        } else {
            leadPartner.setProjectTeamStatus(ACTION_REQUIRED);
        }
    }

    private void setLeadPartnerProjectDetailsTeamStatus(ProjectTeamStatusResource consortiumStatus) {

        boolean allPartnerProjectLocationsSubmitted = consortiumStatus
                .checkForAllPartners(projectPartnerStatusResource -> COMPLETE.equals(projectPartnerStatusResource.getPartnerProjectLocationStatus()));

        boolean requiredDetailsComplete = (allPartnerProjectLocationsSubmitted);

        ProjectPartnerStatusResource leadProjectPartnerStatusResource = consortiumStatus.getLeadPartnerStatus();
        if (!(COMPLETE.equals(leadProjectPartnerStatusResource.getProjectDetailsStatus()) && requiredDetailsComplete)) {
            leadProjectPartnerStatusResource.setProjectDetailsStatus(ACTION_REQUIRED);
        }
    }

    private void setOtherPartnersProjectDetailsTeamStatus(ProjectTeamStatusResource consortiumStatus) {

        CollectionFunctions.simpleMap(consortiumStatus.getOtherPartnersStatuses(),
                projectPartnerStatusResource -> setOtherPartnerProjectDetailsTeamStatus(projectPartnerStatusResource));
    }

    private ProjectPartnerStatusResource setOtherPartnerProjectDetailsTeamStatus(ProjectPartnerStatusResource otherPartnerStatusResource) {

        ProjectActivityStates partnerProjectLocationStatus = otherPartnerStatusResource.getPartnerProjectLocationStatus();

        if (!COMPLETE.equals(partnerProjectLocationStatus)) {
                otherPartnerStatusResource.setProjectDetailsStatus(ACTION_REQUIRED);
            } else {
                otherPartnerStatusResource.setProjectDetailsStatus(COMPLETE);
            }

        return otherPartnerStatusResource;
    }

    private void setLeadPartnerMonitoringOfficerStatus(ProjectTeamStatusResource teamStatus) {

        boolean allPartnerProjectLocationsSubmitted = teamStatus
                .checkForAllPartners(projectPartnerStatusResource -> COMPLETE.equals(projectPartnerStatusResource.getPartnerProjectLocationStatus()));

        ProjectPartnerStatusResource leadPartnerStatusResource = teamStatus.getLeadPartnerStatus();

        if (PENDING.equals(leadPartnerStatusResource.getMonitoringOfficerStatus()) && !allPartnerProjectLocationsSubmitted) {
            leadPartnerStatusResource.setMonitoringOfficerStatus(NOT_STARTED);
        }
    }

}
