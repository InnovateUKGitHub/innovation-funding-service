
package org.innovateuk.ifs.management.competition.setup.application.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.QuestionSetupRestService;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.management.competition.setup.application.form.*;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupPopulator;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupQuestionService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupSubsectionViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.QuestionSetupViewModel;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.question.service.QuestionSetupCompetitionRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection.*;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.file.controller.FileDownloadControllerUtils.getFileResponseEntity;
import static org.innovateuk.ifs.management.competition.setup.CompetitionSetupController.COMPETITION_SETUP_FORM_KEY;

/**
 * Controller to manage the Application Questions and it's sub-sections in the
 * competition setup process
 */
@Controller
@RequestMapping("/competition/setup/{competitionId}/section/application")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionSetupApplicationController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionSetupApplicationController {

    private static final Log LOG = LogFactory.getLog(CompetitionSetupApplicationController.class);
    public static final String APPLICATION_LANDING_REDIRECT = "redirect:/competition/setup/%d/section/application/landing-page";
    public static final String QUESTION_REDIRECT = "redirect:/competition/setup/%d/section/application/question/%d/edit";
    private static final String QUESTION_VIEW = "competition/setup/question";
    private static final String MODEL = "model";

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @Autowired
    private QuestionSetupCompetitionRestService questionSetupCompetitionRestService;

    @Autowired
    private QuestionSetupRestService questionSetupRestService;

    @Autowired
    private CompetitionSetupPopulator competitionSetupPopulator;

    @Autowired
    private FormInputRestService formInputRestService;

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @PostMapping(value = "/landing-page", params = "createQuestion")
    public String createQuestion(@PathVariable long competitionId) {
        ServiceResult<CompetitionSetupQuestionResource> result = questionSetupCompetitionRestService
                .addDefaultToCompetition(competitionId).toServiceResult();

        Function<CompetitionSetupQuestionResource, String> successViewFunction =
                (question) -> String.format("redirect:/competition/setup/%d/section/application/question/%d/edit", competitionId, question.getQuestionId());
        return successViewFunction.apply(result.getSuccess());
    }

    @PostMapping(value = "/landing-page", params = "deleteQuestion")
    public String deleteQuestion(@ModelAttribute("deleteQuestion") DeleteQuestionForm deleteQuestionForm,
                                         @PathVariable long competitionId) {
        questionSetupCompetitionRestService.deleteById(deleteQuestionForm.getDeleteQuestion());

        Supplier<String> view = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        return view.get();
    }

    @GetMapping("/landing-page")
    public String applicationProcessLandingPage(Model model, @PathVariable long competitionId, UserResource loggedInUser) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, loggedInUser, APPLICATION_FORM));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, new LandingPageForm());
        return "competition/setup";
    }

    @PostMapping("/landing-page")
    public String setApplicationProcessAsComplete(Model model,
                                                  @PathVariable long competitionId,
                                                  @ModelAttribute(COMPETITION_SETUP_FORM_KEY) LandingPageForm form,
                                                  BindingResult bindingResult,
                                                  ValidationHandler validationHandler,
                                                  UserResource loggedInUser) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionId;
        }

        Supplier<String> failureView = () -> {
            model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competitionResource, loggedInUser, APPLICATION_FORM));
            model.addAttribute(COMPETITION_SETUP_FORM_KEY, form);
            return "competition/setup";
        };
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView, () ->
                competitionSetupQuestionService.validateApplicationQuestions(competitionResource, form, bindingResult));
    }

    @GetMapping("/question/finance")
    public String seeApplicationFinances(@PathVariable long competitionId,
                                         UserResource loggedInUser,
                                         Model model) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        return getFinancePage(model, competitionResource, loggedInUser, false, null);
    }


    @GetMapping("/question/finance/edit")
    public String editApplicationFinances(@PathVariable long competitionId,
                                          UserResource loggedInUser,
                                          Model model) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        return ifUserCanAccessEditPageMarkSectionAsIncomplete(competitionResource,
                () -> getFinancePage(model, competitionResource, loggedInUser, true, null),
                Optional.of(FINANCES), Optional.empty());
    }

    @PostMapping("/question/finance/edit")
    public String submitApplicationFinances(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) FinanceForm form,
                                            BindingResult bindingResult,
                                            ValidationHandler validationHandler,
                                            @PathVariable long competitionId,
                                            UserResource loggedInUser,
                                            Model model) {

        return handleFinanceSaving(competitionId, loggedInUser, model, form, validationHandler);
    }

    private String handleFinanceSaving(long competitionId, UserResource loggedInUser, Model model, FinanceForm form, ValidationHandler validationHandler) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        Supplier<String> failureView = () -> getFinancePage(model, competitionResource, loggedInUser, true, form);
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> competitionSetupService.saveCompetitionSetupSubsection(form, competitionResource, APPLICATION_FORM, FINANCES));
    }

    @GetMapping("/question/{questionId}")
    public String seeQuestionInCompSetup(@PathVariable long competitionId,
                                         @PathVariable("questionId") Long questionId,
                                         UserResource loggedInUser,
                                         Model model) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        return getQuestionPage(model, competitionResource, loggedInUser, questionId, false, null);
    }

    @GetMapping("/question/{questionId}/edit")
    public String editQuestionInCompSetup(@PathVariable long competitionId,
                                          @PathVariable long questionId,
                                          UserResource loggedInUser,
                                          Model model) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        return ifUserCanAccessEditPageMarkSectionAsIncomplete(competitionResource,
                () -> getQuestionPage(model, competitionResource, loggedInUser, questionId, true, null),
                Optional.empty(),
                Optional.ofNullable(questionId));
    }

    @PostMapping(value = "/question/{questionId}/edit", params = "question.type=ASSESSED_QUESTION")
    public String submitAssessedQuestion(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) QuestionForm competitionSetupForm,
                                         BindingResult bindingResult,
                                         ValidationHandler validationHandler,
                                         @PathVariable long competitionId,
                                         @PathVariable long questionId,
                                         UserResource loggedInUser,
                                         Model model) {
        validateAssessmentGuidanceRows(competitionSetupForm, bindingResult);

        validateRadioButtons(competitionSetupForm, bindingResult);

        validateFileUploaded(competitionSetupForm, bindingResult, questionId);
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        Supplier<String> failureView = () -> getQuestionPage(model, competitionResource, loggedInUser, competitionSetupForm.getQuestion().getQuestionId(), true, competitionSetupForm);
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> competitionSetupService.saveCompetitionSetupSubsection(competitionSetupForm, competitionResource, APPLICATION_FORM, QUESTIONS));
    }


    @PostMapping(value = "/question/{questionId}/edit", params = {"question.type=ASSESSED_QUESTION", "uploadTemplateDocumentFile"})
    public String uploadTemplateDocumentFile(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) QuestionForm competitionSetupForm,
                                             BindingResult bindingResult,
                                             ValidationHandler validationHandler,
                                             @PathVariable long competitionId,
                                             @PathVariable long questionId,
                                             UserResource loggedInUser,
                                             Model model) {

        MultipartFile file = competitionSetupForm.getTemplateDocumentFile();
        Supplier<String> view = () -> getQuestionPage(model, competitionRestService.getCompetitionById(competitionId).getSuccess(),
                loggedInUser, questionId, true, competitionSetupForm);

        return validationHandler.performActionOrBindErrorsToField("templateDocumentFile", view, view,
                () -> questionSetupCompetitionRestService.uploadTemplateDocument(questionId,
                                file.getContentType(), file.getSize(), file.getOriginalFilename(), getMultipartFileBytes(file)));
    }

    @PostMapping(value = "/question/{questionId}/edit", params = {"question.type=ASSESSED_QUESTION", "removeTemplateDocumentFile"})
    public String removeTemplateDocumentFile(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) QuestionForm competitionSetupForm,
                                             BindingResult bindingResult,
                                             ValidationHandler validationHandler,
                                             @PathVariable long competitionId,
                                             @PathVariable long questionId,
                                             UserResource loggedInUser,
                                             Model model,
                                             RedirectAttributes redirectAttributes) {
        Supplier<String> view = () -> getQuestionPage(model, competitionRestService.getCompetitionById(competitionId).getSuccess(),
                loggedInUser, questionId, true, competitionSetupForm);

        return validationHandler.performActionOrBindErrorsToField("templateDocumentFile", view, view,
                () -> questionSetupCompetitionRestService.deleteTemplateDocument(questionId));
    }

    @GetMapping("/question/{questionId}/download-template-file")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> downloadFile(Model model,
                                                   @PathVariable long questionId) {
        CompetitionSetupQuestionResource question = questionSetupCompetitionRestService.getByQuestionId(questionId).getSuccess();
        return getFileResponseEntity(formInputRestService.downloadFile(question.getTemplateFormInput()).getSuccess(),
                        formInputRestService.findFile(question.getTemplateFormInput()).getSuccess());
    }

    @PostMapping("/question/{questionId}/edit")
    public String submitProjectDetailsQuestion(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ProjectForm competitionSetupForm,
                                               BindingResult bindingResult,
                                               ValidationHandler validationHandler,
                                               @PathVariable long competitionId,
                                               UserResource loggedInUser,
                                               Model model) {
        validateScopeGuidanceRows(competitionSetupForm, bindingResult);

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        Supplier<String> failureView = () -> getQuestionPage(model, competitionResource, loggedInUser, competitionSetupForm.getQuestion().getQuestionId(), true, competitionSetupForm);
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);

        return validationHandler.performActionOrBindErrorsToField("", failureView, successView,
                () -> competitionSetupService.saveCompetitionSetupSubsection(competitionSetupForm, competitionResource, APPLICATION_FORM, PROJECT_DETAILS));
    }

    @GetMapping(value = "/detail")
    public String viewApplicationDetails(@PathVariable long competitionId,
                                         UserResource loggedInUser,
                                         Model model) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        return getDetailsPage(model, competitionResource, loggedInUser, false, null);
    }

    @GetMapping(value = "/detail/edit")
    public String getEditApplicationDetails(@PathVariable long competitionId,
                                            UserResource loggedInUser,
                                            Model model) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if(competitionResource.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        return ifUserCanAccessEditPageMarkSectionAsIncomplete(competitionResource,
                () ->  getDetailsPage(model, competitionResource, loggedInUser, true, null),
                Optional.of(APPLICATION_DETAILS),
                Optional.empty());

    }

    @PostMapping("/detail/edit")
    public String submitApplicationDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) DetailsForm form,
                                           BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           @PathVariable long competitionId,
                                           UserResource loggedInUser,
                                           Model model) {
        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)) {
            return "redirect:/competition/setup/" + competitionResource.getId();
        }

        Supplier<String> failureView = () -> getDetailsPage(model, competitionResource, loggedInUser, true, form);
        Supplier<String> successView = () -> String.format(APPLICATION_LANDING_REDIRECT, competitionId);


        return validationHandler.addAnyErrors(
                competitionSetupService.saveCompetitionSetupSubsection(form,
                        competitionResource,
                        APPLICATION_FORM,
                        APPLICATION_DETAILS),
                fieldErrorsToFieldErrors(),
                asGlobalErrors())
                .failNowOrSucceedWith(
                        failureView,
                        successView
                );

    }

    private void validateAssessmentGuidanceRows(QuestionForm applicationQuestionForm, BindingResult bindingResult) {
        if (Boolean.TRUE.equals(applicationQuestionForm.getQuestion().getWrittenFeedback())) {
            ValidationUtils.invokeValidator(validator, applicationQuestionForm, bindingResult, GuidanceRowForm.GuidanceRowViewGroup.class);
        }
    }

    private void validateScopeGuidanceRows(ProjectForm applicationProjectForm, BindingResult bindingResult) {
        if (Boolean.TRUE.equals(applicationProjectForm.getQuestion().getWrittenFeedback())) {
            ValidationUtils.invokeValidator(validator, applicationProjectForm, bindingResult, GuidanceRowResource.GuidanceRowGroup.class);
        }
    }

    private void validateRadioButtons(QuestionForm competitionSetupForm, BindingResult bindingResult) {
        if(competitionSetupForm.getQuestion().getAppendix() == null) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "question.appendix", "This field cannot be left blank."));
        }
        if(competitionSetupForm.getQuestion().getTemplateDocument() == null) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "question.templateDocument", "This field cannot be left blank."));
        }
        if(competitionSetupForm.getQuestion().getWrittenFeedback() == null) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "question.writtenFeedback", "This field cannot be left blank."));
        }
        if(competitionSetupForm.getQuestion().getScored() == null) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "question.scored", "This field cannot be left blank."));
        }
    }

    private void validateFileUploaded(QuestionForm questionForm, BindingResult bindingResult, long questionId) {
        if (TRUE.equals(questionForm.getQuestion().getTemplateDocument())) {
            CompetitionSetupQuestionResource question = questionSetupCompetitionRestService.getByQuestionId(questionId).getSuccess();
            if (question.getTemplateFilename() == null) {
                bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "templateDocumentFile", "You must upload a file."));
            }
        }
    }

    private String getFinancePage(Model model, CompetitionResource competitionResource, UserResource loggedInUser, boolean isEditable, CompetitionSetupForm form) {
        model.addAttribute(MODEL, setupQuestionViewModel(competitionResource, loggedInUser, Optional.empty(), FINANCES, isEditable, null));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, setupQuestionForm(competitionResource, Optional.empty(), FINANCES, form));
        return "competition/finances";
    }

    private String getDetailsPage(Model model, CompetitionResource competitionResource, UserResource loggedInUser, boolean isEditable, CompetitionSetupForm form) {
        model.addAttribute(MODEL, setupQuestionViewModel(competitionResource, loggedInUser, Optional.empty(), APPLICATION_DETAILS, isEditable, null));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, setupQuestionForm(competitionResource, Optional.empty(), APPLICATION_DETAILS, form));
        return "competition/application-details";
    }

    private String getQuestionPage(Model model, CompetitionResource competitionResource, UserResource loggedInUser, Long questionId, boolean isEditable, CompetitionSetupForm form) {
        ServiceResult<String> view = questionSetupCompetitionRestService.getByQuestionId(questionId).toServiceResult()
                .andOnSuccessReturn(
                questionResource -> {
                    QuestionSetupType type = questionResource.getType();
                    CompetitionSetupSubsection setupSubsection;

                    if (type.equals(QuestionSetupType.ASSESSED_QUESTION)) {
                        setupSubsection = CompetitionSetupSubsection.QUESTIONS;
                    } else {
                        setupSubsection = CompetitionSetupSubsection.PROJECT_DETAILS;
                    }

                    model.addAttribute(MODEL, setupQuestionViewModel(competitionResource, loggedInUser, Optional.of(questionId), setupSubsection, isEditable, questionResource.getTemplateFilename()));
                    model.addAttribute(COMPETITION_SETUP_FORM_KEY, setupQuestionForm(competitionResource, Optional.of(questionId), setupSubsection, form));

                    return QUESTION_VIEW;
                }).andOnFailure(() -> serviceSuccess("redirect:/non-ifs-competition/setup/" + questionId));

        return view.getSuccess();
    }

    private QuestionSetupViewModel setupQuestionViewModel(final CompetitionResource competition, final UserResource loggedInUser, final Optional<Long> questionId, CompetitionSetupSubsection subsection, boolean isEditable, String filename) {
        CompetitionSetupSection section = APPLICATION_FORM;

        CompetitionSetupSubsectionViewModel subsectionViewModel = competitionSetupService.populateCompetitionSubsectionModelAttributes(competition, section,
                subsection, questionId);
        GeneralSetupViewModel generalViewModel = competitionSetupPopulator.populateGeneralModelAttributes(competition, loggedInUser, section);

        return new QuestionSetupViewModel(generalViewModel, subsectionViewModel, competition.getName(), isEditable, filename);
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
