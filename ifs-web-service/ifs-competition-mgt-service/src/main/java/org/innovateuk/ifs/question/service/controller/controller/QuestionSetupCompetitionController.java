package org.innovateuk.ifs.question.service.controller.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.QuestionSetupRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.application.DeleteQuestionForm;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.service.populator.CompetitionSetupPopulator;
import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupSubsectionViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.QuestionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.innovateuk.ifs.question.service.controller.service.QuestionSetupCompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupApplicationController.APPLICATION_LANDING_REDIRECT;
import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_ID_KEY;
import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

public class QuestionSetupCompetitionController {

    private static final Log LOG = LogFactory.getLog(QuestionSetupCompetitionController.class);
    private static final String questionView = "competition/setup/question";
    private static final String MODEL = "model";

    @Autowired
    private QuestionSetupCompetitionService questionSetupCompetitionService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionSetupPopulator competitionSetupPopulator;

    @Autowired
    private QuestionSetupRestService questionSetupRestService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @PostMapping(value = "/landing-page", params = "deleteQuestion")
    public String deleteQuestion(@ModelAttribute("deleteQuestion") DeleteQuestionForm deleteQuestionForm,
                                 @PathVariable(COMPETITION_ID_KEY) long competitionId) {
        questionSetupCompetitionService.deleteQuestion(deleteQuestionForm.getDeleteQuestion());

        Supplier<String> view = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        return view.get();
    }

    @PostMapping(value = "/landing-page", params = "createQuestion")
    public String createQuestion(@PathVariable(COMPETITION_ID_KEY) long competitionId) {
        ServiceResult<CompetitionSetupQuestionResource> restResult = questionSetupCompetitionService.createDefaultQuestion(competitionId);

        Function<CompetitionSetupQuestionResource, String> successViewFunction =
                (question) -> String.format("redirect:/competition/setup/%d/section/application/question/%d/edit", competitionId, question.getQuestionId());
        Supplier<String> successView = () -> successViewFunction.apply(restResult.getSuccess());

        return successView.get();
    }

    @GetMapping("/question/{questionId}")
    public String seeQuestionInCompSetup(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                         @PathVariable("questionId") Long questionId,
                                         Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);

        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        return getQuestionPage(model, competitionResource, questionId, false, null);
    }

    @GetMapping("/question/{questionId}/edit")
    public String editQuestionInCompSetup(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                          @PathVariable("questionId") Long questionId,
                                          Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        return ifUserCanAccessEditPageMarkSectionAsIncomplete(competitionResource,
                () -> getQuestionPage(model, competitionResource, questionId, true, null),
                Optional.empty(),
                Optional.ofNullable(questionId));
    }

    private String getQuestionPage(Model model, CompetitionResource competitionResource, Long questionId, boolean isEditable, CompetitionSetupForm form) {
        ServiceResult<String> view = questionSetupCompetitionService.getQuestion(questionId).andOnSuccessReturn(
                questionResource -> {
                    CompetitionSetupQuestionType type = questionResource.getType();
                    CompetitionSetupSubsection setupSubsection;

                    if (type.equals(CompetitionSetupQuestionType.ASSESSED_QUESTION)) {
                        setupSubsection = CompetitionSetupSubsection.QUESTIONS;
                    } else {
                        setupSubsection = CompetitionSetupSubsection.PROJECT_DETAILS;
                    }

                    model.addAttribute(MODEL, setupQuestionViewModel(competitionResource, Optional.of(questionId), setupSubsection, isEditable));
                    model.addAttribute(COMPETITION_SETUP_FORM_KEY, setupQuestionForm(competitionResource, Optional.of(questionId), setupSubsection, form));

                    return questionView;
                }).andOnFailure(() -> serviceSuccess("redirect:/non-ifs-competition/setup/" + questionId));

        return view.getSuccess();
    }

    private QuestionSetupViewModel setupQuestionViewModel(final CompetitionResource competition, final Optional<Long> questionId, CompetitionSetupSubsection subsection, boolean isEditable) {
        CompetitionSetupSection section = APPLICATION_FORM;

        CompetitionSetupSubsectionViewModel subsectionViewModel = competitionSetupService.populateCompetitionSubsectionModelAttributes(competition, section,
                subsection, questionId);
        GeneralSetupViewModel generalViewModel = competitionSetupPopulator.populateGeneralModelAttributes(competition, section);

        return new QuestionSetupViewModel(generalViewModel, subsectionViewModel, competition.getName(), isEditable);
    }

    private CompetitionSetupForm setupQuestionForm(final CompetitionResource competition, final Optional<Long> questionId, CompetitionSetupSubsection subsection, CompetitionSetupForm competitionSetupForm) {
        CompetitionSetupSection section = APPLICATION_FORM;

        if (competitionSetupForm == null) {
            competitionSetupForm = competitionSetupService.getSubsectionFormData(
                    competition,
                    section,
                    subsection,
                    questionId);
        }
        return competitionSetupForm;
    }

    private String ifUserCanAccessEditPageMarkSectionAsIncomplete(CompetitionResource competition, Supplier<String> successAction,
                                                                  Optional<CompetitionSetupSubsection> subsectionOpt,
                                                                  Optional<Long> questionIdOpt) {
        if(CompetitionSetupSection.APPLICATION_FORM.preventEdit(competition)) {
            LOG.error(String.format("Competition with id %1$d cannot edit section %2$s: ", competition.getId(), CompetitionSetupSection.APPLICATION_FORM));
            return "redirect:/dashboard";
        } else {
            questionIdOpt.ifPresent(questionId -> questionSetupRestService.markQuestionSetupIncomplete(competition.getId(), CompetitionSetupSection.APPLICATION_FORM, questionId));
            subsectionOpt.ifPresent(competitionSetupSubsection -> competitionSetupRestService.markSubSectionIncomplete(competition.getId(), CompetitionSetupSection.APPLICATION_FORM, competitionSetupSubsection));
            competitionSetupRestService.markSectionIncomplete(competition.getId(), CompetitionSetupSection.APPLICATION_FORM);
            return successAction.get();
        }
    }

}
