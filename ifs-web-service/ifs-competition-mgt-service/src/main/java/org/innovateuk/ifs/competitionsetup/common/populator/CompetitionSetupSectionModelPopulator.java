package org.innovateuk.ifs.competitionsetup.common.populator;

import org.innovateuk.ifs.competitionsetup.common.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.common.viewmodel.GeneralSetupViewModel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;

/**
 * implementations of this interface will populate the model for the relevant competition setup section.
 */
public interface CompetitionSetupSectionModelPopulator {

	CompetitionSetupSection sectionToPopulateModel();
	
	CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource);
}
