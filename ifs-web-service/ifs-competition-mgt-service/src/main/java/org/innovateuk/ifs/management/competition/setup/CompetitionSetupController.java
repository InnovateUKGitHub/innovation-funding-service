package org.innovateuk.ifs.management.competition.setup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.CharMatcher;
import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.finance.service.GrantClaimMaximumRestService;
import org.innovateuk.ifs.management.competition.setup.application.form.LandingPageForm;
import org.innovateuk.ifs.management.competition.setup.applicationassessment.form.ApplicationAssessmentForm;
import org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.form.ApplicationExpressionOfInterestForm;
import org.innovateuk.ifs.management.competition.setup.applicationsubmission.form.ApplicationSubmissionForm;
import org.innovateuk.ifs.management.competition.setup.assessor.form.AssessorsForm;
import org.innovateuk.ifs.management.competition.setup.completionstage.form.CompletionStageForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupSummaryForm;
import org.innovateuk.ifs.management.competition.setup.core.form.FunderRowForm;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.fundingamountsought.form.FundingAmountSoughtForm;
import org.innovateuk.ifs.management.competition.setup.fundingeligibility.form.FundingEligibilityResearchCategoryForm;
import org.innovateuk.ifs.management.competition.setup.fundinginformation.form.AdditionalInfoForm;
import org.innovateuk.ifs.management.competition.setup.initialdetail.form.InitialDetailsForm;
import org.innovateuk.ifs.management.competition.setup.initialdetail.form.InitialDetailsForm.Unrestricted;
import org.innovateuk.ifs.management.competition.setup.milestone.form.MilestonesForm;
import org.innovateuk.ifs.management.competition.setup.projecteligibility.form.ProjectEligibilityForm;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.List;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;
import static org.innovateuk.ifs.management.competition.setup.application.controller.CompetitionSetupApplicationController.APPLICATION_LANDING_REDIRECT;
import static org.innovateuk.ifs.management.competition.setup.organisationaleligibility.controller.CompetitionSetupOrganisationalEligibilityController.ORGANISATIONAL_ELIGIBILITY_LANDING_REDIRECT;
import static org.innovateuk.ifs.management.competition.setup.projectdocument.controller.CompetitionSetupDocumentController.PROJECT_DOCUMENT_LANDING_REDIRECT;

/**
 * Controller for showing and handling the different competition setup sections
 */
@SuppressWarnings("unchecked")
@Controller
@Slf4j
@RequestMapping("/competition/setup")
@SecuredBySpring(value = "Controller", description = "TODO", securedType = CompetitionSetupController.class)
@PreAuthorize("hasAnyAuthority('comp_admin')")
public class CompetitionSetupController {

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
    private GrantClaimMaximumRestService grantClaimMaximumRestService;

    @Value("${ifs.subsidy.control.enabled:true}")
    private boolean fundingRuleEnabled;

    public static final String SETUP_READY_KEY = "setupReady";
    public static final String READY_TO_OPEN_KEY = "isReadyToOpen";

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @GetMapping("/{competitionId}")
    public String initCompetitionSetupSection(Model model,
                                              @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                              @ModelAttribute(COMPETITION_SETUP_FORM_KEY) CompetitionSetupSummaryForm competitionSetupSummaryForm,
                                              @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                              UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        if (competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        CompetitionSetupSection section = CompetitionSetupSection.fromPath("home");

        boolean canAssignFinanceUsers = competition.getCovidType() != null;

        model.addAttribute("canAssignFinanceUsers", canAssignFinanceUsers);
        model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, section));
        model.addAttribute(SETUP_READY_KEY, competitionSetupService.isCompetitionReadyToOpen(competition));
        model.addAttribute(READY_TO_OPEN_KEY, competition.getCompetitionStatus().equals(CompetitionStatus.READY_TO_OPEN));
        return "competition/setup";
    }

    @PostMapping("/{competitionId}/section/{sectionPath}/edit")
    public String setSectionAsIncomplete(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                         @PathVariable(SECTION_PATH_KEY) String sectionPath,
                                         UserResource loggedInUser) {
        CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);
        if (section == null) {
            log.error("Invalid section path specified: " + sectionPath);
            return DASHBOARD_REDIRECT;
        }

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (section.preventEdit(competition, loggedInUser)) {
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
                                              Model model,
                                              UserResource loggedInUser) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId) && section != CompetitionSetupSection.INITIAL_DETAILS) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        if (section == null) {
            log.error("Invalid section path specified: " + sectionPath);
            return DASHBOARD_REDIRECT;
        } else if (section == CompetitionSetupSection.APPLICATION_FORM) {
            return format(APPLICATION_LANDING_REDIRECT, competitionId);
        } else if (section == CompetitionSetupSection.PROJECT_DOCUMENT) {
            return format(PROJECT_DOCUMENT_LANDING_REDIRECT, competitionId);
        } else if (section == CompetitionSetupSection.CONTENT) {
            return PUBLIC_CONTENT_LANDING_REDIRECT + competitionId;
        } else if (section == CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY) {
            return format(ORGANISATIONAL_ELIGIBILITY_LANDING_REDIRECT, competitionId);
        }

        if (competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, section));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupService.getSectionFormPopulator(section).populateForm(competition));

        return "competition/setup";
    }

    @PostMapping(value = "/{competitionId}/section/initial", params = "unrestricted")
    public String submitUnrestrictedInitialSectionDetails(
            @Validated({Unrestricted.class, Default.class}) @Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) InitialDetailsForm competitionSetupForm,
            @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
            ValidationHandler validationHandler,
            @PathVariable(COMPETITION_ID_KEY) long competitionId,
            UserResource loggedInUser,
            Model model) {

        return doSubmitInitialSectionDetails(competitionSetupForm, validationHandler, competitionId, loggedInUser, model);
    }


    @PostMapping("/{competitionId}/section/initial")
    public String submitInitialSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) InitialDetailsForm competitionSetupForm,
                                              @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                              ValidationHandler validationHandler,
                                              @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                              UserResource loggedInUser,
                                              Model model) {
        return doSubmitInitialSectionDetails(competitionSetupForm, validationHandler, competitionId, loggedInUser, model);
    }

    private String doSubmitInitialSectionDetails(InitialDetailsForm competitionSetupForm,
                                                 ValidationHandler validationHandler,
                                                 long competitionId,
                                                 UserResource loggedInUser,
                                                 Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.INITIAL_DETAILS, loggedInUser, model);
    }

    @PostMapping("/{competitionId}/section/additional")
    public String submitAdditionalSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) AdditionalInfoForm competitionSetupForm,
                                                 BindingResult bindingResult,
                                                 ValidationHandler validationHandler,
                                                 @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                 UserResource loggedInUser,
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

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.ADDITIONAL_INFO, loggedInUser,  model);
    }

    @PostMapping("/{competitionId}/section/project-eligibility")
    public String submitEligibilitySectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ProjectEligibilityForm competitionSetupForm,
                                                  BindingResult bindingResult,
                                                  ValidationHandler validationHandler,
                                                  @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                  UserResource loggedInUser,
                                                  Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        validateResearchParticipationPercentage(competitionSetupForm, bindingResult);
        if ("yes".equals(competitionSetupForm.getMultipleStream()) && ObjectUtils.isEmpty(competitionSetupForm.getStreamName())) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "streamName", "A stream name is required"));
        }

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.PROJECT_ELIGIBILITY, loggedInUser, model);
    }

    @PostMapping("/{competitionId}/section/funding-eligibility")
    public String submitFundingEligibilitySectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) FundingEligibilityResearchCategoryForm competitionSetupForm,
                                                  BindingResult bindingResult,
                                                  ValidationHandler validationHandler,
                                                  @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                  UserResource loggedInUser,
                                                  Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.FUNDING_ELIGIBILITY, loggedInUser, model);
    }

    @PostMapping("/{competitionId}/section/funding-amount-sought")
    public String submitFundingAmountSoughtSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) FundingAmountSoughtForm competitionSetupForm,
                                                         BindingResult bindingResult,
                                                         ValidationHandler validationHandler,
                                                         @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                         UserResource loggedInUser,
                                                         Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();
        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.FUNDING_AMOUNT_SOUGHT, loggedInUser, model);
    }

    @PostMapping("/{competitionId}/section/completion-stage")
    public String submitCompletionStageSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) CompletionStageForm competitionSetupForm,
                                                      BindingResult bindingResult,
                                                      ValidationHandler validationHandler,
                                                      @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                      UserResource loggedInUser,
                                                      Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition,
                CompetitionSetupSection.COMPLETION_STAGE, loggedInUser, model);
    }

    @PostMapping("/{competitionId}/section/application-submission")
    public String submitApplicationSubmissionSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationSubmissionForm competitionSetupForm,
                                                            BindingResult bindingResult,
                                                            ValidationHandler validationHandler,
                                                            @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                            UserResource loggedInUser,
                                                            Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition,
                CompetitionSetupSection.APPLICATION_SUBMISSION, loggedInUser, model);
    }

    @PostMapping("/{competitionId}/section/application-expression-of-interest")
    public String submitApplicationExpressionOfInterestSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationExpressionOfInterestForm competitionSetupForm,
                                                            BindingResult bindingResult,
                                                            ValidationHandler validationHandler,
                                                            @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                            UserResource loggedInUser,
                                                            Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition,
                CompetitionSetupSection.APPLICATION_EXPRESSION_OF_INTEREST, loggedInUser, model);
    }

    @PostMapping("/{competitionId}/section/application-assessment")
    public String submitApplicationAssessmentSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationAssessmentForm competitionSetupForm,
                                                            BindingResult bindingResult,
                                                            ValidationHandler validationHandler,
                                                            @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                            UserResource loggedInUser,
                                                            Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition,
                CompetitionSetupSection.APPLICATION_ASSESSMENT, loggedInUser, model);
    }

    @PostMapping("/{competitionId}/section/milestones")
    public String submitMilestonesSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) MilestonesForm competitionSetupForm,
                                                 BindingResult bindingResult,
                                                 ValidationHandler validationHandler,
                                                 @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                 UserResource loggedInUser,
                                                 Model model) {

        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        if (bindingResult.hasErrors()) {
            competitionSetupMilestoneService.sortMilestones(competitionSetupForm);
        }

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.MILESTONES, loggedInUser, model);
    }

    @PostMapping("/{competitionId}/section/application")
    public String submitApplicationFormSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) LandingPageForm competitionSetupForm,
                                                      @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                      ValidationHandler validationHandler,
                                                      @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                      UserResource loggedInUser,
                                                      Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.APPLICATION_FORM, loggedInUser, model);
    }


    @PostMapping("/{competitionId}/section/assessors")
    public String submitAssessorsSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) AssessorsForm competitionSetupForm,
                                                @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                ValidationHandler validationHandler,
                                                @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                UserResource loggedInUser,
                                                Model model) {
        CompetitionResource competition = competitionRestService.getCompetitionById(competitionId).getSuccess();

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.ASSESSORS, loggedInUser, model);
    }

    @PostMapping("/{competitionId}/ready-to-open")
    public String setAsReadyToOpen(Model model,
                                   @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                   @Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) CompetitionSetupSummaryForm competitionSetupSummaryForm,
                                   BindingResult bindingResult,
                                   ValidationHandler validationHandler,
                                   UserResource loggedInUser) {
        Supplier<String> failureView = () -> initCompetitionSetupSection(model, competitionId, competitionSetupSummaryForm, bindingResult, loggedInUser);

        ServiceResult<Void> updateResult = competitionSetupService.setCompetitionAsReadyToOpen(competitionId);

        return validationHandler.addAnyErrors(updateResult, asGlobalErrors())
                .failNowOrSucceedWith(failureView, () -> format("redirect:/competition/setup/%d", competitionId));
    }

    @PostMapping("/{competitionIdToDelete}/delete")
    public String delete(Model model,
                         @PathVariable("competitionIdToDelete") long competitionId,
                         @Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) CompetitionSetupSummaryForm competitionSetupSummaryForm,
                         BindingResult bindingResult,
                         ValidationHandler validationHandler,
                         UserResource loggedInUser) {
        Supplier<String> failureView = () -> initCompetitionSetupSection(model, competitionId, competitionSetupSummaryForm, bindingResult, loggedInUser);

        ServiceResult<Void> deleteResult = competitionSetupService.deleteCompetition(competitionId);

        return validationHandler.addAnyErrors(deleteResult, asGlobalErrors())
                .failNowOrSucceedWith(failureView, () -> DASHBOARD_REDIRECT);
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
                                                  UserResource loggedInUser,
                                                  Model model) {
        if (competition.isNonIfs()) {
            return format("redirect:/non-ifs-competition/setup/%d", competition.getId());
        }

        if (!competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competition.getId()) && section != CompetitionSetupSection.INITIAL_DETAILS) {
            return format("redirect:/competition/setup/%d", competition.getId());
        }

        Supplier<String> successView = () -> competitionSetupService.getNextSetupSection(competitionSetupForm, competition, section).getSuccess();
        Supplier<String> failureView = () -> {
            model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, loggedInUser, section));
            return "competition/setup";
        };

        return validationHandler.failNowOrSucceedWith(failureView, () -> {
            ServiceResult<Void> saveResult = competitionSetupService.saveCompetitionSetupSection(competitionSetupForm, competition, section, loggedInUser);
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
    private void validateResearchParticipationPercentage(ProjectEligibilityForm competitionSetupForm, BindingResult bindingResult) {
        Integer researchParticipationPercentage = competitionSetupForm.getResearchParticipationPercentage();
        if (researchParticipationPercentage == null || researchParticipationPercentage > 100 || researchParticipationPercentage < 0) {
            bindingResult.rejectValue("researchParticipationPercentage", "validation.projecteligibility.researchParticipationPercentage.required");
        }
    }
}
