package org.innovateuk.ifs.competitionsetup.core.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;

/**
 * implementations of this interface will populate the model for the relevant competition setup section.
 */
public interface CompetitionSetupSectionModelPopulator {

    CompetitionSetupSection sectionToPopulateModel();

    CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource);
}
