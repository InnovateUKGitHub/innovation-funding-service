package com.worth.ifs.controller;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSectionResource;
import com.worth.ifs.controller.form.CompetitionSetupForm;
import com.worth.ifs.controller.form.competitionsetup.CompetitionSetupInitialDetailsForm;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/competition/setup/{competitionId}")
public class CompetitionSetupController {
    
	private static final String SECTION_ONE = "Initial details";
	
    @Autowired
    private CompetitionService competitionService;

    private static final Log LOG = LogFactory.getLog(CompetitionSetupController.class);

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String initCompetitionSetupSection(Model model, @PathVariable("competitionId") Long competitionId){

        List<CompetitionSetupSectionResource> sections = competitionService.getCompetitionSetupSectionsByCompetitionId(competitionId);

        if(sections.size() > 0) {
            return "redirect:/competition/setup/" + competitionId + "/section/" + sections.get(0).getId();
        } else {
            LOG.error("Competition is not found");
            return "redirect:/dashboard";
        }
    }


    @RequestMapping(value = "/section/{section}", method = RequestMethod.GET)
    public String editCompetitionSetupSection(Model model, @PathVariable("competitionId") Long competitionId, @PathVariable("section") Long sectionId){

        CompetitionResource competition = competitionService.getById(competitionId);
        List<Long> completedSections = competitionService.getCompletedCompetitionSetupSectionStatusesByCompetitionId(competitionId);
        List<CompetitionSetupSectionResource> sections = competitionService.getCompetitionSetupSectionsByCompetitionId(competitionId);

        Optional<CompetitionSetupSectionResource> competitionSetupSection = sections.stream()
                .filter(competitionSetupSectionResource -> competitionSetupSectionResource.getId() == sectionId)
                .findAny();

        if(!competitionSetupSection.isPresent()) {
            LOG.error("Competition setup section is not found");
            return "redirect:/dashboard";
        }

		model.addAttribute("competition", competition);
        model.addAttribute("currentSection", competitionSetupSection.get());
        model.addAttribute("editable", !completedSections.contains(sectionId));
        model.addAttribute("allSections", sections);
        model.addAttribute("allCompletedSections", completedSections);
        model.addAttribute("sectionFormData", getSectionFormData(competition, competitionSetupSection.get()));
        model.addAttribute("subTitle", (competition.getCode() != null ? competition.getCode() : "Unknown") + ": " + (competition.getName() != null ? competition.getName() : "Unknown"));

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
        competitionSetupForm.setPafNumber(competitionSetupForm.getPafNumber());
        competitionSetupForm.setTitle(competitionResource.getName());
        competitionSetupForm.setBudgetCode(competitionResource.getBudgetCode());

        return competitionSetupForm;
    }


    @RequestMapping(value = "/section/{section}", method = RequestMethod.POST)
    public String saveCompetitionSetupSection(Model model, @PathVariable("competitionId") Long competitionId, @PathVariable("section") Long sectionId){

        CompetitionResource competition = competitionService.getById(competitionId);
        List<Long> completedSections = competitionService.getCompletedCompetitionSetupSectionStatusesByCompetitionId(competitionId);
        List<CompetitionSetupSectionResource> sections = competitionService.getCompetitionSetupSectionsByCompetitionId(competitionId);

        Optional<CompetitionSetupSectionResource> competitionSetupSection = sections.stream()
                .filter(competitionSetupSectionResource -> competitionSetupSectionResource.getId() == sectionId)
                .findAny();

        if(!competitionSetupSection.isPresent()) {
            LOG.error("Competition setup section is not found");
            return "redirect:/dashboard";
        }

        model.addAttribute("competition", competition);
        model.addAttribute("currentSection", sectionId);
        model.addAttribute("editable", completedSections.contains(sectionId));
        model.addAttribute("allSections", sections);
        model.addAttribute("allCompletedSections", completedSections);

        return "competition/setup";
    }



}
