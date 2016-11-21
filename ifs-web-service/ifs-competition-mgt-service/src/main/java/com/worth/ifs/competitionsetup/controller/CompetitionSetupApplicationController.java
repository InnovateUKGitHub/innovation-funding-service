package com.worth.ifs.competitionsetup.controller;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
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

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;
import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;
import static com.worth.ifs.competitionsetup.utils.CompetitionUtils.isSendToDashboard;

/**
 * Controller to manage the Application Questions and it's sub-sections in the
 * competition setup process
 */
@Controller
@RequestMapping("/competition/setup/{competitionId}/section/application")
public class CompetitionSetupApplicationController {

    private static final Log LOG = LogFactory.getLog(CompetitionSetupApplicationController.class);
    public static final String APPLICATION_LANDING_REDIRECT = "redirect:/competition/setup/%d/section/application/landing-page";
    private static final String questionView = "competition/setup/question";

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupQuestionService competitionSetupQuestionService;

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
    public String seeApplicationFinances(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         Model model) {
        String view = getFinancePage(model, competitionId);
        model.addAttribute("editable", false);
        return view;
    }


    @RequestMapping(value = "/question/finance/edit", method = RequestMethod.GET)
    public String editApplicationFinances(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         Model model) {
        String view = getFinancePage(model, competitionId);
        model.addAttribute("editable", true);
        return view;
    }

    @RequestMapping(value = "/question/finance/edit", method = RequestMethod.POST)
    public String submitApplicationFinances(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationFinanceForm form,
                                            BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {

        Supplier<String> failureView = () -> getFinancePage(model, competitionId);
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        CompetitionResource resource = competitionService.getById(competitionId);
        resource.setFullApplicationFinance(form.isFullApplicationFinance());
        resource.setIncludeGrowthTable(form.isIncludeGrowthTable());
        competitionService.update(resource);

        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    private String getFinancePage(Model model, Long competitionId) {
        competitionSetupService.populateCompetitionSubsectionModelAttributes(model, competitionService.getById(competitionId),
                CompetitionSetupSection.APPLICATION_FORM, CompetitionSetupSubsection.FINANCES, Optional.empty());
        return "competition/finances";
    }

    @RequestMapping(value = "/question/{questionId}", method = RequestMethod.GET)
    public String seeQuestionInCompSetup(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         @PathVariable("questionId") Long questionId,
                                         Model model) {

        CompetitionResource competition = competitionService.getById(competitionId);

        if(isSendToDashboard(competition)) {
            LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
        }

        setupQuestionToModel(competition, questionId, model);
        model.addAttribute("editable", false);

        return questionView;
    }

    @RequestMapping(value = "/question/{questionId}/edit", method = RequestMethod.GET)
    public String editQuestionInCompSetup(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                          @PathVariable("questionId") Long questionId,
                                          Model model) {

        CompetitionResource competition = competitionService.getById(competitionId);

        if(isSendToDashboard(competition)) {
            LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
        }

        setupQuestionToModel(competition, questionId, model);
        model.addAttribute("editable", true);

        return questionView;
    }

    @RequestMapping(value = "/question", method = RequestMethod.POST)
    public String submitApplicationQuestion(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationQuestionForm competitionSetupForm,
                                            BindingResult bindingResult,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {

        competitionSetupQuestionService.updateQuestion(competitionSetupForm.getQuestion());

        if(!bindingResult.hasErrors()) {
            return "redirect:/competition/setup/" + competitionId + "/section/application";
        } else {
            competitionSetupService.populateCompetitionSubsectionModelAttributes(model,
                    competitionService.getById(competitionId), CompetitionSetupSection.APPLICATION_FORM, CompetitionSetupSubsection.QUESTIONS,
                    Optional.of(competitionSetupForm.getQuestion().getId()));
            model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupForm);
            return questionView;
        }
    }

    private void setupQuestionToModel(final CompetitionResource competition, final Long questionId, Model model) {
        CompetitionSetupSection section = CompetitionSetupSection.APPLICATION_FORM;

        competitionSetupService.populateCompetitionSubsectionModelAttributes(model, competition, section,
                CompetitionSetupSubsection.QUESTIONS, Optional.of(questionId));
        ApplicationQuestionForm competitionSetupForm =
                (ApplicationQuestionForm) competitionSetupService.getSubsectionFormData(
                        competition,
                        section,
                        CompetitionSetupSubsection.QUESTIONS,
                        Optional.of(questionId));

        model.addAttribute("competitionName", competition.getName());
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupForm);
    }
}
