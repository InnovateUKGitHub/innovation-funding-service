package org.innovateuk.ifs.project.organisationsize.controller;

import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.Boolean.TRUE;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}")
@PreAuthorize("hasAnyAuthority('project_finance', 'comp_admin', 'support', 'innovation_lead', 'stakeholder')")
public class ProjectOrganisationSizeController {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectRestService projectRestService;

    @GetMapping
    public String viewPage(
            @PathVariable long projectId,
            @PathVariable long organisationId) {

        long competitionId = projectRestService.getProjectById(projectId).getSuccess().getCompetition();
        return redirectToViewPage(projectId, organisationId, isIncludingGrowthTable(competitionId));
    }

    private String redirectToViewPage(long projectId, long organisationId, boolean includeGrowthTable) {
        return "redirect:" +
                String.format("/project/%d/organisation/%d/%s",
                        projectId,
                        organisationId,
                        includeGrowthTable ? "with-growth-table" : "without-growth-table");
    }

    private boolean isIncludingGrowthTable(long competitionId) {
        return competitionRestService.getCompetitionById(competitionId).
                andOnSuccessReturn(competition -> TRUE.equals(competition.getIncludeProjectGrowthTable())).getSuccess();
    }
}