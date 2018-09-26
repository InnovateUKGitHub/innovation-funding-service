package org.innovateuk.ifs.competitionsetup.stakeholder.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.stakeholder.viewmodel.ManageStakeholderViewModel;
import org.springframework.stereotype.Service;

@Service
public class ManageStakeholderModelPopulator {

    public ManageStakeholderViewModel populateModel(CompetitionResource competition) {
        return new ManageStakeholderViewModel(competition.getId(), competition.getName());
    }
}

