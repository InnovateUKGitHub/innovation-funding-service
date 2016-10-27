package com.worth.ifs.competitionsetup.controller;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competitionsetup.form.FinanceForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupService;
import com.worth.ifs.controller.ValidationHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.function.Supplier;

import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;
import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

/**
 * Controller to manage the Application Questions and it's sub-sections in the
 * competition setup process
 */
@Controller
@RequestMapping("/competition/setup/{competitionId}/section/application")
public class CompetitionSetupApplicationController {

    public static final String APPLICATION_LANDING_REDIRECT = "redirect:/competition/setup/%d/section/application/landing-page";

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionService competitionService;

    @RequestMapping(value = "/landing-page", method = RequestMethod.GET)
    public String applicationProcessLandingPage(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        competitionSetupService.populateCompetitionSectionModelAttributes(model, competitionResource, CompetitionSetupSection.APPLICATION_FORM);
        return "competition/setup";
    }

    @RequestMapping(value = "/mark-as-complete", method = RequestMethod.GET)
    public String setApplicationProcessAsComplete(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId) {
        competitionService.setSetupSectionMarkedAsComplete(competitionId, CompetitionSetupSection.APPLICATION_FORM);
        return String.format(APPLICATION_LANDING_REDIRECT, competitionId);
    }

    @RequestMapping(value = "/question/finance", method = RequestMethod.GET)
    public String getApplicationFinances(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         Model model) {
        CompetitionResource resource = competitionService.getById(competitionId);
        FinanceForm form = new FinanceForm();
        form.setFullApplicationFinance(resource.isFullApplicationFinance());
        form.setIncludeGrowthTable(resource.isIncludeGrowthTable());
        return getFinancePage(model, form, competitionId);
    }

    @RequestMapping(value = "/question/finance", method = RequestMethod.POST)
    public String submitApplicationFinances(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) FinanceForm form,
                                            BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {

        Supplier<String> failureView = () -> getFinancePage(model, form, competitionId);
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);
        CompetitionResource resource = competitionService.getById(competitionId);
        resource.setFullApplicationFinance(form.isFullApplicationFinance());
        resource.setIncludeGrowthTable(form.isIncludeGrowthTable());
        competitionService.update(resource);
        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    private String getFinancePage(Model model, FinanceForm form, Long competitionId) {
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, form);
        model.addAttribute(COMPETITION_ID_KEY, competitionId);
        return "competition/finances";
    }
}
