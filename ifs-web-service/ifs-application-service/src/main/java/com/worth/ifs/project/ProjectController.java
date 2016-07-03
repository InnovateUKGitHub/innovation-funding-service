package com.worth.ifs.project;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.service.ApplicationService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.bankdetails.resource.BankDetailsResource;
import com.worth.ifs.bankdetails.service.BankDetailsRestService;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.worth.ifs.project.BankDetailsUtils.getBankDetails;

/**
 * This controller will handle all requests that are related to a project.
 */
@Controller
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private BankDetailsRestService bankDetailsRestService;
	
    @RequestMapping(value = "/{projectId}", method = RequestMethod.GET)
    public String projectOverview(Model model, @PathVariable("projectId") final Long projectId, @ModelAttribute("loggedInUser") UserResource loggedInUser) {
        ProjectResource projectResource = projectService.getById(projectId);
        ApplicationResource applicationResource = applicationService.getById(projectResource.getApplication());
        CompetitionResource competitionResource = competitionService.getById(applicationResource.getCompetition());
        OrganisationResource organisationResource = projectService.getOrganisationByProjectAndUser(projectId, loggedInUser.getId());
        RestResult<BankDetailsResource> bankDetailsResourceRestResult = getBankDetails(projectId, organisationResource.getId(), bankDetailsRestService);
        if(bankDetailsResourceRestResult.isSuccess()) {
            BankDetailsResource bankDetails = bankDetailsResourceRestResult.getSuccessObject();
            model.addAttribute("bankDetails", bankDetails);
        }

        model.addAttribute("project", projectResource);
        model.addAttribute("app", applicationResource);
        model.addAttribute("competition", competitionResource);
        model.addAttribute("isFunded", true); // TODO: INFUND-3709 - Some partners don't need this

        return "project/overview";
    }
}
