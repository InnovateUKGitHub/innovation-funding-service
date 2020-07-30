package org.innovateuk.ifs.project.pendingpartner.controller;


import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.status.controller.SetupStatusController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.lang.String.format;

@Controller
@RequestMapping("/project/{projectId}/organisation/{organisationId}/your-organisation")
@SecuredBySpring(value = "Controller", description = "Project partners can see the Your Organisation page for their organisation",
        securedType = SetupStatusController.class)
@PreAuthorize("hasAuthority('applicant')")
public class ProjectYourOrganisationController {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ProjectRestService projectRestService;

    @GetMapping
    public String viewPage(@PathVariable long projectId,
                           @PathVariable long organisationId) {
        ProjectResource project = projectRestService.getProjectById(projectId).getSuccess();

        CompetitionResource competition = competitionRestService.getCompetitionById(project.getCompetition()).getSuccess();

        String urlPart;
        if (competition.getFundingType() == FundingType.KTP) {
            urlPart = "ktp-financial-years";
        } else if (competition.getIncludeProjectGrowthTable()) {
            urlPart = "with-growth-table";
        } else {
            urlPart = "without-growth-table";
        }
        return format("redirect:/project/%d/organisation/%d/your-organisation/%s",
                projectId,
                organisationId,
                urlPart);
    }
}
