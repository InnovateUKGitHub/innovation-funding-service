package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller will handle requests related to finance checks
 */
@Controller
@RequestMapping("/" + FinanceChecksController.BASE_DIR + "/{projectId}/partner-organisation/{organisationId}/finance-checks")
public class FinanceChecksController {

    public static final String BASE_DIR = "project";

    @Autowired
    ProjectService projectService;

    @Autowired
    OrganisationService organisationService;

    @RequestMapping(method = GET)
    public String viewFinanceChecks(Model model,
                                               @PathVariable("projectId") final Long projectId,
                                               @PathVariable("organisationId") final Long organisationId,
                                               @ModelAttribute("loggedInUser")UserResource loggedInUser) {

        model.addAttribute("model", buildFinanceChecksLandingPage(projectId, organisationId, loggedInUser));

        return "project/finance-checks";
    }

    private ProjectFinanceChecksViewModel buildFinanceChecksLandingPage(final Long projectId, final Long organisationId, final UserResource loggedInUser) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        boolean approved = isApproved(projectId, organisationId);

        return new ProjectFinanceChecksViewModel(projectResource, organisationResource, approved);
    }

    private boolean isApproved(final Long projectId, final Long organisationId) {
        ProjectTeamStatusResource teamStatus = projectService.getProjectTeamStatus(projectId, Optional.empty());
        return COMPLETE.equals(teamStatus.getPartnerStatusForOrganisation(organisationId).get().getFinanceChecksStatus());
    }
}
