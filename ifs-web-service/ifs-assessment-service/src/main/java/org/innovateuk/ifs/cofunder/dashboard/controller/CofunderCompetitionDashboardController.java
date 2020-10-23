package org.innovateuk.ifs.supporter.dashboard.controller;

import org.innovateuk.ifs.supporter.dashboard.viewmodel.SupporterCompetitionDashboardViewModel;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationPageResource;
import org.innovateuk.ifs.supporter.service.SupporterDashboardRestService;
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
@RequestMapping("/supporter/dashboard/competition/{competitionId}")
@SecuredBySpring(value = "Controller", description = "Supporters can view their dashboard", securedType = SupporterCompetitionDashboardController.class)
@PreAuthorize("hasAuthority('supporter')")
public class SupporterCompetitionDashboardController {

    @Autowired
    private SupporterDashboardRestService supporterDashboardRestService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @GetMapping
    public String view(@PathVariable long competitionId,
                       @RequestParam(name = "page", defaultValue = "1") int page,
                       UserResource loggedInUser,
                       Model model) {
        SupporterDashboardApplicationPageResource pageResource = supporterDashboardRestService.getSupporterCompetitionDashboardApplications(loggedInUser.getId(), competitionId, page - 1).getSuccess();
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        model.addAttribute("model", new SupporterCompetitionDashboardViewModel(pageResource, competition));
        return "supporter/supporter-competition-dashboard";
    }
}
