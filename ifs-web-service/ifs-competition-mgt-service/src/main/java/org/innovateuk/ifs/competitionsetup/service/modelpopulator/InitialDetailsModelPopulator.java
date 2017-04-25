package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

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
	private CategoryRestService categoryRestService;
	
	@Override
	public CompetitionSetupSection sectionToPopulateModel() {
		return CompetitionSetupSection.INITIAL_DETAILS;
	}

	@Override
	public void populateModel(Model model, CompetitionResource competitionResource) {
		model.addAttribute("competitionExecutiveUsers", userService.findUserByType(UserRoleType.COMP_ADMIN));
		model.addAttribute("innovationSectors", categoryRestService.getInnovationSectors().getSuccessObjectOrThrowException());
		model.addAttribute("innovationAreas", categoryRestService.getInnovationAreas().getSuccessObjectOrThrowException());
		model.addAttribute("competitionTypes", competitionService.getAllCompetitionTypes());
		model.addAttribute("competitionLeadTechUsers", userService.findUserByType(UserRoleType.COMP_TECHNOLOGIST));
	}

}
