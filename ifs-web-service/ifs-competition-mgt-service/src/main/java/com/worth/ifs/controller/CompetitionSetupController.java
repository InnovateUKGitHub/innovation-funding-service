package com.worth.ifs.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionResource.Status;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.CompetitionSetupForm;
import com.worth.ifs.controller.form.CompetitionSetupInitialDetailsForm;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.UserService;


/**
 * Controller for showing and handling the different competition setup sections
 */
@Controller
@RequestMapping("/competition/setup")
public class CompetitionSetupController {

    private static final Log LOG = LogFactory.getLog(CompetitionSetupController.class);
    @Autowired
    private CompetitionService competitionService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value = "/{competitionId}", method = RequestMethod.GET)
    public String initCompetitionSetupSection(Model model, @PathVariable("competitionId") Long competitionId) {

        CompetitionResource competition = competitionService.getById(competitionId);

        if(competition == null || !Status.COMPETITION_SETUP.equals(competition.getCompetitionStatus())) {
        	LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
        }

        return "redirect:/competition/setup/" + competitionId + "/section/initial";
    }


    @RequestMapping(value = "/{competitionId}/section/initial", method = RequestMethod.GET)
    public String editCompetitionSetupSection(Model model, @PathVariable("competitionId") Long competitionId) {

        CompetitionResource competition = competitionService.getById(competitionId);

        if(competition == null || !Status.COMPETITION_SETUP.equals(competition.getCompetitionStatus())) {
        	LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
        }

        populateCompetitionSectionModelAttributes(model, competition, CompetitionSetupSection.INITIAL_DETAILS);
        model.addAttribute("competitionSetupForm", getSectionFormData(competition, CompetitionSetupSection.INITIAL_DETAILS));

        return "competition/setup";
    }
    
    @RequestMapping(value = "/{competitionId}/section/initial", method = RequestMethod.POST)
    public String submitSectionInitialDetails(@Valid @ModelAttribute("competitionSetupForm") CompetitionSetupInitialDetailsForm competitionSetupForm,
                                              BindingResult bindingResult,
                                              @PathVariable("competitionId") Long competitionId,
                                              Model model, HttpServletRequest request) {

        return genericCompetitionSetupSection(competitionSetupForm, bindingResult, competitionId, CompetitionSetupSection.INITIAL_DETAILS, model);
    }
    
    @RequestMapping(value = "/{competitionId}/section/initial/edit", method = RequestMethod.POST)
    public String setSectionAsIncomplete(@PathVariable("competitionId") Long competitionId) {

        competitionService.setSetupSectionMarkedAsIncomplete(competitionId, CompetitionSetupSection.INITIAL_DETAILS);

        return "redirect:/competition/setup/" + competitionId + "/section/initial";
    }
    
    /* AJAX Function */
    @RequestMapping(value = "/getInnovationArea/{innovationSectorId}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<CategoryResource> getInnovationAreas(@PathVariable("innovationSectorId") Long innovationSectorId) {

        return categoryService.getCategoryByParentId(innovationSectorId);
    }

    /* AJAX Function */
    @RequestMapping(value = "/{competitionId}/generateCompetitionCode", method = RequestMethod.GET)
    public
    @ResponseBody
    String generateCompetitionCode(@PathVariable("competitionId") Long competitionId, HttpServletRequest request) {

        LocalDateTime openingDate = LocalDateTime.of(Integer.parseInt(request.getParameter("year")),
                Integer.parseInt(request.getParameter("month")),
                Integer.parseInt(request.getParameter("day")),
                0, 0, 0);
        return competitionService.generateCompetitionCode(competitionId, openingDate);
    }

    private CompetitionSetupForm getSectionFormData(CompetitionResource competitionResource, CompetitionSetupSection section) {
        CompetitionSetupForm competitionSetupForm;

        switch (section) {
            case INITIAL_DETAILS:
                competitionSetupForm = fillFirstSectionFormSection(competitionResource);
                break;
            default:
                competitionSetupForm = fillFirstSectionFormSection(competitionResource);
                break;
        }


        return competitionSetupForm;
    }

    private CompetitionSetupForm fillFirstSectionFormSection(CompetitionResource competitionResource) {
        CompetitionSetupInitialDetailsForm competitionSetupForm = new CompetitionSetupInitialDetailsForm();

        competitionSetupForm.setCompetitionTypeId(competitionResource.getCompetitionType());
        competitionSetupForm.setExecutiveUserId(competitionResource.getExecutive());

        competitionSetupForm.setInnovationAreaCategoryId(competitionResource.getInnovationArea());
        competitionSetupForm.setLeadTechnologistUserId(competitionResource.getLeadTechnologist());


        if (competitionResource.getStartDate() != null) {
            competitionSetupForm.setOpeningDateDay(competitionResource.getStartDate().getDayOfMonth());
            competitionSetupForm.setOpeningDateMonth(competitionResource.getStartDate().getMonth().getValue());
            competitionSetupForm.setOpeningDateYear(competitionResource.getStartDate().getYear());
        }

        competitionSetupForm.setCompetitionCode(competitionResource.getCode());
        competitionSetupForm.setPafNumber(competitionResource.getPafCode());
        competitionSetupForm.setTitle(competitionResource.getName());
        competitionSetupForm.setBudgetCode(competitionResource.getBudgetCode());

        return competitionSetupForm;
    }

    private String genericCompetitionSetupSection(CompetitionSetupForm competitionSetupForm, BindingResult bindingResult, Long competitionId, CompetitionSetupSection section, Model model) {
        CompetitionResource competition = competitionService.getById(competitionId);

        if(competition == null || !Status.COMPETITION_SETUP.equals(competition.getCompetitionStatus())) {
        	LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
        }


        if (!bindingResult.hasErrors()) {
            saveCompetitionSetupSection(competitionSetupForm, competition, section);
        } else {
            LOG.debug("Form errors");
        }

        populateCompetitionSectionModelAttributes(model, competition, section);

        return "competition/setup";
    }

    private void saveCompetitionSetupSection(CompetitionSetupForm competitionSetupForm, CompetitionResource competitionResource, CompetitionSetupSection section) {
        switch (section) {
            case INITIAL_DETAILS:
                saveInitialDetailSection((CompetitionSetupInitialDetailsForm) competitionSetupForm, competitionResource);
                break;
        }

        competitionService.setSetupSectionMarkedAsComplete(competitionResource.getId(), section);
    }

    private void saveInitialDetailSection(CompetitionSetupInitialDetailsForm competitionSetupForm, CompetitionResource competition) {
        competition.setName(competitionSetupForm.getTitle());
        competition.setBudgetCode(competitionSetupForm.getBudgetCode());
        competition.setExecutive(competitionSetupForm.getExecutiveUserId());

        try {
            LocalDateTime startDate = LocalDateTime.of(competitionSetupForm.getOpeningDateYear(), competitionSetupForm.getOpeningDateMonth(), competitionSetupForm.getOpeningDateDay(), 0, 0);
            competition.setStartDate(startDate);
        } catch (Exception e) {
            competition.setStartDate(null);
        }
        competition.setCompetitionType(competitionSetupForm.getCompetitionTypeId());
        competition.setLeadTechnologist(competitionSetupForm.getLeadTechnologistUserId());
        competition.setPafCode(competitionSetupForm.getPafNumber());

        competition.setInnovationArea(competitionSetupForm.getInnovationAreaCategoryId());
        competition.setInnovationSector(competitionSetupForm.getInnovationSectorCategoryId());

        competitionService.update(competition);

        competitionSetupForm.setCompetitionCode(competition.getCode());
    }

    private void populateCompetitionSectionModelAttributes(Model model,
                                                           CompetitionResource competitionResource,
                                                           CompetitionSetupSection section) {
        List<CompetitionSetupSection> completedSections = competitionService.getCompletedCompetitionSetupSectionStatusesByCompetitionId(competitionResource.getId());

        model.addAttribute("competition", competitionResource);
        model.addAttribute("currentSection", section);
        model.addAttribute("currentSectionFragment", "section-" + section.getPath());
        model.addAttribute("editable", !completedSections.contains(section));
        model.addAttribute("allSections", CompetitionSetupSection.values());
        model.addAttribute("allCompletedSections", completedSections);
        model.addAttribute("subTitle", (competitionResource.getCode() != null ? competitionResource.getCode() : "Unknown") + ": " + (competitionResource.getName() != null ? competitionResource.getName() : "Unknown"));

        model.addAttribute("competitionExecutiveUsers", userService.findUserByType(UserRoleType.COMP_EXEC));
        model.addAttribute("innovationSectors", categoryService.getCategoryByType(CategoryType.INNOVATION_SECTOR));
        if (competitionResource.getInnovationSector() != null) {
            model.addAttribute("innovationAreas", categoryService.getCategoryByParentId(competitionResource.getInnovationSector()));
        } else {
            model.addAttribute("innovationAreas", categoryService.getCategoryByType(CategoryType.INNOVATION_AREA));
        }
        model.addAttribute("competitionTypes", competitionService.getAllCompetitionTypes());
        model.addAttribute("competitionLeadTechUsers", userService.findUserByType(UserRoleType.COMP_TECHNOLOGIST));
    }

}
