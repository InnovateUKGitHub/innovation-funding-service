package org.innovateuk.ifs.management.competition.setup.projectimpact.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static java.lang.String.format;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_IMPACT;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.management.competition.setup.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

/**
 * Controller to manage the Organisational eligibility and Lead organisations in competition setup process
 */
@Controller
@RequestMapping("/competition/setup/{competitionId}/section/supporting-document")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = SupportingDocumentController.class)
@PreAuthorize("hasAnyAuthority('comp_admin')")
public class SupportingDocumentController {


    private static final String MODEL = "model";

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupService competitionSetupService;



    @GetMapping
    public String organisationalEligibilityPageDetails(Model model,
                                                       @PathVariable long competitionId,
                                                       UserResource loggedInUser) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, PROJECT_IMPACT));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupService.getSectionFormPopulator(PROJECT_IMPACT).populateForm(competition));

        return "competition/setup";
    }


}