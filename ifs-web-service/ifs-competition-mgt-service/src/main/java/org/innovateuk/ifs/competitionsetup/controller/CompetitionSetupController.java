package org.innovateuk.ifs.competitionsetup.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.CharMatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.service.CategoryService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSubsection;
import org.innovateuk.ifs.competitionsetup.form.*;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import org.innovateuk.ifs.competitionsetup.service.CompetitionSetupService;
import org.innovateuk.ifs.controller.ValidationHandler;
import org.innovateuk.ifs.profiling.ProfileExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.competitionsetup.controller.CompetitionSetupApplicationController.APPLICATION_LANDING_REDIRECT;
import static org.innovateuk.ifs.controller.ErrorLookupHelper.lookupErrorMessageResourceBundleEntry;
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
    private CategoryService categoryService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    private static final String READY_TO_OPEN_KEY = "readyToOpen";

    private static final String RESTRICT_INITIAL_DETAILS_EDIT = "restrictInitialDetailsEdit";

    @Autowired
    @Qualifier("mvcValidator")
    private Validator validator;

    @GetMapping("/{competitionId}")
    public String initCompetitionSetupSection(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId) {

        CompetitionResource competition = competitionService.getById(competitionId);
        if(competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        CompetitionSetupSection section = CompetitionSetupSection.fromPath("home");
        competitionSetupService.populateCompetitionSectionModelAttributes(model, competition, section);
        model.addAttribute(READY_TO_OPEN_KEY, competitionSetupService.isCompetitionReadyToOpen(competition));
        return "competition/setup";
    }

    @PostMapping("/{competitionId}/section/{sectionPath}/edit")
    public String setSectionAsIncomplete(@PathVariable(COMPETITION_ID_KEY) Long competitionId, @PathVariable(SECTION_PATH_KEY) String sectionPath) {
    	CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);
    	if(section == null) {
    		LOG.error("Invalid section path specified: " + sectionPath);
            return "redirect:/dashboard";
    	}

        CompetitionResource competition = competitionService.getById(competitionId);
    	if(section.preventEdit(competition)) {
            LOG.error(String.format("Competition with id %1$d cannot edit section %2$s: ", competitionId, section));
            return "redirect:/dashboard";
        }


        competitionService.setSetupSectionMarkedAsIncomplete(competitionId, section).getSuccessObjectOrThrowException();
        if(!competition.isSetupAndLive()) {
            competitionSetupService.setCompetitionAsCompetitionSetup(competitionId);
        }

        return "redirect:/competition/setup/" + competitionId + "/section/" + section.getPath();
    }

    @GetMapping("/{competitionId}/section/{sectionPath}")
    public String editCompetitionSetupSection(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                              @PathVariable(SECTION_PATH_KEY) String sectionPath,
                                              Model model) {

        CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);
        if(section == null) {
            LOG.error("Invalid section path specified: " + sectionPath);
            return "redirect:/dashboard";
        } else if (section == CompetitionSetupSection.APPLICATION_FORM) {
            return String.format(APPLICATION_LANDING_REDIRECT, competitionId);
        } else if (section == CompetitionSetupSection.CONTENT){
            return PUBLIC_CONTENT_LANDING_REDIRECT + competitionId;
        }

        CompetitionResource competition = competitionService.getById(competitionId);

        if(competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }

        competitionSetupService.populateCompetitionSectionModelAttributes(model, competition, section);
        model.addAttribute("competitionSetupForm", competitionSetupService.getSectionFormData(competition, section));

        if(model.containsAttribute("isInitialComplete")) {
            Map<String, Object> modelMap = model.asMap();

            if(!(Boolean) modelMap.get("isInitialComplete") && !section.equals(CompetitionSetupSection.INITIAL_DETAILS)) {
                LOG.error("User should first fill the initial details");
                return "redirect:/dashboard";
            }
        }

        checkRestrictionOfInitialDetails(section, competition, model);

        return "competition/setup";
    }

    /**
     * This method is for supporting ajax saving from the competition setup subsections forms.
     */
    @ProfileExecution
    @PostMapping("/{competitionId}/section/{sectionPath}/sub/{subsectionPath}/saveFormElement")
    @ResponseBody
    public JsonNode saveFormElement(@RequestParam("fieldName") String fieldName,
                                    @RequestParam("value") String value,
                                    @RequestParam(name = "objectId", required = false) Long objectId,
                                    @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                    @PathVariable(SECTION_PATH_KEY) String sectionPath,
                                    @PathVariable(SUBSECTION_PATH_KEY) String subsectionPath) {

        CompetitionResource competitionResource = competitionService.getById(competitionId);
        CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);
        CompetitionSetupSubsection subsection = CompetitionSetupSubsection.fromPath(subsectionPath);

        List<String> errors = new ArrayList<>();
        try {
            errors = toStringList(competitionSetupService.autoSaveCompetitionSetupSubsection(
                    competitionResource,
                    section, subsection,
                    fieldName, value,
                    Optional.ofNullable(objectId)
                    ).getErrors()
            );
            return this.createJsonObjectNode(true);
        } catch (Exception e) {
            errors.add(e.getMessage());
            return this.createJsonObjectNode(false);
        }
    }


    /**
     * This method is for supporting ajax saving from the competition setup sections forms.
     */
    @ProfileExecution
    @PostMapping("/{competitionId}/section/{sectionPath}/saveFormElement")
    @ResponseBody
    public JsonNode saveFormElement(@RequestParam("fieldName") String fieldName,
                                    @RequestParam("value") String value,
                                    @RequestParam(name = "objectId", required = false) Long objectId,
                                    @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                    @PathVariable(SECTION_PATH_KEY) String sectionPath) {

        CompetitionResource competitionResource = competitionService.getById(competitionId);
        CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);

        List<String> errors = new ArrayList<>();
        try {
            errors = toStringList(competitionSetupService.autoSaveCompetitionSetupSection(competitionResource, section, fieldName, value, Optional.ofNullable(objectId)).getErrors());

            return this.createJsonObjectNode(true);
        } catch (Exception e) {
            errors.add(e.getMessage());
            return this.createJsonObjectNode(false);
        }
    }

    private List<String> toStringList(List<Error> errors) {
        return errors
                .stream()
                .map(this::lookupErrorMessage)
                .collect(toList());
    }

    private String lookupErrorMessage(Error e) {
        return lookupErrorMessageResourceBundleEntry(messageSource, e);
    }

    @PostMapping("/{competitionId}/section/initial")
    public String submitInitialSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) InitialDetailsForm competitionSetupForm,
                                              @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                              ValidationHandler validationHandler,
                                              @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                              Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        checkRestrictionOfInitialDetails(CompetitionSetupSection.INITIAL_DETAILS, competitionResource, model);
        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competitionId, CompetitionSetupSection.INITIAL_DETAILS, model);
    }

    @PostMapping("/{competitionId}/section/additional")
    public String submitAdditionalSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) AdditionalInfoForm competitionSetupForm,
                                              BindingResult bindingResult,
                                              ValidationHandler validationHandler,
                                              @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                              Model model, HttpServletRequest request) {
        if (request.getParameterMap().containsKey("generate-code")) {
            CompetitionResource competition = competitionService.getById(competitionId);
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

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competitionId, CompetitionSetupSection.ADDITIONAL_INFO, model);
    }

    @PostMapping("/{competitionId}/section/eligibility")
    public String submitEligibilitySectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) EligibilityForm competitionSetupForm,
                                              BindingResult bindingResult,
                                              ValidationHandler validationHandler,
                                              @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                              Model model) {

    	if("yes".equals(competitionSetupForm.getMultipleStream()) && StringUtils.isEmpty(competitionSetupForm.getStreamName())){
    		bindingResult.addError(new FieldError("competitionSetupForm", "streamName", "A stream name is required"));
    	}

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competitionId, CompetitionSetupSection.ELIGIBILITY, model);
    }

    @PostMapping("/{competitionId}/section/milestones")
    public String submitMilestonesSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) MilestonesForm competitionSetupForm,
                                              BindingResult bindingResult,
                                              ValidationHandler validationHandler,
                                              @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                              Model model) {
        if (bindingResult.hasErrors()) {
            competitionSetupMilestoneService.sortMilestones(competitionSetupForm);
        }
        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competitionId, CompetitionSetupSection.MILESTONES, model);
    }

    @PostMapping("/{competitionId}/section/application")
    public String submitApplicationFormSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) LandingPageForm competitionSetupForm,
                                                      @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                      ValidationHandler validationHandler,
                                                      @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                                      Model model) {

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competitionId, CompetitionSetupSection.APPLICATION_FORM, model);
    }


    @PostMapping("/{competitionId}/section/assessors")
    public String submitAssessorsSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) AssessorsForm competitionSetupForm,
                                                  @SuppressWarnings("UnusedParameters") BindingResult bindingResult,
                                                  ValidationHandler validationHandler,
                                                  @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                                  Model model) {

        return genericCompetitionSetupSection(competitionSetupForm, validationHandler, competitionId, CompetitionSetupSection.ASSESSORS, model);
    }

    @GetMapping("/{competitionId}/ready-to-open")
    public String setAsReadyToOpen(@PathVariable(COMPETITION_ID_KEY) Long competitionId) {
        competitionSetupService.setCompetitionAsReadyToOpen(competitionId);
        return String.format("redirect:/competition/setup/%d", competitionId);
    }


    /* AJAX Function */
    @GetMapping("/getInnovationArea/{innovationSectorId}")
    @ResponseBody
    public List<InnovationAreaResource> getInnovationAreas(@PathVariable("innovationSectorId") Long innovationSectorId) {

        return categoryService.getInnovationAreasBySector(innovationSectorId);
    }

    /* AJAX Function */
    @GetMapping("/{competitionId}/generateCompetitionCode")
    @ResponseBody
    public JsonNode generateCompetitionCode(@PathVariable(COMPETITION_ID_KEY) Long competitionId) {

        CompetitionResource competition = competitionService.getById(competitionId);
        if (competition.getStartDate() != null) {
            return this.createJsonObjectNode(true, competitionService.generateCompetitionCode(competitionId, competition.getStartDate()));
        }
        else {
            return this.createJsonObjectNode(false, "Please set a start date for your competition before generating the competition code, you can do this in the Initial Details section");
        }
    }

    private String genericCompetitionSetupSection(CompetitionSetupForm competitionSetupForm, ValidationHandler validationHandler, Long competitionId, CompetitionSetupSection section, Model model) {
        CompetitionResource competition = competitionService.getById(competitionId);
        if(competition.isNonIfs()) {
            return "redirect:/non-ifs-competition/setup/" + competitionId;
        }
        Supplier<String> successView = () -> "redirect:/competition/setup/" + competitionId + "/section/" + section.getPath();
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
                competitionResource.getSectionSetupStatus().containsKey(section)) {
            model.addAttribute(RESTRICT_INITIAL_DETAILS_EDIT, Boolean.TRUE);
        }
    }

}
