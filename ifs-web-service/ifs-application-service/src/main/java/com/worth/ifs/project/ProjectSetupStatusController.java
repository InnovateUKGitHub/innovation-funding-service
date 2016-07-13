package com.worth.ifs.project;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.viewmodel.ProjectSetupStatusViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

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
    public String viewProjectSetupStatus(Model model, @PathVariable("projectId") final Long projectId) {

        model.addAttribute("model", getProjectSetupStatusViewModel(projectId));
        return "project/overview";
    }

    private ProjectSetupStatusViewModel getProjectSetupStatusViewModel(Long projectId) {
        ProjectResource project = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(project.getApplication());
        CompetitionResource competition = competitionService.getById(applicationResource.getCompetition());
        Optional<MonitoringOfficerResource> monitoringOfficer = projectService.getMonitoringOfficerForProject(projectId);
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        Optional<BankDetailsResource> bankDetailsResource = bankDetailsRestService.getBankDetailsByProjectAndOrganisation(projectId, organisationResource.getId()).toOptionalIfNotFound().getSuccessObject();
        if(bankDetailsResource.isPresent()) {
            model.addAttribute("bankDetails", bankDetailsResource.get());
        }
        model.addAttribute("isFunded", true); // TODO: INFUND-3709 - Some partners don't need this

        return new ProjectSetupStatusViewModel(project, competition, monitoringOfficer);
    }
}
