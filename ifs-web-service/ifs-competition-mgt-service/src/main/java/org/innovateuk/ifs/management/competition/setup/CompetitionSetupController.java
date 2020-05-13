package org.innovateuk.ifs.management.competition.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.CharMatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.management.competition.setup.application.form.LandingPageForm;
import org.innovateuk.ifs.management.competition.setup.assessor.form.AssessorsForm;
import org.innovateuk.ifs.management.competition.setup.completionstage.form.CompletionStageForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupSummaryForm;
import org.innovateuk.ifs.management.competition.setup.core.form.FunderRowForm;
import org.innovateuk.ifs.management.competition.setup.core.form.TermsAndConditionsForm;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.eligibility.form.EligibilityForm;
import org.innovateuk.ifs.management.competition.setup.fundinginformation.form.AdditionalInfoForm;
import org.innovateuk.ifs.management.competition.setup.initialdetail.form.InitialDetailsForm;
import org.innovateuk.ifs.management.competition.setup.initialdetail.form.InitialDetailsForm.Unrestricted;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestFailure.error;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.*;
import static org.innovateuk.ifs.controller.FileUploadControllerUtils.getMultipartFileBytes;
import static org.innovateuk.ifs.management.competition.setup.application.controller.CompetitionSetupApplicationController.APPLICATION_LANDING_REDIRECT;
import static org.innovateuk.ifs.management.competition.setup.projectdocument.controller.CompetitionSetupDocumentController.PROJECT_DOCUMENT_LANDING_REDIRECT;

/**
 * Controller for showing and handling the different competition setup sections
 */
@Controller
@RequestMapping("/competition/setup")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionSetupController.class)
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionSetupController {
    private static final Log LOG = LogFactory.getLog(CompetitionSetupController.class);
    public static final String COMPETITION_ID_KEY = "competitionId";
    public static final String COMPETITION_SETUP_FORM_KEY = "competitionSetupForm";
    private static final String SECTION_PATH_KEY = "sectionPath";
    private static final String PUBLIC_CONTENT_LANDING_REDIRECT = "redirect:/competition/setup/public-content/";
    private static final String DASHBOARD_REDIRECT = "redirect:/dashboard";
    private static final String MODEL = "model";

    @Autowired
    private CompetitionRestService competitionRestService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Autowired
    private TermsAndConditionsRestService termsAndConditionsRestService;

    public static final String SETUP_READY_KEY = "setupReady";
    public static final String READY_TO_OPEN_KEY = "isReadyToOpen";

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @GetMapping("/{competitionId}")
    public String initCompetitionSetupSection(Model model,
                                              @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                              @ModelAttribute(COMPETITION_SETUP_FORM_KEY) CompetitionSetupSummaryForm competitionSetupSummaryForm,
                                              @SuppressWarnings("UnusedParameters") BindingResult bindingResult) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if (competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        CompetitionSetupSection section = CompetitionSetupSection.fromPath("home");

        boolean canAssignFinanceUsers = competition.getCovidType() != null;

        model.addAttribute("canAssignFinanceUsers", canAssignFinanceUsers);
        model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, section));
        model.addAttribute(SETUP_READY_KEY, competitionSetupService.isCompetitionReadyToOpen(competition));
        model.addAttribute(READY_TO_OPEN_KEY, competition.getCompetitionStatus().equals(CompetitionStatus.READY_TO_OPEN));
        return "competition/setup";
    }

    @PostMapping("/{competitionId}/section/{sectionPath}/edit")
    public String setSectionAsIncomplete(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                         @PathVariable(SECTION_PATH_KEY) String sectionPath) {
        CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);
        if (section == null) {
            LOG.error("Invalid section path specified: " + sectionPath);
            return DASHBOARD_REDIRECT;
        }

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (section.preventEdit(competition)) {
            return DASHBOARD_REDIRECT;
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId) && section != CompetitionSetupSection.INITIAL_DETAILS) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        return competitionSetupRestService.markSectionIncomplete(competitionId, section).handleSuccessOrFailure(
                failure -> "redirect:/competition/setup/" + competition.getId(),
                success -> {
                    if (!competition.isSetupAndLive()) {
                        competitionSetupService.setCompetitionAsCompetitionSetup(competitionId);
                    }
                    return "redirect:/competition/setup/" + competitionId + "/section/" + section.getPostMarkIncompletePath();
                }
        );
    }

    @GetMapping("/{competitionId}/section/{sectionPath}")
    public String editCompetitionSetupSection(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                              @PathVariable(SECTION_PATH_KEY) String sectionPath,
                                              Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId) && section != CompetitionSetupSection.INITIAL_DETAILS) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        if (section == null) {
            LOG.error("Invalid section path specified: " + sectionPath);
            return DASHBOARD_REDIRECT;
        } else if (section == CompetitionSetupSection.APPLICATION_FORM) {
            return format(APPLICATION_LANDING_REDIRECT, competitionId);
        } else if (section == CompetitionSetupSection.PROJECT_DOCUMENT) {
            return format(PROJECT_DOCUMENT_LANDING_REDIRECT, competitionId);
        } else if (section == CompetitionSetupSection.CONTENT) {
            return PUBLIC_CONTENT_LANDING_REDIRECT + competitionId;
        }

        if (competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, section));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupService.getSectionFormData(competition, section));

        return "competition/setup";
    }

    @PostMapping(value = "/{competitionId}/section/initial", params = "unrestricted")
    public String submitUnrestrictedInitialSectionDetails(
            @Validated({Unrestricted.class, Default.class}) @Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) InitialDetailsForm competitionSetupForm,
            @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
            ValidationHandler validationHandler,
            @PathVariable(COMPETITION_ID_KEY) long competitionId,
            Model model) {
        return doSubmitInitialSectionDetails(competitionSetupForm, validationHandler, competitionId, model);
    }


    @PostMapping("/{competitionId}/section/initial")
    public String submitInitialSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) InitialDetailsForm competitionSetupForm,
                                              @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                              ValidationHandler validationHandler,
                                              @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                              Model model) {
        return doSubmitInitialSectionDetails(competitionSetupForm, validationHandler, competitionId, model);
    }

    private String doSubmitInitialSectionDetails(InitialDetailsForm competitionSetupForm,
                                                 ValidationHandler validationHandler,
                                                 long competitionId,
                                                 Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.INITIAL_DETAILS, model);
    }

    @PostMapping("/{competitionId}/section/additional")
    public String submitAdditionalSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) AdditionalInfoForm competitionSetupForm,
                                                 BindingResult bindingResult,
                                                 ValidationHandler validationHandler,
                                                 @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                 Model model, HttpServletRequest request) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (request.getParameterMap().containsKey("generate-code")) {
            if (competition.getStartDate() != null) {
                String competitionCode = competitionSetupRestService.generateCompetitionCode(competitionId, competition.getStartDate())
                        .getSuccess();
                competitionSetupForm.setCompetitionCode(competitionCode);
                competitionSetupForm.setMarkAsCompleteAction(false);
            }
        } else if (request.getParameterMap().containsKey("add-funder")) {
            List<FunderRowForm> funders = competitionSetupForm.getFunders();
            funders.add(new FunderRowForm(new CompetitionFunderResource()));
            competitionSetupForm.setFunders(funders);
            competitionSetupForm.setMarkAsCompleteAction(false);
        } else if (request.getParameterMap().containsKey("remove-funder")) {
            int removeCoFunderIndex = Integer.valueOf(request.getParameterMap().get("remove-cofunder")[0]);
            competitionSetupForm.getFunders().remove(removeCoFunderIndex);
            competitionSetupForm.setMarkAsCompleteAction(false);
        }

        //Validate after competition code generated and co funders added/removed.
        validator.validate(competitionSetupForm, bindingResult);

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.ADDITIONAL_INFO, model);
    }

    @PostMapping("/{competitionId}/section/eligibility")
    public String submitEligibilitySectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) EligibilityForm competitionSetupForm,
                                                  BindingResult bindingResult,
                                                  ValidationHandler validationHandler,
                                                  @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                  Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if ("yes".equals(competitionSetupForm.getMultipleStream()) && StringUtils.isEmpty(competitionSetupForm.getStreamName())) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "streamName", "A stream name is required"));
        }

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.ELIGIBILITY, model);
    }

    @PostMapping("/{competitionId}/section/completion-stage")
    public String submitCompletionStageSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) CompletionStageForm competitionSetupForm,
                                                 BindingResult bindingResult,
                                                 ValidationHandler validationHandler,
                                                 @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                 Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition,
                CompetitionSetupSection.COMPLETION_STAGE, model);
    }

    @PostMapping("/{competitionId}/section/milestones")
    public String submitMilestonesSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) MilestonesForm competitionSetupForm,
                                                 BindingResult bindingResult,
                                                 ValidationHandler validationHandler,
                                                 @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                 Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (bindingResult.hasErrors()) {
            competitionSetupMilestoneService.sortMilestones(competitionSetupForm);
        }

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.MILESTONES, model);
    }

    @PostMapping("/{competitionId}/section/application")
    public String submitApplicationFormSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) LandingPageForm competitionSetupForm,
                                                      @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                      ValidationHandler validationHandler,
                                                      @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                      Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.APPLICATION_FORM, model);
    }


    @PostMapping("/{competitionId}/section/assessors")
    public String submitAssessorsSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) AssessorsForm competitionSetupForm,
                                                @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                ValidationHandler validationHandler,
                                                @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.ASSESSORS, model);
    }

    @PostMapping("/{competitionId}/section/terms-and-conditions")
    public String submitTermsAndConditionsSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) TermsAndConditionsForm competitionSetupForm,
                                                         @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                         ValidationHandler validationHandler,
                                                         @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                         Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if (isProcurement(competitionSetupForm.getTermsAndConditionsId())) {
            if (competition.getCompetitionTerms() == null) {
                bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "termsAndConditionsDoc", "Upload a terms and conditions document."));
            }
        } else {
            competitionSetupRestService.deleteCompetitionTerms(competitionId);
        }

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.TERMS_AND_CONDITIONS, model);
    }

    private boolean isProcurement(long termsAndConditionsId) {
        return termsAndConditionsRestService.getById(termsAndConditionsId).getSuccess().isProcurement();
    }


    @PostMapping("/{competitionId}/ready-to-open")
    public String setAsReadyToOpen(Model model,
                                   @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                   @Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) CompetitionSetupSummaryForm competitionSetupSummaryForm,
                                   BindingResult bindingResult,
                                   ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> initCompetitionSetupSection(model, competitionId, competitionSetupSummaryForm, bindingResult);

        ServiceResult<Void> updateResult = competitionSetupService.setCompetitionAsReadyToOpen(competitionId);

        return validationHandler.addAnyErrors(updateResult, asGlobalErrors())
                .failNowOrSucceedWith(failureView, () -> format("redirect:/competition/setup/%d", competitionId));
    }

    @PostMapping("/{competitionIdToDelete}/delete")
    public String delete(Model model,
                         @PathVariable("competitionIdToDelete") long competitionId,
                         @Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) CompetitionSetupSummaryForm competitionSetupSummaryForm,
                         BindingResult bindingResult,
                         ValidationHandler validationHandler) {
        Supplier<String> failureView = () -> initCompetitionSetupSection(model, competitionId, competitionSetupSummaryForm, bindingResult);

        ServiceResult<Void> deleteResult = competitionSetupService.deleteCompetition(competitionId);

        return validationHandler.addAnyErrors(deleteResult, asGlobalErrors())
                .failNowOrSucceedWith(failureView, () -> DASHBOARD_REDIRECT);
    }

    @PostMapping(path="/{competitionId}/section/terms-and-conditions", params = "uploadTermsAndConditionsDoc")
    public String uploadTermsAndConditions(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) TermsAndConditionsForm termsAndConditionsForm,
                                           @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                           Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        Supplier<String> success = () -> format("redirect:/competition/setup/%d/section/terms-and-conditions", + competition.getId());
        Supplier<String> failure = () -> genericCompetitionSetupSection(termsAndConditionsForm, validationHandler, competition, CompetitionSetupSection.TERMS_AND_CONDITIONS, model);

        MultipartFile file = termsAndConditionsForm.getTermsAndConditionsDoc();
        RestResult<FileEntryResource> uploadResult = competitionSetupRestService.uploadCompetitionTerms(competitionId, file.getContentType(), file.getSize(),
                file.getOriginalFilename(), getMultipartFileBytes(file));

        termsAndConditionsForm.setMarkAsCompleteAction(false);
        competitionSetupService.saveCompetitionSetupSection(termsAndConditionsForm, competition, CompetitionSetupSection.TERMS_AND_CONDITIONS);

        return validationHandler.addAnyErrors(error(uploadResult.getErrors()), fileUploadField("termsAndConditionsDoc"), defaultConverters())
                .failNowOrSucceedWith(failure, success);
    }

    @PostMapping(path="/{competitionId}/section/terms-and-conditions", params = "deleteTermsAndConditionsDoc")
    public String deleteTermsAndConditions(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) TermsAndConditionsForm termsAndConditionsForm,
                                           @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                           ValidationHandler validationHandler,
                                           @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                           Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        Supplier<String> failureAndSuccessView = () -> format("redirect:/competition/setup/%d/section/terms-and-conditions", + competition.getId());

        RestResult<Void> deleteResult = competitionSetupRestService.deleteCompetitionTerms(competitionId);
        return validationHandler.addAnyErrors(error(deleteResult.getErrors()), fileUploadField("termsAndConditionsDoc"), defaultConverters())
                .failNowOrSucceedWith(failureAndSuccessView, failureAndSuccessView);
    }

    /* AJAX Function */
    @GetMapping("/{competitionId}/generateCompetitionCode")
    @ResponseBody
    public JsonNode generateCompetitionCode(@PathVariable(COMPETITION_ID_KEY) long competitionId) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if (competition.getStartDate() != null) {
            return this.createJsonObjectNode(true, competitionSetupRestService.generateCompetitionCode(competitionId, competition.getStartDate())
                    .getSuccess());
        } else {
            return this.createJsonObjectNode(false, "Please set a start date for your competition before generating the competition code, you can do this in the Initial Details section");
        }
    }

    private String genericCompetitionSetupSection(CompetitionSetupForm competitionSetupForm,
                                                  ValidationHandler validationHandler,
                                                  CompetitionResource competition,
                                                  CompetitionSetupSection section,
                                                  Model model) {
        if (competition.isNonIfs()) {
            return format("redirect:/non-ifs-competition/setup/%d", competition.getId());
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competition.getId()) && section != CompetitionSetupSection.INITIAL_DETAILS) {
            return format("redirect:/competition/setup/%d", competition.getId());
        }

        Supplier<String> successView = () -> format("redirect:/competition/setup/%d/section/%s", competition.getId(), section.getPostMarkCompletePath());
        Supplier<String> failureView = () -> {
            model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, section));
            return "competition/setup";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> saveResult = competitionSetupService.saveCompetitionSetupSection(competitionSetupForm, competition, section);
            return validationHandler.addAnyErrors(saveResult, fieldErrorsToFieldErrors(), asGlobalErrors())
                    .failNowOrSucceedWith(failureView, successView);
        });
    }

    private ObjectNode createJsonObjectNode(boolean success, String message) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", success ? "true" : "false");
        node.put("message", CharMatcher.is('\"').trimFrom(message));

        return node;
    }

    private ObjectNode createJsonObjectNode(boolean success) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", success ? "true" : "false");

        return node;
    }
}
