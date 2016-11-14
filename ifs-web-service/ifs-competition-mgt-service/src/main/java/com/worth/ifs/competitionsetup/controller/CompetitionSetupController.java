package com.worth.ifs.competitionsetup.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.CharMatcher;
import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.competition.resource.CompetitionFunderResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.competition.resource.CompetitionSetupSubsection;
import com.worth.ifs.competitionsetup.form.*;
import com.worth.ifs.competitionsetup.service.CompetitionSetupMilestoneService;
import com.worth.ifs.competitionsetup.service.CompetitionSetupQuestionService;
import com.worth.ifs.competitionsetup.service.CompetitionSetupService;
import com.worth.ifs.competitionsetup.viewmodel.FunderViewModel;
import com.worth.ifs.profiling.ProfileExecution;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.worth.ifs.competitionsetup.controller.CompetitionSetupApplicationController.APPLICATION_LANDING_REDIRECT;
import static com.worth.ifs.competitionsetup.utils.CompetitionUtils.isSendToDashboard;
import static com.worth.ifs.controller.ErrorLookupHelper.lookupErrorMessageResourceBundleEntry;
import static java.util.stream.Collectors.toList;

/**
 * Controller for showing and handling the different competition setup sections
 */
@Controller
@RequestMapping("/competition/setup")
public class CompetitionSetupController {

    private static final Log LOG = LogFactory.getLog(CompetitionSetupController.class);
    public static final String COMPETITION_ID_KEY = "competitionId";
    public static final String COMPETITION_SETUP_FORM_KEY = "competitionSetupForm";
    private static final String SECTION_PATH_KEY = "sectionPath";
    private static final String SUBSECTION_PATH_KEY = "subsectionPath";

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionSetupQuestionService competitionSetupQuestionService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CompetitionSetupMilestoneService competitionSetupMilestoneService;

    private static final String READY_TO_OPEN_KEY = "readyToOpen";

    private static final String RESTRICT_INITIAL_DETAILS_EDIT = "restrictInitialDetailsEdit";

    @Autowired
    private Validator validator;

    @RequestMapping(value = "/{competitionId}", method = RequestMethod.GET)
    public String initCompetitionSetupSection(Model model, @PathVariable(COMPETITION_ID_KEY) Long competitionId) {

        CompetitionResource competition = competitionService.getById(competitionId);

        if(isSendToDashboard(competition)) {
        	LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
        }

        CompetitionSetupSection section = CompetitionSetupSection.fromPath("home");
        competitionSetupService.populateCompetitionSectionModelAttributes(model, competition, section);

        model.addAttribute(READY_TO_OPEN_KEY, competitionSetupService.isCompetitionReadyToOpen(competition));
        return "competition/setup";
    }

    @RequestMapping(value = "/{competitionId}/section/{sectionPath}/edit", method = RequestMethod.POST)
    public String setSectionAsIncomplete(@PathVariable(COMPETITION_ID_KEY) Long competitionId, @PathVariable(SECTION_PATH_KEY) String sectionPath) {
    	CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);
    	if(section == null) {
    		LOG.error("Invalid section path specified: " + sectionPath);
            return "redirect:/dashboard";
    	}

        competitionService.setSetupSectionMarkedAsIncomplete(competitionId, section);
        competitionSetupService.setCompetitionAsCompetitionSetup(competitionId);

        return "redirect:/competition/setup/" + competitionId + "/section/" + section.getPath();
    }

    @RequestMapping(value = "/{competitionId}/section/{sectionPath}", method = RequestMethod.GET)
    public String editCompetitionSetupSection(@PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                              @PathVariable(SECTION_PATH_KEY) String sectionPath,
                                              Model model) {

        CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);
        if(section == null) {
            LOG.error("Invalid section path specified: " + sectionPath);
            return "redirect:/dashboard";
        } else if (section == CompetitionSetupSection.APPLICATION_FORM) {
            return String.format(APPLICATION_LANDING_REDIRECT, competitionId);
        }

        CompetitionResource competition = competitionService.getById(competitionId);

        if(isSendToDashboard(competition)) {
        	LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
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
    @RequestMapping(value = "/{competitionId}/section/{sectionPath}/sub/{subsectionPath}/saveFormElement", method = RequestMethod.POST)
    @ResponseBody
    public JsonNode saveFormElement(@RequestParam("fieldName") String fieldName,
                                    @RequestParam("value") String value,
                                    @RequestParam(name = "objectId", required = false) Long objectId,
                                    @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                    @PathVariable(SECTION_PATH_KEY) String sectionPath,
                                    @PathVariable(SUBSECTION_PATH_KEY) String subsectionPath,
                                    HttpServletRequest request) {

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
                    )
            );

            return this.createJsonObjectNode(errors.isEmpty(), errors);
        } catch (Exception e) {
            errors.add(e.getMessage());
            return this.createJsonObjectNode(false, errors);
        }
    }


    /**
     * This method is for supporting ajax saving from the competition setup sections forms.
     */
    @ProfileExecution
    @RequestMapping(value = "/{competitionId}/section/{sectionPath}/saveFormElement", method = RequestMethod.POST)
    @ResponseBody
    public JsonNode saveFormElement(@RequestParam("fieldName") String fieldName,
                                    @RequestParam("value") String value,
                                    @RequestParam(name = "objectId", required = false) Long objectId,
                                    @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                    @PathVariable(SECTION_PATH_KEY) String sectionPath,
                                    HttpServletRequest request) {

        CompetitionResource competitionResource = competitionService.getById(competitionId);
        CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);

        List<String> errors = new ArrayList<>();
        try {
            errors = toStringList(competitionSetupService.autoSaveCompetitionSetupSection(competitionResource, section, fieldName, value, Optional.ofNullable(objectId)));

            return this.createJsonObjectNode(errors.isEmpty(), errors);
        } catch (Exception e) {
            errors.add(e.getMessage());
            return this.createJsonObjectNode(false, errors);
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

    @RequestMapping(value = "/{competitionId}/section/initial", method = RequestMethod.POST)
    public String submitInitialSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) InitialDetailsForm competitionSetupForm,
                                              BindingResult bindingResult,
                                              @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                              Model model) {
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        checkRestrictionOfInitialDetails(CompetitionSetupSection.INITIAL_DETAILS, competitionResource, model);
        return genericCompetitionSetupSection(competitionSetupForm, bindingResult, competitionId, CompetitionSetupSection.INITIAL_DETAILS, model);
    }

    @RequestMapping(value = "/{competitionId}/section/additional", method = RequestMethod.POST)
    public String submitAdditionalSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) AdditionalInfoForm competitionSetupForm,
                                              BindingResult bindingResult,
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
            List<FunderViewModel> funders = competitionSetupForm.getFunders();
            funders.add(new FunderViewModel(new CompetitionFunderResource()));
            competitionSetupForm.setFunders(funders);
            competitionSetupForm.setMarkAsCompleteAction(false);
        } else if (request.getParameterMap().containsKey("remove-funder")) {
            int removeCoFunderIndex = Integer.valueOf(request.getParameterMap().get("remove-cofunder")[0]);
            competitionSetupForm.getFunders().remove(removeCoFunderIndex);
            competitionSetupForm.setMarkAsCompleteAction(false);
        }

        //Validate after competition code generated and co funders added/removed.
        validator.validate(competitionSetupForm, bindingResult);

        return genericCompetitionSetupSection(competitionSetupForm, bindingResult, competitionId, CompetitionSetupSection.ADDITIONAL_INFO, model);
    }

    @RequestMapping(value = "/{competitionId}/section/eligibility", method = RequestMethod.POST)
    public String submitEligibilitySectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) EligibilityForm competitionSetupForm,
                                              BindingResult bindingResult,
                                              @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                              Model model) {

        return genericCompetitionSetupSection(competitionSetupForm, bindingResult, competitionId, CompetitionSetupSection.ELIGIBILITY, model);
    }

    @RequestMapping(value = "/{competitionId}/section/milestones", method = RequestMethod.POST)
    public String submitMilestonesSectionDetails(@Valid @ModelAttribute(COMPETITION_SETUP_FORM_KEY) MilestonesForm competitionSetupForm,
                                              BindingResult bindingResult,
                                              @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                              Model model) {
        if (bindingResult.hasErrors()) {
            competitionSetupMilestoneService.sortMilestones(competitionSetupForm);
        }
        return genericCompetitionSetupSection(competitionSetupForm, bindingResult, competitionId, CompetitionSetupSection.MILESTONES, model);
    }

    @RequestMapping(value = "/{competitionId}/section/application", method = RequestMethod.POST)
    public String submitApplicationFormSectionDetails(@ModelAttribute(COMPETITION_SETUP_FORM_KEY) ApplicationFormForm competitionSetupForm,
                                                      BindingResult bindingResult,
                                                      @PathVariable(COMPETITION_ID_KEY) Long competitionId,
                                                      Model model) {

        return genericCompetitionSetupSection(competitionSetupForm, bindingResult, competitionId, CompetitionSetupSection.APPLICATION_FORM, model);
    }

    @RequestMapping(value = "/{competitionId}/ready-to-open", method = RequestMethod.GET)
    public String setAsReadyToOpen(@PathVariable(COMPETITION_ID_KEY) Long competitionId) {
        competitionSetupService.setCompetitionAsReadyToOpen(competitionId);
        return String.format("redirect:/competition/setup/%d", competitionId);
    }


    /* AJAX Function */
    @RequestMapping(value = "/getInnovationArea/{innovationSectorId}", method = RequestMethod.GET)
    @ResponseBody
    public List<CategoryResource> getInnovationAreas(@PathVariable("innovationSectorId") Long innovationSectorId) {

        return categoryService.getCategoryByParentId(innovationSectorId);
    }

    /* AJAX Function */
    @RequestMapping(value = "/{competitionId}/generateCompetitionCode", method = RequestMethod.GET)
    @ResponseBody
    public JsonNode generateCompetitionCode(@PathVariable(COMPETITION_ID_KEY) Long competitionId, HttpServletRequest request) {

        CompetitionResource competition = competitionService.getById(competitionId);
        if (competition.getStartDate() != null) {
            return this.createJsonObjectNode(true, competitionService.generateCompetitionCode(competitionId, competition.getStartDate()));
        }
        else {
            return this.createJsonObjectNode(false, "Please set a start date for your competition before generating the competition code, you can do this in the Initial Details section");
        }
    }

    private String genericCompetitionSetupSection(CompetitionSetupForm competitionSetupForm, BindingResult bindingResult, Long competitionId, CompetitionSetupSection section, Model model) {
        CompetitionResource competition = competitionService.getById(competitionId);

        if(isSendToDashboard(competition)) {
        	LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
        }

        if (isSuccessfulSaved(competitionSetupForm, competition, section, bindingResult)) {
            return "redirect:/competition/setup/" + competitionId + "/section/" + section.getPath();
        } else {
            LOG.debug("Form errors");
            competitionSetupService.populateCompetitionSectionModelAttributes(model, competition, section);
            return "competition/setup";
        }
    }

    private Boolean isSuccessfulSaved(CompetitionSetupForm competitionSetupForm, CompetitionResource competition, CompetitionSetupSection section, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return false;
        }

        List<Error> saveSectionResult = competitionSetupService.saveCompetitionSetupSection(competitionSetupForm, competition, section);
        if(saveSectionResult != null && !saveSectionResult.isEmpty()) {
            saveSectionResult.forEach(e -> {
                if(e.getFieldName() != null) {
                    bindingResult.rejectValue(e.getFieldName(), e.getErrorKey());
                } else {
                    ObjectError error = new ObjectError("currentSection", new String[] {e.getErrorKey()}, null, null);
                    bindingResult.addError(error);
                }
            });
        }

        return !bindingResult.hasErrors();
    }

    private ObjectNode createJsonObjectNode(boolean success, String message) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", success ? "true" : "false");
        node.put("message", CharMatcher.is('\"').trimFrom(message));

        return node;
    }

    private ObjectNode createJsonObjectNode(boolean success, List<String> errors) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", success ? "true" : "false");

        if (!success) {
            ArrayNode errorsNode = mapper.createArrayNode();
            errors.stream().forEach(errorsNode::add);
            node.set("validation_errors", errorsNode);
        }

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
