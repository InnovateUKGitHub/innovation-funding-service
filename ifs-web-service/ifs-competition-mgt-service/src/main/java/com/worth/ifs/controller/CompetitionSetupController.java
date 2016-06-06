package com.worth.ifs.controller;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/competition/setup/{competitionId}")
public class CompetitionSetupController {
    
	private static final int PAGE_SIZE = 20;
	
    @Autowired
    private CompetitionService competitionService;

    @RequestMapping("/{section}")
    public String editCompetitionSetupStepOne(Model model, @PathVariable("competitionId") Long competitionId, @PathVariable("section") Long section){

		CompetitionResource competition = competitionService.getById(competitionId);
		model.addAttribute("competition", competition);
        model.addAttribute("currentSection", section);
        model.addAttribute("allSections", competitionService.getCompetitionSetupSectionsByCompetitionId(competitionId));
        model.addAttribute("allCompletedSections", competitionService.getCompletedCompetitionSetupSectionStatusesByCompetitionId(competitionId));

		return "competition/setup";
    }



}
