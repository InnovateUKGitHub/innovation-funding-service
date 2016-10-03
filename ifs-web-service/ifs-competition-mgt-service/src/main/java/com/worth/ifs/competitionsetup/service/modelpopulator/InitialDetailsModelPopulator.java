package com.worth.ifs.competitionsetup.service.modelpopulator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.worth.ifs.application.service.CategoryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.category.resource.CategoryType;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionSetupSection;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.UserService;

/**
 * populates the model for the initial details competition setup section.
 */
@Service
public class InitialDetailsModelPopulator implements CompetitionSetupSectionModelPopulator {

	@Autowired
	private CompetitionService competitionService;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;
	
	@Override
	public CompetitionSetupSection sectionToPopulateModel() {
		return CompetitionSetupSection.INITIAL_DETAILS;
	}

	@Override
	public void populateModel(Model model, CompetitionResource competitionResource) {
		model.addAttribute("competitionExecutiveUsers", userService.findUserByType(UserRoleType.COMP_EXEC));
		model.addAttribute("innovationSectors", categoryService.getCategoryByType(CategoryType.INNOVATION_SECTOR));
		model.addAttribute("innovationAreas", categoryService.getCategoryByType(CategoryType.INNOVATION_AREA));
		model.addAttribute("competitionTypes", competitionService.getAllCompetitionTypes());
		model.addAttribute("competitionLeadTechUsers", userService.findUserByType(UserRoleType.COMP_TECHNOLOGIST));
	}

}
