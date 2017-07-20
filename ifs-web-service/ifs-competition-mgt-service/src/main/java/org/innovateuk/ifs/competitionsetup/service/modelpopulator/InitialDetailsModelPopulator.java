package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.utils.CompetitionUtils;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

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
		model.addAttribute("innovationAreas", addAllInnovationAreaOption(categoryRestService.getInnovationAreas().getSuccessObjectOrThrowException()));
		model.addAttribute("competitionTypes", competitionService.getAllCompetitionTypes());
		model.addAttribute("innovationLeadTechUsers", userService.findUserByType(UserRoleType.INNOVATION_LEAD));
	}

	private List<InnovationAreaResource> addAllInnovationAreaOption(List<InnovationAreaResource> innovationAreas) {
        List<InnovationAreaResource> returnList = new ArrayList<>(innovationAreas);

		InnovationAreaResource innovationAreaResource = new InnovationAreaResource();
		innovationAreaResource.setId(CompetitionUtils.ALL_INNOVATION_AREAS);
		innovationAreaResource.setName("All");

        returnList.add(0, innovationAreaResource);

		return returnList;
	}

}
