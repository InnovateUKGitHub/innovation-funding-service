package org.innovateuk.ifs.management.competition.previous.controller;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.previous.viewmodel.PreviousCompetitionViewModel;
import org.innovateuk.ifs.management.funding.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.management.navigation.ManagementApplicationOrigin;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;

@Controller
@RequestMapping("/competition/{competitionId}/previous")
@SecuredBySpring(value = "COMPETITION_PREVIOUS", description = "Only internal users can see previous applications")
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance','innovation_lead', 'stakeholder')")
public class PreviousCompetitionController {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private ApplicationFundingDecisionService applicationFundingDecisionService;

    @Autowired
    private ProjectRestService projectRestService;

    @GetMapping
    public String viewPreviousCompetition(@PathVariable long competitionId,
                                          @RequestParam MultiValueMap<String, String> queryParams,
                                          Model model,
                                          UserResource user) {
        model.addAttribute("model",  new PreviousCompetitionViewModel(
            competitionRestService.getCompetitionById(competitionId).getSuccess(),
            applicationSummaryRestService.getPreviousApplications(competitionId).getSuccess(),
            user.hasRole(Role.IFS_ADMINISTRATOR))
        );

        String originQuery = buildOriginQueryString(ManagementApplicationOrigin.PREVIOUS_APPLICATIONS, queryParams);
        model.addAttribute("originQuery", originQuery);
        return "competition/previous";
    }

    @SecuredBySpring(value = "UPDATE", description = "Only the IFS admin is able to mark an application as successful after funding decisions have been made")
    @PreAuthorize("hasAuthority('ifs_administrator')")
    @PostMapping("/mark-successful/application/{applicationId}")
    public String markApplicationAsSuccessful(
            @PathVariable("competitionId") long competitionId,
            @PathVariable("applicationId") long applicationId) {
        applicationFundingDecisionService.saveApplicationFundingDecisionData(competitionId, FundingDecision.FUNDED, singletonList(applicationId)).getSuccess();
        projectRestService.createProjectFromApplicationId(applicationId).getSuccess();

        return "redirect:/competition/{competitionId}/previous";
    }
}
