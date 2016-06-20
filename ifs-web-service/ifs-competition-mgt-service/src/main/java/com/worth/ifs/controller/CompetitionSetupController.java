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
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionResource.Status;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.controller.form.CompetitionSetupForm;
import com.worth.ifs.controller.form.CompetitionSetupInitialDetailsForm;
import com.worth.ifs.service.CompetitionSetupService;


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
    private CompetitionSetupService competitionSetupService;
   
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
    
    @RequestMapping(value = "/{competitionId}/section/{sectionPath}/edit", method = RequestMethod.POST)
    public String setSectionAsIncomplete(@PathVariable("competitionId") Long competitionId, @PathVariable("sectionPath") String sectionPath) {

    	CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);
    	if(section == null) {
    		LOG.error("Invalid section path specified: " + sectionPath);
            return "redirect:/dashboard";
    	}
    	
        competitionService.setSetupSectionMarkedAsIncomplete(competitionId, section);

        return "redirect:/competition/setup/" + competitionId + "/section/" + section.getPath();
    }

    @RequestMapping(value = "/{competitionId}/section/{sectionPath}", method = RequestMethod.GET)
    public String editCompetitionSetupSection(Model model, @PathVariable("competitionId") Long competitionId, @PathVariable("sectionPath") String sectionPath) {

    	CompetitionSetupSection section = CompetitionSetupSection.fromPath(sectionPath);
    	if(section == null) {
    		LOG.error("Invalid section path specified: " + sectionPath);
            return "redirect:/dashboard";
    	}
    	
        CompetitionResource competition = competitionService.getById(competitionId);

        if(competition == null || !Status.COMPETITION_SETUP.equals(competition.getCompetitionStatus())) {
        	LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
        }

        competitionSetupService.populateCompetitionSectionModelAttributes(model, competition, section);
        model.addAttribute("competitionSetupForm", competitionSetupService.getSectionFormData(competition, section));

        return "competition/setup";
    }
    
    @RequestMapping(value = "/{competitionId}/section/initial", method = RequestMethod.POST)
    public String submitSectionDetails(@Valid @ModelAttribute("competitionSetupForm") CompetitionSetupInitialDetailsForm competitionSetupForm,
                                              BindingResult bindingResult,
                                              @PathVariable("competitionId") Long competitionId,
                                              Model model, HttpServletRequest request) {

        return genericCompetitionSetupSection(competitionSetupForm, bindingResult, competitionId, CompetitionSetupSection.INITIAL_DETAILS, model);
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
    public String generateCompetitionCode(@PathVariable("competitionId") Long competitionId, HttpServletRequest request) {

        LocalDateTime openingDate = LocalDateTime.of(Integer.parseInt(request.getParameter("year")),
                Integer.parseInt(request.getParameter("month")),
                Integer.parseInt(request.getParameter("day")),
                0, 0, 0);
        return competitionService.generateCompetitionCode(competitionId, openingDate);
    }

    
    private String genericCompetitionSetupSection(CompetitionSetupForm competitionSetupForm, BindingResult bindingResult, Long competitionId, CompetitionSetupSection section, Model model) {
        CompetitionResource competition = competitionService.getById(competitionId);

        if(competition == null || !Status.COMPETITION_SETUP.equals(competition.getCompetitionStatus())) {
        	LOG.error("Competition is not found in setup state");
            return "redirect:/dashboard";
        }

        if (!bindingResult.hasErrors()) {
        	competitionSetupService.saveCompetitionSetupSection(competitionSetupForm, competition, section);
        } else {
            LOG.debug("Form errors");
        }

        competitionSetupService.populateCompetitionSectionModelAttributes(model, competition, section);

        return "competition/setup";
    }

}
