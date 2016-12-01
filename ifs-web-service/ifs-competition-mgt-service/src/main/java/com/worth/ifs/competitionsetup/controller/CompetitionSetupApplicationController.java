package com.worth.ifs.competitionsetup.controller;

import com.worth.ifs.application.resource.QuestionResource;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.resource.*;
import com.worth.ifs.competitionsetup.form.CompetitionSetupForm;
import com.worth.ifs.competitionsetup.form.application.*;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import com.worth.ifs.competitionsetup.service.CompetitionSetupService;
import com.worth.ifs.competitionsetup.viewmodel.GuidanceRowViewModel;
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
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;
import static com.worth.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_NAME_KEY;
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

    @RequestMapping(value = "/question", method = RequestMethod.POST, params = "ASSESSED_QUESTION")
    public String submitAssessedQuestion(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationQuestionForm competitionSetupForm,
                                            BindingResult bindingResult,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {

        if(!bindingResult.hasErrors()) {

            competitionSetupForm.getQuestion().getGuidanceRows().clear();

            competitionSetupForm.getGuidanceRows().forEach(guidanceRowViewModel -> {
                GuidanceRowResource grr = new GuidanceRowResource();
                grr.setSubject(guidanceRowViewModel.getScoreFrom() + ":" + guidanceRowViewModel.getScoreTo());
                grr.setJustification(guidanceRowViewModel.getJustification());
                competitionSetupForm.getQuestion().getGuidanceRows().add(grr);
            });

            competitionSetupQuestionService.updateQuestion(competitionSetupForm.getQuestion());
            return "redirect:/competition/setup/" + competitionId + "/section/application";
        } else {
            competitionSetupService.populateCompetitionSubsectionModelAttributes(model,
                    competitionService.getById(competitionId), CompetitionSetupSection.APPLICATION_FORM, CompetitionSetupSubsection.QUESTIONS,
                    Optional.of(competitionSetupForm.getQuestion().getQuestionId()));

            competitionSetupForm.getQuestion().setType(CompetitionSetupQuestionType.ASSESSED_QUESTION);
            model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupForm);
            return questionView;
        }
    }

    @RequestMapping(value = "/question", method = RequestMethod.POST, params = "SCOPE")
    public String submitScopeQuestion(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationProjectForm competitionSetupForm,
                                            BindingResult bindingResult,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {

        if(!bindingResult.hasErrors()) {
            competitionSetupQuestionService.updateQuestion(competitionSetupForm.getQuestion());
            return "redirect:/competition/setup/" + competitionId + "/section/application";
        } else {
            competitionSetupService.populateCompetitionSubsectionModelAttributes(model,
                    competitionService.getById(competitionId), CompetitionSetupSection.APPLICATION_FORM, CompetitionSetupSubsection.PROJECT_DETAILS,
                    Optional.of(competitionSetupForm.getQuestion().getQuestionId()));

            competitionSetupForm.getQuestion().setType(CompetitionSetupQuestionType.SCOPE);
            model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupForm);
            return questionView;
        }
    }

    // TODO - treat the following 2 types as per a scoped question until further analysis has been done

    @RequestMapping(value = "/question", method = RequestMethod.POST, params = "PUBLIC_DESCRIPTION")
    public String submitPublicDescriptionQuestion(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationProjectForm competitionSetupForm,
                                                  BindingResult bindingResult,
                                                  @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                                  Model model) {

        return submitScopeQuestion(competitionSetupForm, bindingResult, competitionId, model);
    }

    @RequestMapping(value = "/question", method = RequestMethod.POST, params = "PROJECT_SUMMARY")
    public String submitProjectSummary(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationProjectForm competitionSetupForm,
                                                  BindingResult bindingResult,
                                                  @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                                  Model model) {

        return submitScopeQuestion(competitionSetupForm, bindingResult, competitionId, model);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String viewApplicationDetails(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                        Model model) {

        return getDetailsPage(model, competitionId, false);
    }

    @RequestMapping(value = "/detail/edit", method = RequestMethod.GET)
    public String getEditApplicationDetails(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                         Model model) {
        return getDetailsPage(model, competitionId, true);
    }

    @RequestMapping(value = "/detail/edit", method = RequestMethod.POST)
    public String submitApplicationDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationDetailsForm form,
                                            BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                            Model model) {

        Supplier<String> failureView = () -> getDetailsPage(model, competitionId, true);
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        CompetitionResource resource = competitionService.getById(competitionId);
        resource.setUseResubmissionQuestion(form.isUseResubmissionQuestion());
        competitionService.update(resource);

        return validationHandler.failNowOrSucceedWith(failureView, successView);
    }

    private String getDetailsPage(Model model, Long competitionId, boolean isEditable) {
        CompetitionResource competition = competitionService.getById(competitionId);
        competitionSetupService.populateCompetitionSubsectionModelAttributes(model, competition,
                CompetitionSetupSection.APPLICATION_FORM, CompetitionSetupSubsection.APPLICATION_DETAILS, Optional.empty());

        ApplicationDetailsForm competitionSetupForm = (ApplicationDetailsForm) competitionSetupService.getSubsectionFormData(
                competition,
                CompetitionSetupSection.APPLICATION_FORM,
                CompetitionSetupSubsection.APPLICATION_DETAILS,
                null);

        model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupForm);
        model.addAttribute("editable", isEditable);
        return "competition/application-details";
    }

    private void setupQuestionToModel(final CompetitionResource competition, final Long questionId, Model model) {

        ServiceResult<CompetitionSetupQuestionResource> questionResource = competitionSetupQuestionService.getQuestion(questionId);

        CompetitionSetupQuestionType type = questionResource.getSuccessObject().getType();

        CompetitionSetupSection section = CompetitionSetupSection.APPLICATION_FORM;
        CompetitionSetupForm competitionSetupForm;
        CompetitionSetupSubsection setupSubsection;

        if (type.equals(CompetitionSetupQuestionType.ASSESSED_QUESTION)) {
            setupSubsection =  CompetitionSetupSubsection.QUESTIONS;

        } else {
            setupSubsection =  CompetitionSetupSubsection.PROJECT_DETAILS;
        }

        competitionSetupService.populateCompetitionSubsectionModelAttributes(model, competition, section,
                setupSubsection, Optional.of(questionId));

        competitionSetupForm =
                competitionSetupService.getSubsectionFormData(
                        competition,
                        section,
                        setupSubsection,
                        Optional.of(questionId));

        model.addAttribute(COMPETITION_NAME_KEY, competition.getName());
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupForm);
    }
}
