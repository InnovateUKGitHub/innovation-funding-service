package org.innovateuk.ifs.competitionsetup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.CharMatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.application.form.LandingPageForm;
import org.innovateuk.ifs.competitionsetup.assessor.form.AssessorsForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupSummaryForm;
import org.innovateuk.ifs.competitionsetup.core.form.FunderRowForm;
import org.innovateuk.ifs.competitionsetup.core.form.TermsAndConditionsForm;
import org.innovateuk.ifs.competitionsetup.eligibility.form.EligibilityForm;
import org.innovateuk.ifs.competitionsetup.fundinginformation.form.AdditionalInfoForm;
import org.innovateuk.ifs.competitionsetup.initialdetail.form.InitialDetailsForm;
import org.innovateuk.ifs.competitionsetup.initialdetail.form.InitialDetailsForm.Unrestricted;
import org.innovateuk.ifs.competitionsetup.milestone.form.MilestonesForm;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.initialdetail.populator.ManageInnovationLeadsModelPopulator;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.innovateuk.ifs.competitionsetup.application.controller.CompetitionSetupApplicationController.APPLICATION_LANDING_REDIRECT;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

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
    private static final String SUBSECTION_PATH_KEY = "subsectionPath";
    public static final String PUBLIC_CONTENT_LANDING_REDIRECT = "redirect:/competition/setup/public-content/";
    private static final String MODEL = "model";

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private ManageInnovationLeadsModelPopulator manageInnovationLeadsModelPopulator;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    public static final String SETUP_READY_KEY = "setupReady";
    public static final String READY_TO_OPEN_KEY = "isReadyToOpen";

    private static final String RESTRICT_INITIAL_DETAILS_EDIT = "restrictInitialDetailsEdit";

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @GetMapping("/{competitionId}")
    public String initCompetitionSetupSection(Model model,
                                              @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                              @ModelAttribute(COMPETITION_SETUP_FORM_KEY) CompetitionSetupSummaryForm competitionSetupSummaryForm,
                                              @SuppressWarnings("UnusedParameters") BindingResult bindingResult) {
        CompetitionResource competition = competitionService.getById(competitionId);
        if (competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        CompetitionSetupSection section = CompetitionSetupSection.fromPath("home");
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
            return "redirect:/dashboard";
        }

        CompetitionResource competition = competitionService.getById(competitionId);

        if (section.preventEdit(competition)) {
            return "redirect:/dashboard";
        }

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId) && section != CompetitionSetupSection.INITIAL_DETAILS) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        competitionSetupRestService.markSectionIncomplete(competitionId, section).getSuccess();
        if (!competition.isSetupAndLive()) {
            competitionSetupService.setCompetitionAsCompetitionSetup(competitionId);
        }

        return "redirect:/competition/setup/" + competitionId + "/section/" + section.getPath();
    }

    @GetMapping("/{competitionId}/section/{sectionPath}")
    public String editCompetitionSetupSection(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                              @PathVariable(SECTION_PATH_KEY) String sectionPath,
                                              Model model) {
        CompetitionResource competition = competitionService.getById(competitionId);
        CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId) && section != CompetitionSetupSection.INITIAL_DETAILS) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        if (section == null) {
            LOG.error("Invalid section path specified: " + sectionPath);
            return "redirect:/dashboard";
        } else if (section == CompetitionSetupSection.APPLICATION_FORM) {
            return format(APPLICATION_LANDING_REDIRECT, competitionId);
        } else if (section == CompetitionSetupSection.CONTENT) {
            return PUBLIC_CONTENT_LANDING_REDIRECT + competitionId;
        }

        if (competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        model.addAttribute(MODEL, competitionSetupService.populateCompetitionSectionModelAttributes(competition, section));
        model.addAttribute(COMPETITION_SETUP_FORM_KEY, competitionSetupService.getSectionFormData(competition, section));

        checkRestrictionOfInitialDetails(section, competition, model);

        return "competition/setup";
    }

    /**
     * This method is for supporting ajax saving from the competition setup subsections forms.
     */
    @PostMapping("/{competitionId}/section/{sectionPath}/sub/{subsectionPath}/saveFormElement")
    @ResponseBody
    public JsonNode saveFormElement(@RequestParam("fieldName") String fieldName,
                                    @RequestParam("value") String value,
                                    @RequestParam(name = "objectId", required = false) Long objectId,
                                    @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                    @PathVariable(SECTION_PATH_KEY) String sectionPath,
                                    @PathVariable(SUBSECTION_PATH_KEY) String subsectionPath) {

        CompetitionResource competition = competitionService.getById(competitionId);
        CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);
        CompetitionSetupSubsection subsection = CompetitionSetupSubsection.fromPath(subsectionPath);

        try {
            competitionSetupService.autoSaveCompetitionSetupSubsection(
                    competition,
                    section, subsection,
                    fieldName, value,
                    Optional.ofNullable(objectId));
            return createJsonObjectNode(true);
        } catch (Exception e) {
            LOG.error("exception thrown saving form element", e);
            return createJsonObjectNode(false);
        }
    }

    /**
     * This method is for supporting ajax saving from the competition setup sections forms.
     */
    @PostMapping("/{competitionId}/section/{sectionPath}/saveFormElement")
    @ResponseBody
    public JsonNode saveFormElement(@RequestParam("fieldName") String fieldName,
                                    @RequestParam("value") String value,
                                    @RequestParam(name = "objectId", required = false) Long objectId,
                                    @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                    @PathVariable(SECTION_PATH_KEY) String sectionPath) {

        CompetitionResource competition = competitionService.getById(competitionId);
        CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);
        try {
            competitionSetupService.autoSaveCompetitionSetupSection(competition,
                    section,
                    fieldName,
                    value,
                    Optional.ofNullable(objectId));
            return createJsonObjectNode(true);
        } catch (Exception e) {
            LOG.error("exception thrown saving form element", e);
            return createJsonObjectNode(false);
        }
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
        CompetitionResource competition = competitionService.getById(competitionId);
        checkRestrictionOfInitialDetails(CompetitionSetupSection.INITIAL_DETAILS, competition, model);

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.INITIAL_DETAILS, model);
    }

    @PostMapping("/{competitionId}/section/additional")
    public String submitAdditionalSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) AdditionalInfoForm competitionSetupForm,
                                                 BindingResult bindingResult,
                                                 ValidationHandler validationHandler,
                                                 @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                 Model model, HttpServletRequest request) {
        CompetitionResource competition = competitionService.getById(competitionId);

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
        CompetitionResource competition = competitionService.getById(competitionId);

        if ("yes".equals(competitionSetupForm.getMultipleStream()) && StringUtils.isEmpty(competitionSetupForm.getStreamName())) {
            bindingResult.addError(new FieldError(COMPETITION_SETUP_FORM_KEY, "streamName", "A stream name is required"));
        }

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.ELIGIBILITY, model);
    }

    @PostMapping("/{competitionId}/section/milestones")
    public String submitMilestonesSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) MilestonesForm competitionSetupForm,
                                                 BindingResult bindingResult,
                                                 ValidationHandler validationHandler,
                                                 @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                 Model model) {

        CompetitionResource competition = competitionService.getById(competitionId);

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
        CompetitionResource competition = competitionService.getById(competitionId);

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.APPLICATION_FORM, model);
    }


    @PostMapping("/{competitionId}/section/assessors")
    public String submitAssessorsSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) AssessorsForm competitionSetupForm,
                                                @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                ValidationHandler validationHandler,
                                                @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                Model model) {
        CompetitionResource competition = competitionService.getById(competitionId);

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.ASSESSORS, model);
    }

    @PostMapping("/{competitionId}/section/terms-and-conditions")
    public String submitTermsAndConditionsSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) TermsAndConditionsForm competitionSetupForm,
                                                @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                ValidationHandler validationHandler,
                                                @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                                Model model) {
        CompetitionResource competition = competitionService.getById(competitionId);

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competition, CompetitionSetupSection.TERMS_AND_CONDITIONS, model);
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

        RestResult<Void> deleteResult = competitionSetupRestService.delete(competitionId);

        return validationHandler.addAnyErrors(deleteResult, asGlobalErrors())
                .failNowOrSucceedWith(failureView, () -> "redirect:/dashboard");
    }

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'MANAGE_INNOVATION_LEAD')")
    @GetMapping("/{competitionId}/manage-innovation-leads/find")
    public String manageInnovationLead(@P("competitionId")@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                       Model model,
                                       UserResource loggedInUser) {


        CompetitionResource competition = competitionService.getById(competitionId);

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)){
            return "redirect:/competition/setup/" + competitionId;
        }

        model.addAttribute(MODEL, manageInnovationLeadsModelPopulator.populateModel(competition));

        return "competition/manage-innovation-leads-find";
    }

    @PreAuthorize("hasPermission(#competitionId,'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'MANAGE_INNOVATION_LEAD')")
    @GetMapping("/{competitionId}/manage-innovation-leads/overview")
    public String manageInnovationLeadOverview(@P("competitionId") @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                       Model model,
                                       UserResource loggedInUser) {

        CompetitionResource competition = competitionService.getById(competitionId);

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)){
            return "redirect:/competition/setup/" + competitionId;
        }

        model.addAttribute(MODEL, manageInnovationLeadsModelPopulator.populateModel(competition));

        return "competition/manage-innovation-leads-overview";
    }

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'MANAGE_INNOVATION_LEAD')")
    @PostMapping("/{competitionId}/add-innovation-lead/{innovationLeadUserId}")
    public String addInnovationLead(@P("competitionId")@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                    @PathVariable("innovationLeadUserId") long innovationLeadUserId,
                                    Model model,
                                    UserResource loggedInUser) {

        CompetitionResource competition = competitionService.getById(competitionId);

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)){
            return "redirect:/competition/setup/" + competitionId;
        }

        competitionService.addInnovationLead(competitionId, innovationLeadUserId);
        model.addAttribute(MODEL, manageInnovationLeadsModelPopulator.populateModel(competition));

        return "competition/manage-innovation-leads-find";
    }

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionCompositeId', 'MANAGE_INNOVATION_LEAD')")
    @PostMapping("/{competitionId}/remove-innovation-lead/{innovationLeadUserId}")
    public String removeInnovationLead(@P("competitionId") @PathVariable(COMPETITION_ID_KEY) long competitionId,
                                       @PathVariable("innovationLeadUserId") long innovationLeadUserId,
                                       Model model,
                                       UserResource loggedInUser) {

        CompetitionResource competition = competitionService.getById(competitionId);

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competitionId)){
            return "redirect:/competition/setup/" + competitionId;
        }

        competitionService.removeInnovationLead(competitionId, innovationLeadUserId);
        model.addAttribute(MODEL, manageInnovationLeadsModelPopulator.populateModel(competition));

        return "competition/manage-innovation-leads-overview";
    }


    /* AJAX Function */
    @GetMapping("/{competitionId}/generateCompetitionCode")
    @ResponseBody
    public JsonNode generateCompetitionCode(@PathVariable(COMPETITION_ID_KEY) long competitionId) {

        CompetitionResource competition = competitionService.getById(competitionId);
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
            return "redirect:/non-ifs-competition/setup/" + competition.getId();
        }

        if (!competitionSetupService.isInitialDetailsCompleteOrTouched(competition.getId()) && section != CompetitionSetupSection.INITIAL_DETAILS) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        Supplier<String> successView = () -> "redirect:/competition/setup/" + competition.getId() + "/section/" + section.getPath();
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

    private void checkRestrictionOfInitialDetails(CompetitionSetupSection section,
                                                  CompetitionResource competitionResource,
                                                  Model model) {
        if (section == CompetitionSetupSection.INITIAL_DETAILS &&
                competitionSetupService.isInitialDetailsCompleteOrTouched(competitionResource.getId())) {
            model.addAttribute(RESTRICT_INITIAL_DETAILS_EDIT, Boolean.TRUE);
        }
    }
}
