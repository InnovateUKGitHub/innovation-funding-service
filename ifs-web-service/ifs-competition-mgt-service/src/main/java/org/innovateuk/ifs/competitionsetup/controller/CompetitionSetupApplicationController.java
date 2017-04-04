
package org.innovateuk.ifs.competitionsetup.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.GuidanceRowForm;
import org.innovateuk.ifs.competitionsetup.form.LandingPageForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationDetailsForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationFinanceForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationProjectForm;
import org.innovateuk.ifs.competitionsetup.form.application.ApplicationQuestionForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.*;
import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.*;

/**
 * Controller to manage the Application Questions and it's sub-sections in the
 * competition setup process
 */
@Controller
@RequestMapping("/competition/setup/{competitionId}/section/application")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
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

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @GetMapping("/landing-page")
    public String applicationProcessLandingPage(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        competitionSetupService.populateCompetitionSectionModelAttributes(model, competitionResource, APPLICATION_FORM);
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, new LandingPageForm());
        return "competition/setup";
    }

    @PostMapping("/landing-page")
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

    @GetMapping("/question/finance")
    public String seeApplicationFinances(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        return getFinancePage(model, competitionResource, false, null);
    }


    @GetMapping("/question/finance/edit")
    public String editApplicationFinances(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                          Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        return ifUserCanAccessEditPage(competitionResource, () -> getFinancePage(model, competitionResource, true, null));
    }

    @PostMapping("/question/finance/edit")
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

    @GetMapping("/question/{questionId}")
    public String seeQuestionInCompSetup(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         @PathVariable("questionId") Long questionId,
                                         Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        return getQuestionPage(model, competitionResource, questionId, false, null);
    }

    @GetMapping("/question/{questionId}/edit")
    public String editQuestionInCompSetup(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                          @PathVariable("questionId") Long questionId,
                                          Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        return ifUserCanAccessEditPage(competitionResource, () -> getQuestionPage(model, competitionResource, questionId, true, null));
    }

    @PostMapping(value = "/question/{questionId}/edit", params = "question.type=ASSESSED_QUESTION")
    public String submitAssessedQuestion(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationQuestionForm competitionSetupForm,
                                            BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {
        validateAssessmentGuidanceRows(competitionSetupForm, bindingResult);

        CompetitionResource competitionResource = competitionService.getById(competitionId);
        Supplier<String> failureView = () -> getQuestionPage(model, competitionResource, competitionSetupForm.getQuestion().getQuestionId(), true, competitionSetupForm);
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> competitionSetupService.saveCompetitionSetupSubsection(competitionSetupForm, competitionResource, APPLICATION_FORM, QUESTIONS));
    }

    @PostMapping("/question/{questionId}/edit")
    public String submitProjectDetailsQuestion(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationProjectForm competitionSetupForm,
                                               BindingResult bindingResult,
                                               ValidationHandler validationHandler,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {
        validateScopeGuidanceRows(competitionSetupForm, bindingResult);


        CompetitionResource competitionResource = competitionService.getById(competitionId);
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        Supplier<String> failureView = () -> getQuestionPage(model, competitionResource, competitionSetupForm.getQuestion().getQuestionId(), true, competitionSetupForm);
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> competitionSetupService.saveCompetitionSetupSubsection(competitionSetupForm, competitionResource, APPLICATION_FORM, PROJECT_DETAILS));
    }

    @GetMapping(value = "/detail")
    public String viewApplicationDetails(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        return getDetailsPage(model, competitionResource, false, null);
    }

    @GetMapping(value = "/detail/edit")
    public String getEditApplicationDetails(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        return ifUserCanAccessEditPage(competitionResource, () ->  getDetailsPage(model, competitionResource, true, null));

    }

    @PostMapping("/detail/edit")
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

    private void validateAssessmentGuidanceRows(ApplicationQuestionForm applicationQuestionForm, BindingResult bindingResult) {
        if (Boolean.TRUE.equals(applicationQuestionForm.getQuestion().getWrittenFeedback())) {
            ValidationUtils.invokeValidator(validator, applicationQuestionForm, bindingResult, GuidanceRowForm.GuidanceRowViewGroup.class);
        }
    }

    private void validateScopeGuidanceRows(ApplicationProjectForm applicationProjectForm, BindingResult bindingResult) {
        if (Boolean.TRUE.equals(applicationProjectForm.getQuestion().getWrittenFeedback())) {
            ValidationUtils.invokeValidator(validator, applicationProjectForm, bindingResult, GuidanceRowResource.GuidanceRowGroup.class);
        }
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
        ServiceResult<CompetitionSetupQuestionResource> questionResource = competitionSetupQuestionService.getQuestion(questionId);

        CompetitionSetupQuestionType type = questionResource.getSuccessObjectOrThrowException().getType();
        CompetitionSetupSubsection setupSubsection;

        if (type.equals(CompetitionSetupQuestionType.ASSESSED_QUESTION)) {
            setupSubsection = CompetitionSetupSubsection.QUESTIONS;
        } else {
            setupSubsection = CompetitionSetupSubsection.PROJECT_DETAILS;
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

    private String ifUserCanAccessEditPage(CompetitionResource competition, Supplier<String> successAction) {
        if(CompetitionSetupSection.APPLICATION_FORM.preventEdit(competition)) {
            LOG.error(String.format("Competition with id %1$d cannot edit section %2$s: ", competition.getId(), CompetitionSetupSection.APPLICATION_FORM));
            return "redirect:/dashboard";
        } else {
            return successAction.get();
        }
    }
}
