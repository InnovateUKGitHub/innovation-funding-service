package org.innovateuk.ifs.management.competition.setup.closecompetition.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.competition.setup.closecompetition.populator.AlwaysOpenCloseCompetitionViewModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.competition.resource.MilestoneType.FEEDBACK_RELEASED;

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

    @Autowired
    private MilestoneRestService milestoneRestService;

    @GetMapping
    public String viewPage(Model model,
                           @PathVariable long competitionId) {
        model.addAttribute("model", populator.populate(competitionId));
        return "competition/setup/close-always-open-competition";
    }

    @PostMapping
    public String closeCompetition(Model model,
                                   @PathVariable long competitionId,
                                   @SuppressWarnings("unused") BindingResult bindingResult,
                                   ValidationHandler validationHandler) {
        MilestoneResource milestone = new MilestoneResource();
        milestone.setCompetitionId(competitionId);
        milestone.setDate(now());
        milestone.setType(FEEDBACK_RELEASED); // not sure if this is what's required
        milestoneRestService.create(milestone);
//        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
//        competitionResource.setFeedbackReleasedDate(now()); // temporarily set to now(). I assume this should match the last notifications entry in milestone table

        return String.format("redirect:/management/competition/%d", competitionId);
    }
}
