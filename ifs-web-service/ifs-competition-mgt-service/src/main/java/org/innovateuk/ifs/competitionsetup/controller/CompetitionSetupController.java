package org.innovateuk.ifs.competitionsetup.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.CharMatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.*;
import org.innovateuk.ifs.competitionsetup.form.InitialDetailsForm.Unrestricted;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.viewmodel.ManageInnovationLeadsViewModel;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserService;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupApplicationController.APPLICATION_LANDING_REDIRECT;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.asGlobalErrors;
import static org.innovateuk.ifs.controller.ErrorToObjectErrorConverterFactory.fieldErrorsToFieldErrors;

/**
 * Controller for showing and handling the different competition setup sections
 */
@Controller
@RequestMapping("/competition/setup")
@PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
public class CompetitionSetupController {
    private static final Log LOG = LogFactory.getLog(CompetitionSetupController.class);
    public static final String COMPETITION_ID_KEY = "competitionId";
    public static final String COMPETITION_SETUP_FORM_KEY = "competitionSetupForm";
    private static final String SECTION_PATH_KEY = "sectionPath";
    private static final String SUBSECTION_PATH_KEY = "subsectionPath";
    public static final String COMPETITION_NAME_KEY = "competitionName";
    public static final String PUBLIC_CONTENT_LANDING_REDIRECT = "redirect:/competition/setup/public-content/";

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    @Autowired
    private UserService userService;

    private static final String READY_TO_OPEN_KEY = "readyToOpen";

    private static final String RESTRICT_INITIAL_DETAILS_EDIT = "restrictInitialDetailsEdit";

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @GetMapping("/{competitionId}")
    public String initCompetitionSetupSection(Model model, @PathVariable(COMPETITION_ID_KEY) long competitionId) {

        CompetitionResource competition = competitionService.getById(competitionId);
        if (competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        CompetitionSetupSection section = CompetitionSetupSection.fromPath("home");
        competitionSetupService.populateCompetitionSectionModelAttributes(model, competition, section);
        model.addAttribute(READY_TO_OPEN_KEY, competitionSetupService.isCompetitionReadyToOpen(competition));
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

        if (!competition.isInitialDetailsComplete() && section != CompetitionSetupSection.INITIAL_DETAILS) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        competitionService.setSetupSectionMarkedAsIncomplete(competitionId, section).getSuccessObjectOrThrowException();
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

        if (!competition.isInitialDetailsComplete() && section != CompetitionSetupSection.INITIAL_DETAILS) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        if (section == null) {
            LOG.error("Invalid section path specified: " + sectionPath);
            return "redirect:/dashboard";
        } else if (section == CompetitionSetupSection.APPLICATION_FORM) {
            return String.format(APPLICATION_LANDING_REDIRECT, competitionId);
        } else if (section == CompetitionSetupSection.CONTENT) {
            return PUBLIC_CONTENT_LANDING_REDIRECT + competitionId;
        }

        if (competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        competitionSetupService.populateCompetitionSectionModelAttributes(model, competition, section);
        model.addAttribute("competitionSetupForm", competitionSetupService.getSectionFormData(competition, section));

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
            return createJsonObjectNode(false);
        }
    }

    @PostMapping(value = "/{competitionId}/section/initial", params = "unrestricted")
    public String submitUnrestrictedInitialSectionDetails(
            @Validated({Unrestricted.class, Default.class}) @Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) InitialDetailsForm competitionSetupForm,
            @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
            ValidationHandler validationHandler,
            @PathVariable(COMPETITION_ID_KEY) long competitionId,
            Model model
    ) {
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
                String competitionCode = competitionService.generateCompetitionCode(competitionId, competition.getStartDate());
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
            bindingResult.addError(new FieldError("competitionSetupForm", "streamName", "A stream name is required"));
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

    @GetMapping("/{competitionId}/ready-to-open")
    public String setAsReadyToOpen(@PathVariable(COMPETITION_ID_KEY) long competitionId) {
        competitionSetupService.setCompetitionAsReadyToOpen(competitionId);
        return String.format("redirect:/competition/setup/%d", competitionId);
    }

    @GetMapping("/{competitionId}/manage-innovation-leads")
    public String manageInnovationLead(@PathVariable(COMPETITION_ID_KEY) long competitionId,
                                       Model model,
                                       UserResource loggedInUser) {

        List<UserResource> allInnovationLeads = userService.findUserByType(UserRoleType.INNOVATION_LEAD);

        List<UserResource> innovationLeadsAssignedToCompetition = competitionService.findInnovationLeads(competitionId);

        //TODO - Just initial test code. Needs to be altered.
        List<UserResource> availableInnovationLeads = new ArrayList<>();
        availableInnovationLeads.addAll(allInnovationLeads);
        availableInnovationLeads.removeAll(innovationLeadsAssignedToCompetition);

        model.addAttribute("model", new ManageInnovationLeadsViewModel(allInnovationLeads, innovationLeadsAssignedToCompetition, availableInnovationLeads));

        return "competition/manage-innovation-leads";
    }



    /* AJAX Function */
    @GetMapping("/{competitionId}/generateCompetitionCode")
    @ResponseBody
    public JsonNode generateCompetitionCode(@PathVariable(COMPETITION_ID_KEY) long competitionId) {

        CompetitionResource competition = competitionService.getById(competitionId);
        if (competition.getStartDate() != null) {
            return this.createJsonObjectNode(true, competitionService.generateCompetitionCode(competitionId, competition.getStartDate()));
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

        if (!competition.isInitialDetailsComplete() && section != CompetitionSetupSection.INITIAL_DETAILS) {
            return "redirect:/competition/setup/" + competition.getId();
        }

        Supplier<String> successView = () -> "redirect:/competition/setup/" + competition.getId() + "/section/" + section.getPath();
        Supplier<String> failureView = () -> {
            competitionSetupService.populateCompetitionSectionModelAttributes(model, competition, section);
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
                competitionResource.isInitialDetailsComplete()) {
            model.addAttribute(RESTRICT_INITIAL_DETAILS_EDIT, Boolean.TRUE);
        }
    }
}
