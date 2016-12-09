package com.worth.ifs.competitionsetup.controller;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.LandingPageForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import com.worth.ifs.competitionsetup.form.application.ApplicationProjectForm;
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

import static com.worth.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static com.worth.ifs.competition.resource.CompetitionSetupSubsection.*;
import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.*;
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
        competitionSetupService.populateCompetitionSectionModelAttributes(model, competitionResource, APPLICATION_FORM);
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, new LandingPageForm());
        return "competition/setup";
    }

    @RequestMapping(value = "/landing-page", method = RequestMethod.POST)
    public String setApplicationProcessAsComplete(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                                  @ModelAttribute(COMPETITION_SETUP_FORM_KEY) LandingPageForm form, BindingResult bindingResult, ValidationHandler validationHandler) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);

        Supplier<String> failureView = () -> {
            competitionSetupService.populateCompetitionSectionModelAttributes(model, competitionResource, APPLICATION_FORM);
            model.addAttribute(COMPETITION_SETUP_FORM_KEY, form);
            return "competition/setup";
        };
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView, () ->
                competitionSetupQuestionService.validateApplicationQuestions(competitionResource, form, bindingResult));
    }

    @RequestMapping(value = "/question/finance", method = RequestMethod.GET)
    public String seeApplicationFinances(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        return getFinancePage(model, competitionResource, false, null);
    }


    @RequestMapping(value = "/question/finance/edit", method = RequestMethod.GET)
    public String editApplicationFinances(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        return getFinancePage(model, competitionResource, true, null);
    }

    @RequestMapping(value = "/question/finance/edit", method = RequestMethod.POST)
    public String submitApplicationFinances(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationFinanceForm form,
                                            BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {

        CompetitionResource competitionResource = competitionService.getById(competitionId);
        Supplier<String> failureView = () -> getFinancePage(model, competitionResource, true, form);
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> competitionSetupService.saveCompetitionSetupSubsection(form, competitionResource, APPLICATION_FORM, FINANCES));

    }

    @RequestMapping(value = "/question/{questionId}", method = RequestMethod.GET)
    public String seeQuestionInCompSetup(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         @PathVariable("questionId") Long questionId,
                                         Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        return getQuestionPage(model, competitionResource, questionId, false, null);

    }

    @RequestMapping(value = "/question/{questionId}/edit", method = RequestMethod.GET)
    public String editQuestionInCompSetup(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                          @PathVariable("questionId") Long questionId,
                                          Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        return getQuestionPage(model, competitionResource, questionId, true, null);

    }

    @RequestMapping(value = "/question", method = RequestMethod.POST, params = "question.type=ASSESSED_QUESTION")
    public String submitAssessedQuestion(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationQuestionForm competitionSetupForm,
                                            BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        Supplier<String> failureView = () -> getQuestionPage(model, competitionResource, competitionSetupForm.getQuestion().getQuestionId(), true, competitionSetupForm);
        Supplier<String> successView = () -> "redirect:/competition/setup/" + competitionId + "/section/application";

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> competitionSetupService.saveCompetitionSetupSubsection(competitionSetupForm, competitionResource, APPLICATION_FORM, QUESTIONS));
    }

    @RequestMapping(value = "/question", method = RequestMethod.POST)
    public String submitProjectDetailsQuestion(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationProjectForm competitionSetupForm,
                                            BindingResult bindingResult,
                                               ValidationHandler validationHandler,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {

        CompetitionResource competitionResource = competitionService.getById(competitionId);
        Supplier<String> failureView = () -> getQuestionPage(model, competitionResource, competitionSetupForm.getQuestion().getQuestionId(), true, competitionSetupForm);
        Supplier<String> successView = () -> "redirect:/competition/setup/" + competitionId + "/section/application";

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> competitionSetupService.saveCompetitionSetupSubsection(competitionSetupForm, competitionResource, APPLICATION_FORM, PROJECT_DETAILS));


    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String viewApplicationDetails(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                        Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        return getDetailsPage(model, competitionResource, false, null);
    }

    @RequestMapping(value = "/detail/edit", method = RequestMethod.GET)
    public String getEditApplicationDetails(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        return getDetailsPage(model, competitionResource, true, null);
    }

    @RequestMapping(value = "/detail/edit", method = RequestMethod.POST)
    public String submitApplicationDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationDetailsForm form,
                                            BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        Supplier<String> failureView = () -> getDetailsPage(model, competitionResource, true, form);
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> competitionSetupService.saveCompetitionSetupSubsection(form, competitionResource, APPLICATION_FORM, APPLICATION_DETAILS));

    }

    private String getFinancePage(Model model, CompetitionResource competitionResource, boolean isEditable, CompetitionSetupForm form) {
        setupQuestionToModel(competitionResource, Optional.empty(), model, FINANCES, isEditable, form);
        return "competition/finances";
    }

    private String getDetailsPage(Model model, CompetitionResource competitionResource, boolean isEditable, CompetitionSetupForm form) {
        setupQuestionToModel(competitionResource, Optional.empty(), model, APPLICATION_DETAILS, isEditable, form);
        return "competition/application-details";
    }

    private String getQuestionPage(Model model, CompetitionResource competitionResource, Long questionId, boolean isEditable, CompetitionSetupForm form) {
        if(isSendToDashboard(competitionResource)) {
            LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
        }

        ServiceResult<CompetitionSetupQuestionResource> questionResource = competitionSetupQuestionService.getQuestion(questionId);

        CompetitionSetupQuestionType type = questionResource.getSuccessObjectOrThrowException().getType();
        CompetitionSetupSubsection setupSubsection;

        if (type.equals(CompetitionSetupQuestionType.ASSESSED_QUESTION)) {
            setupSubsection =  CompetitionSetupSubsection.QUESTIONS;
        } else {
            setupSubsection =  CompetitionSetupSubsection.PROJECT_DETAILS;
        }

        setupQuestionToModel(competitionResource, Optional.of(questionId), model, setupSubsection, isEditable, form);

        return questionView;
    }


    private void setupQuestionToModel(final CompetitionResource competition, final Optional<Long> questionId, Model model, CompetitionSetupSubsection subsection, boolean isEditable, CompetitionSetupForm form) {
        CompetitionSetupSection section = APPLICATION_FORM;

        competitionSetupService.populateCompetitionSubsectionModelAttributes(model, competition, section,
                subsection, questionId);

        CompetitionSetupForm competitionSetupForm = form;
        if (form == null) {
            competitionSetupForm = competitionSetupService.getSubsectionFormData(
                    competition,
                    section,
                    subsection,
                    questionId);
        }

        model.addAttribute(COMPETITION_NAME_KEY, competition.getName());
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupForm);
        model.addAttribute("editable", isEditable);
    }
}
