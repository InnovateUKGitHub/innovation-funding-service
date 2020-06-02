package org.innovateuk.ifs.management.competition.setup.postawardservice.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupPostAwardServiceRestService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.postawardservice.form.PostAwardServiceForm;
import org.innovateuk.ifs.management.competition.setup.postawardservice.populator.ChoosePostAwardServiceModelPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/competition/setup")
@SecuredBySpring(value = "Controller", description = "Controller for choosing post award service", securedType = CompetitionSetupPostAwardServiceController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance', 'ifs_administrator')")
public class CompetitionSetupPostAwardServiceController {

    private static final String COMPETITION_ID_KEY = "competitionId";
    private static final String MODEL = "model";
    private static final String FORM_ATTR_NAME = "form";

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupPostAwardServiceRestService competitionSetupPostAwardServiceRestService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private ChoosePostAwardServiceModelPopulator choosePostAwardServiceModelPopulator;


    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'CHOOSE_POST_AWARD_SERVICE')")
    @GetMapping("/{competitionId}/post-award-service")
    public String setupPostAwardService(@PathVariable(COMPETITION_ID_KEY) long competitionId, Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionId;
        }

        PostAwardServiceForm form = new PostAwardServiceForm();
        form.setPostAwardService(competition.getPostAwardService());

        model.addAttribute(MODEL, choosePostAwardServiceModelPopulator.populateModel(competition));
        model.addAttribute(FORM_ATTR_NAME, form);

        return "competition/setup/post-award-service";
    }

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'CHOOSE_POST_AWARD_SERVICE')")
    @PostMapping(value = "/{competitionId}/post-award-service")
    public String configurePostAwardService(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                    @RequestParam("service") PostAwardService postAwardService) {

        competitionSetupPostAwardServiceRestService.setPostAwardService(competitionId, postAwardService);
        return "redirect:/competition/setup/" + competitionId;
    }

}
