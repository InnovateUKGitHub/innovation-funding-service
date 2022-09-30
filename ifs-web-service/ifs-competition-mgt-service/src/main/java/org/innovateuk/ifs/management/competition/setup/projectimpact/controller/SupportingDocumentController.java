package org.innovateuk.ifs.management.competition.setup.projectimpact.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.projectimpact.form.ProjectImpactForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.PROJECT_IMPACT;
import static org.innovateuk.ifs.management.competition.setup.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

/**
 * Controller to manage the Organisational eligibility and Lead organisations in competition setup process
 */
@Controller
@RequestMapping("/competition/setup/{competitionId}/section/project-impact")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = SupportingDocumentController.class)
@PreAuthorize("hasAnyAuthority('comp_admin')")
public class SupportingDocumentController {


    private static final String MODEL = "model";

    private final CompetitionRestService competitionRestService;

    private final CompetitionSetupService competitionSetupService;

    @Autowired
    public SupportingDocumentController(CompetitionRestService competitionRestService, CompetitionSetupService competitionSetupService) {
        this.competitionRestService = competitionRestService;
        this.competitionSetupService = competitionSetupService;
    }


    @PostMapping
    public String submitOrganisationalEligibilitySectionDetails(@Valid @ModelAttribute("competitionSetupForm") ProjectImpactForm projectImpactForm,
                                                                BindingResult bindingResult,
                                                                ValidationHandler validationHandler,
                                                                @PathVariable("competitionId") long competitionId,
                                                                UserResource loggedInUser,
                                                                Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        Supplier<String> failureView = () -> {
            model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, PROJECT_IMPACT));
            model.addAttribute(COMPETITION_SETUP_FORM_KEY, projectImpactForm);
            return "competition/setup";
        };
        return validationHandler.failNowOrSucceedWith(failureView, null);
    }

}