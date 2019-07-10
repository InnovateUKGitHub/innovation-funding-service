package org.innovateuk.ifs.management.competition.previous.controller;

import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.exception.IncorrectStateForPageException;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.previous.viewmodel.PreviousCompetitionViewModel;
import org.innovateuk.ifs.management.navigation.ManagementApplicationOrigin;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.innovateuk.ifs.origin.BackLinkUtil.buildOriginQueryString;

@Controller
@RequestMapping("/competition/{competitionId}/previous")
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance','innovation_lead', 'stakeholder')")
public class PreviousCompetitionController {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @GetMapping
    public String viewPreviousCompetition(@PathVariable long competitionId,
                                          @RequestParam MultiValueMap<String, String> queryParams,
                                          Model model,
                                          UserResource user) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        checkCompetitionIsOpen(competition);

        model.addAttribute("model",  new PreviousCompetitionViewModel(competitionRestService.getCompetitionById(competitionId).getSuccess(),
        applicationSummaryRestService.getPreviousApplications(competitionId).getSuccess(),
        user.hasRole(Role.IFS_ADMINISTRATOR)));
        String originQuery = buildOriginQueryString(ManagementApplicationOrigin.PREVIOUS_APPLICATIONS, queryParams);
        model.addAttribute("originQuery", originQuery);
        return "competition/previous";
    }


    private void checkCompetitionIsOpen(CompetitionResource competition ) {
        if (!competition.getCompetitionStatus().isLaterThan(CompetitionStatus.READY_TO_OPEN)) {
            throw new IncorrectStateForPageException("Competition is not yet open.");
        }
    }
}
