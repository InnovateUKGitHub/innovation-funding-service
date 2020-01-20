package org.innovateuk.ifs.project.organisationsize.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.project.finance.service.ProjectYourOrganisationRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}")
public class ProjectOrganisationSizeController {

    private ProjectYourOrganisationRestService projectYourOrganisationRestService;

    @Autowired
    ProjectOrganisationSizeController(ProjectYourOrganisationRestService projectYourOrganisationRestService) {
        this.projectYourOrganisationRestService = projectYourOrganisationRestService;
    }

    public ProjectOrganisationSizeController() {
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
    @SecuredBySpring(value = "VIEW_YOUR_ORGANISATION", description = "Applicants and internal users can view the Your organisation page")
    public String viewPage(
            @PathVariable long projectId,
            @PathVariable long organisationId) {

        boolean includeGrowthTable = projectYourOrganisationRestService.isIncludingGrowthTable(projectId).getSuccess();

        return redirectToViewPage(projectId, organisationId, includeGrowthTable);
    }

    private String redirectToViewPage(long projectId, long organisationId, boolean includeGrowthTable) {
        return "redirect:" +
                String.format("/project/%d/organisation/%d/",
                        projectId,
                        organisationId,
                        includeGrowthTable ? "with-growth-table" : "without-growth-table");
    }
}