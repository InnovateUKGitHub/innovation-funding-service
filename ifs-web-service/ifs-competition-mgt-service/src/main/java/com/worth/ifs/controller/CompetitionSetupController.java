package com.worth.ifs.controller;

import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.category.resource.CategoryResource;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.controller.form.CompetitionSetupForm;
import com.worth.ifs.controller.form.CompetitionSetupInitialDetailsForm;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/competition/setup")
public class CompetitionSetupController {


    private static final String SECTION_ONE = "Initial details";

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    private static final Log LOG = LogFactory.getLog(CompetitionSetupController.class);

    @RequestMapping(value = "/{competitionId}", method = RequestMethod.GET)
    public String initCompetitionSetupSection(Model model, @PathVariable("competitionId") Long competitionId){

        List<CompetitionSetupSectionResource> sections = competitionService.getCompetitionSetupSectionsByCompetitionId(competitionId);

        if(sections.size() > 0) {
            return "redirect:/competition/setup/" + competitionId + "/section/" + sections.get(0).getId();
        } else {
            LOG.error("Competition is not found");
            return "redirect:/dashboard";
        }
    }


    @RequestMapping(value = "/{competitionId}/section/{section}", method = RequestMethod.GET)
    public String editCompetitionSetupSection(Model model, @PathVariable("competitionId") Long competitionId, @PathVariable("section") Long sectionId){

        List<CompetitionSetupSectionResource> sections = competitionService.getCompetitionSetupSectionsByCompetitionId(competitionId);
        Optional<CompetitionSetupSectionResource> competitionSetupSection = findCompetitionSetupSection(sections, sectionId);
        CompetitionResource competition = competitionService.getById(competitionId);

        if(!competitionSetupSection.isPresent()) {
            LOG.error("Competition setup section is not found");
            return "redirect:/dashboard";
        }

		populateCompetitionSectionModelAttributes(model, competition, competitionSetupSection.get(), sections);
        model.addAttribute("competitionSetupForm", getSectionFormData(competition, competitionSetupSection.get()));

		return "competition/setup";
    }

    private CompetitionSetupForm getSectionFormData(CompetitionResource competitionResource, CompetitionSetupSectionResource competitionSetupSectionResource) {
        CompetitionSetupForm competitionSetupForm;

        switch (competitionSetupSectionResource.getName()) {
            case SECTION_ONE:
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
        competitionSetupForm.setInnovationSectorCategoryId(competitionResource.getInnovationSector());
        competitionSetupForm.setLeadTechnologistUserId(competitionResource.getLeadTechnologist());


        if(competitionResource.getStartDate() != null) {
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

    @RequestMapping(value = "/{competitionId}/section/{sectionId}/edit", method = RequestMethod.GET)
    public String submitSectionInitialDetails(@PathVariable("competitionId") Long competitionId,
                                              @PathVariable("sectionId") Long sectionId){

        competitionService.setSetupSectionMarkedAsIncomplete(competitionId, sectionId);

        return "redirect:/competition/setup/" + competitionId + "/section/" + sectionId;
    }

    /* AJAX Function */
    @RequestMapping(value = "/getInnovationArea/{innovationSectorId}", method = RequestMethod.GET)
    public @ResponseBody List<CategoryResource> getInnovationAreas(@PathVariable("innovationSectorId") Long innovationSectorId){

        return categoryService.getCategoryByParentId(innovationSectorId);
    }

    /* AJAX Function */
    @RequestMapping(value = "/{competitionId}/generateCompetitionCode", method = RequestMethod.GET)
    public @ResponseBody String generateCompetionCode(@PathVariable("competitionId") Long competitionId, HttpServletRequest request){

        LocalDateTime openingDate = LocalDateTime.of(Integer.parseInt(request.getParameter("year")),
                Integer.parseInt(request.getParameter("month")),
                Integer.parseInt(request.getParameter("day")),
                0, 0, 0);
        return competitionService.generateCompetitionCode(competitionId, openingDate);
    }


    @RequestMapping(value = "/{competitionId}/section/1", method = RequestMethod.POST)
    public String submitSectionInitialDetails(@Valid @ModelAttribute("competitionSetupForm") CompetitionSetupInitialDetailsForm competitionSetupForm,
                                              BindingResult bindingResult,
                                              @PathVariable("competitionId") Long competitionId,
                                              Model model, HttpServletRequest request){

        return genericCompetitionSetupSection(competitionSetupForm, bindingResult, competitionId, 1L, model);
    }

    private String genericCompetitionSetupSection(CompetitionSetupForm competitionSetupForm, BindingResult bindingResult, Long competitionId, Long sectionId, Model model) {
        List<CompetitionSetupSectionResource> sections = competitionService.getCompetitionSetupSectionsByCompetitionId(competitionId);
        Optional<CompetitionSetupSectionResource> competitionSetupSection = findCompetitionSetupSection(sections, sectionId);
        CompetitionResource competition = competitionService.getById(competitionId);

        if(!competitionSetupSection.isPresent()) {
            LOG.error("Competition setup section is not found");
            return "redirect:/dashboard";
        }


        if(!bindingResult.hasErrors()) {
            saveCompetitionSetupSection(competitionSetupForm, competition, competitionSetupSection.get());
        } else {
            LOG.debug("Form errors");
        }

        populateCompetitionSectionModelAttributes(model, competition, competitionSetupSection.get(), sections);

        return "competition/setup";
    }

    private void saveCompetitionSetupSection(CompetitionSetupForm competitionSetupForm, CompetitionResource competitionResource, CompetitionSetupSectionResource competitionSetupSectionResource) {
        switch (competitionSetupSectionResource.getName()) {
            case SECTION_ONE:
                saveInitialDetailSection((CompetitionSetupInitialDetailsForm) competitionSetupForm, competitionResource);
                break;
        }

        competitionService.setSetupSectionMarkedAsComplete(competitionResource.getId(), competitionSetupSectionResource.getId());

    }

    private void saveInitialDetailSection(CompetitionSetupInitialDetailsForm competitionSetupForm, CompetitionResource competition) {
        competition.setName(competitionSetupForm.getTitle());
        competition.setBudgetCode(competitionSetupForm.getBudgetCode());
        competition.setCode(competitionSetupForm.getCompetitionCode());
        competition.setExecutive(competitionSetupForm.getExecutiveUserId());

        LocalDateTime startDate = LocalDateTime.of(competitionSetupForm.getOpeningDateYear(), competitionSetupForm.getOpeningDateMonth(), competitionSetupForm.getOpeningDateDay(), 0, 0);
        competition.setStartDate(startDate);
        competition.setCompetitionType(competitionSetupForm.getCompetitionTypeId());
        competition.setLeadTechnologist(competitionSetupForm.getLeadTechnologistUserId());
        competition.setPafCode(competitionSetupForm.getPafNumber());

        competition.setInnovationArea(competitionSetupForm.getInnovationAreaCategoryId());
        competition.setInnovationSector(competitionSetupForm.getInnovationSectorCategoryId());

        competitionService.update(competition);
    }

    private Optional<CompetitionSetupSectionResource> findCompetitionSetupSection(List<CompetitionSetupSectionResource> sections, long sectionId) {
        Optional<CompetitionSetupSectionResource> competitionSetupSection = sections.stream()
                .filter(competitionSetupSectionResource -> competitionSetupSectionResource.getId() == sectionId)
                .findAny();

        return competitionSetupSection;
    }

    private void populateCompetitionSectionModelAttributes(Model model,
                                                           CompetitionResource competitionResource,
                                                           CompetitionSetupSectionResource competitionSetupSectionResource,
                                                           List<CompetitionSetupSectionResource> sections) {
        List<Long> completedSections = competitionService.getCompletedCompetitionSetupSectionStatusesByCompetitionId(competitionResource.getId());

        model.addAttribute("competition", competitionResource);
        model.addAttribute("currentSection", competitionSetupSectionResource.getId());
        model.addAttribute("currentSectionFragment", "section-" + competitionSetupSectionResource.getName().replaceAll(" ", "-").toLowerCase());
        model.addAttribute("editable", !completedSections.contains(competitionSetupSectionResource.getId()));
        model.addAttribute("allSections", sections);
        model.addAttribute("allCompletedSections", completedSections);
        model.addAttribute("subTitle", (competitionResource.getCode() != null ? competitionResource.getCode() : "Unknown") + ": " + (competitionResource.getName() != null ? competitionResource.getName() : "Unknown"));

        model.addAttribute("competitionExecutiveUsers", userService.findUserByType(UserRoleType.COMP_EXEC));
        model.addAttribute("innovationSectors", categoryService.getCategoryByType(CategoryType.INNOVATION_SECTOR));
        model.addAttribute("innovationAreas", categoryService.getCategoryByType(CategoryType.INNOVATION_AREA));
        model.addAttribute("competitionTypes", competitionService.getAllCompetitionTypes());
        model.addAttribute("competitionLeadTechUsers", userService.findUserByType(UserRoleType.COMP_TECHNOLOGIST));
    }

}
