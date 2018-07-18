package org.innovateuk.ifs.competitionsetup.documents.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.application.controller.CompetitionSetupApplicationController;
import org.innovateuk.ifs.competitionsetup.application.form.LandingPageForm;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.DOCUMENTS;
import static org.innovateuk.ifs.competitionsetup.CompetitionSetupController.COMPETITION_ID_KEY;

import static org.innovateuk.ifs.competitionsetup.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

/**
 * Controller to manage the Document section and it's sub-sections in the
 * competition setup process
 */


@Controller
@RequestMapping("/competition/setup/{competitionId}/section/documents")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionSetupDocumentsController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionSetupDocumentsController {
    private static final Log LOG = LogFactory.getLog(CompetitionSetupDocumentsController.class);
    public static final String DOCUMENTS_LANDING_REDIRECT = "redirect:/competition/setup/%d/section/documents/landing-page";
    private static final String MODEL = "model";

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @GetMapping("/landing-page")
    public String documentsLandingPage(Model model, @PathVariable(COMPETITION_ID_KEY) long competitionId) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);

        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, DOCUMENTS));
        //model.addAttribute(COMPETITION_SETUP_FORM_KEY, new LandingPageForm());
        return "competition/setup";
    }
}
