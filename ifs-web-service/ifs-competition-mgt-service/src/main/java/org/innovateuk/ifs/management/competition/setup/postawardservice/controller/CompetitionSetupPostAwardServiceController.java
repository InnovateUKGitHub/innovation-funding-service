package org.innovateuk.ifs.management.competition.setup.postawardservice.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionPostAwardServiceResource;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionId;
        }

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionPostAwardServiceResource competitionPostAwardService = competitionSetupPostAwardServiceRestService.getPostAwardService(competitionId).getSuccess();

        PostAwardServiceForm form = new PostAwardServiceForm();
        form.setPostAwardService(competitionPostAwardService.getPostAwardService());

        return populateModel(model, competition, form);
    }

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'CHOOSE_POST_AWARD_SERVICE')")
    @PostMapping(value = "/{competitionId}/post-award-service")
    public String configurePostAwardService(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                            @Valid @ModelAttribute(FORM_ATTR_NAME) PostAwardServiceForm form,
                                            BindingResult bindingResult,
                                            Model model) {

        if (bindingResult.hasErrors()) {
            CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
            return populateModel(model, competition, form);
        }
        competitionSetupPostAwardServiceRestService.setPostAwardService(competitionId, form.getPostAwardService());
        return "redirect:/competition/setup/" + competitionId;
    }

    private String populateModel(Model model, CompetitionResource competition, PostAwardServiceForm form) {
        model.addAttribute(MODEL, choosePostAwardServiceModelPopulator.populateModel(competition));
        model.addAttribute(FORM_ATTR_NAME, form);

        return "competition/setup/post-award-service";
    }

}
