package org.innovateuk.ifs.cofunder.dashboard.controller;

import org.innovateuk.ifs.cofunder.dashboard.viewmodel.CofunderCompetitionDashboardViewModel;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationPageResource;
import org.innovateuk.ifs.cofunder.service.CofunderDashboardRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cofunder/dashboard/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "Cofunders can view their dashboard", securedType = CofunderCompetitionDashboardController.class)
@PreAuthorize("hasAuthority('cofunder')")
public class CofunderCompetitionDashboardController {

    @Autowired
    private CofunderDashboardRestService cofunderDashboardRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @GetMapping
    public String view(@PathVariable long competitionId,
                       @RequestParam(name = "page", defaultValue = "1") int page,
                       UserResource loggedInUser,
                       Model model) {
        CofunderDashboardApplicationPageResource pageResource = cofunderDashboardRestService.getCofunderCompetitionDashboardApplications(loggedInUser.getId(), competitionId, page - 1).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        model.addAttribute("model", new CofunderCompetitionDashboardViewModel(pageResource, competition));
        return "cofunder/cofunder-competition-dashboard";
    }
}
