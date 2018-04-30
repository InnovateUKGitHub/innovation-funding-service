package org.innovateuk.ifs.competitionsetup.initialdetail.populator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.core.util.CompetitionUtils;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.initialdetail.viewmodel.InitialDetailsViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;

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
	public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
		return new InitialDetailsViewModel(generalViewModel,
                userService.findUserByType(COMP_ADMIN),
				categoryRestService.getInnovationSectors().getSuccess(),
				addAllInnovationAreaOption(categoryRestService.getInnovationAreas().getSuccess()),
                competitionService.getAllCompetitionTypes(),
                userService.findUserByType(INNOVATION_LEAD));
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
