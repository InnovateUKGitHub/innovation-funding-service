package org.innovateuk.ifs.management.competition.setup.closecompetition.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.closecompetition.populator.AlwaysOpenCloseCompetitionViewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/competition/{competitionId}/close-competition")
@SecuredBySpring(value = "Controller", description = "Comp Admins, Project Finance user" +
        "and IFS Admins can close Always open competitions", securedType = AlwaysOpenCloseCompetitionController.class)
@PreAuthorize("hasAnyAuthority('comp_admin','project_finance', 'ifs_administrator')")
public class AlwaysOpenCloseCompetitionController {

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private AlwaysOpenCloseCompetitionViewModelPopulator populator;

    @GetMapping
    public String viewPage(Model model,
                           @PathVariable("competitionId") Long competitionId) {
        model.addAttribute("model", populator.populate(competitionId));
        return "competition/setup/close-competition-always-open-competition";
    }


}
