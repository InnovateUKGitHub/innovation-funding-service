package org.innovateuk.ifs.project.financechecks.controller;

import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.financechecks.viewmodel.ProjectFinanceChecksViewModel;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.COMPLETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This controller will handle requests related to finance checks
 */
@Controller
@RequestMapping("/" + ProjectFinanceChecksController.BASE_DIR + "/{projectId}/partner-organisation/{organisationId}/finance-checks")
public class ProjectFinanceChecksController {

    public static final String BASE_DIR = "project";

    @Autowired
    ProjectService projectService;

    @Autowired
    OrganisationService organisationService;

    @PreAuthorize("hasPermission(#projectId, 'ACCESS_FINANCE_CHECKS_SECTION_EXTERNAL')")
    @RequestMapping(method = GET)
    public String viewFinanceChecks(Model model,
                                               @PathVariable("projectId") final Long projectId,
                                               @PathVariable("organisationId") final Long organisationId) {

        model.addAttribute("model", buildFinanceChecksLandingPage(projectId, organisationId));

        return "project/finance-checks";
    }

    private ProjectFinanceChecksViewModel buildFinanceChecksLandingPage(final Long projectId, final Long organisationId) {
        ProjectResource projectResource = projectService.getById(projectId);
        OrganisationResource organisationResource = organisationService.getOrganisationById(organisationId);
        boolean approved = isApproved(projectId, organisationId);

        return new ProjectFinanceChecksViewModel(projectResource, organisationResource, approved);
    }

    private boolean isApproved(final Long projectId, final Long organisationId) {
        Optional<ProjectPartnerStatusResource> organisationStatus = projectService.getProjectTeamStatus(projectId, Optional.empty()).getPartnerStatusForOrganisation(organisationId);
        return COMPLETE.equals(organisationStatus.get().getFinanceChecksStatus());
    }
}
