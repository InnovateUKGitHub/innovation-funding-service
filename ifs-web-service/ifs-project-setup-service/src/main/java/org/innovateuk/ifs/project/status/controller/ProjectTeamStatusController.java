package org.innovateuk.ifs.project.status.controller;

import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.consortiumoverview.viewmodel.ProjectConsortiumStatusViewModel;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.ACTION_REQUIRED;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;

/**
 * This controller will handle display of project activity status for all partners
 */
@Controller
@RequestMapping("/project/{projectId}/team-status")
@PreAuthorize("hasAnyAuthority('applicant', 'project_finance', 'comp_admin')")
public class ProjectTeamStatusController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public String viewProjectTeamStatus(Model model, @PathVariable("projectId") final Long projectId) {
        ProjectTeamStatusResource teamStatus = projectService.getProjectTeamStatus(projectId, Optional.empty());
        setLeadPartnerProjectDetailsTeamStatus(teamStatus);

        model.addAttribute("model", new ProjectConsortiumStatusViewModel(projectId, teamStatus));
        return "project/consortium-status";
    }

    private void setLeadPartnerProjectDetailsTeamStatus(ProjectTeamStatusResource teamStatus) {
        boolean allFinanceContactSubmitted = teamStatus.getPartnerStatuses().stream().allMatch(projectPartnerStatusResource -> COMPLETE.equals(projectPartnerStatusResource.getFinanceContactStatus()));
        ProjectPartnerStatusResource leadProjectPartnerStatusResource = teamStatus.getLeadPartnerStatus();
        if (!(COMPLETE.equals(leadProjectPartnerStatusResource.getProjectDetailsStatus()) && allFinanceContactSubmitted)) {
            leadProjectPartnerStatusResource.setProjectDetailsStatus(ACTION_REQUIRED);
        }
    }

}
