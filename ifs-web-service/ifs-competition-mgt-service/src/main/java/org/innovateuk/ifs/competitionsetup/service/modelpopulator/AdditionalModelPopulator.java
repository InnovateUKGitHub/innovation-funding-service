package org.innovateuk.ifs.competitionsetup.service.modelpopulator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.viewmodel.AdditionalModelViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.innovateuk.ifs.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * populates the model for the additional info competition setup section.
 */
@Service
public class AdditionalModelPopulator implements CompetitionSetupSectionModelPopulator {

	@Autowired
	private CompetitionService competitionService;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryRestService categoryRestService;
	
	@Override
	public CompetitionSetupSection sectionToPopulateModel() {
		return CompetitionSetupSection.ADDITIONAL_INFO;
	}

    @Override
	public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
		return new AdditionalModelViewModel(generalViewModel);
	}
}
