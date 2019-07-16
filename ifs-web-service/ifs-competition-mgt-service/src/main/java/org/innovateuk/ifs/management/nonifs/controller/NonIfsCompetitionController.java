package org.innovateuk.ifs.management.nonifs.controller;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.management.nonifs.form.NonIfsDetailsForm;
import org.innovateuk.ifs.management.nonifs.formpopulator.NonIfsDetailsFormPopulator;
import org.innovateuk.ifs.management.nonifs.modelpopulator.NonIfsDetailsViewModelPopulator;
import org.innovateuk.ifs.management.nonifs.saver.NonIfsDetailsFormSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Controller for all Non-IFS competition actions.
 */
@Controller
@SecuredBySpring(value = "Controller", description = "TODO", securedType = NonIfsCompetitionController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class NonIfsCompetitionController {
    private static final String FORM_ATTR = "form";

    @Autowired
    private NonIfsDetailsFormSaver nonIfsDetailsFormSaver;
    @Autowired
    private NonIfsDetailsFormPopulator nonIfsDetailsFormPopulator;
    @Autowired
    private NonIfsDetailsViewModelPopulator nonIfsDetailsViewModelPopulator;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @GetMapping("/non-ifs-competition/create")
    public String create() {
        CompetitionResource competition = competitionSetupRestService.createNonIfs()
                .getSuccess();
        return String.format("redirect:/non-ifs-competition/setup/%s", competition.getId());
    }

    @GetMapping("/non-ifs-competition/setup/{competitionId}")
    public String details(Model model, @PathVariable("competitionId") Long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if (!competition.isNonIfs()) {
            return "redirect:/competition/setup/" + competitionId;
        }
        return getDetailsPage(model, competition, Optional.empty());
    }


    @PostMapping("/non-ifs-competition/setup/{competitionId}")
    public String save(Model model,@Valid @ModelAttribute(FORM_ATTR) NonIfsDetailsForm form,
                       BindingResult bindingResult, ValidationHandler validationHandler,
                       @PathVariable("competitionId") Long competitionId) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        Supplier<String> failureView = () -> getDetailsPage(model, competition, Optional.of(form));
        Supplier<String> successView = () -> "redirect:/competition/setup/public-content/" + competitionId;

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> nonIfsDetailsFormSaver.save(form, competition));
    }

    private String getDetailsPage(Model model, CompetitionResource competition, Optional<NonIfsDetailsForm> form) {
        if(form.isPresent()) {
            model.addAttribute(FORM_ATTR, form.get());
        } else {
            model.addAttribute(FORM_ATTR, nonIfsDetailsFormPopulator.populate(competition));
        }
        model.addAttribute("model", nonIfsDetailsViewModelPopulator.populate(competition));
        return "competition/non-ifs-details";
    }

}
