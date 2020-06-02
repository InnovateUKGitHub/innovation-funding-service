package org.innovateuk.ifs.management.competition.setup.postawardservice.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.competition.setup.postawardservice.viewmodel.ChoosePostAwardServiceViewModel;
import org.springframework.stereotype.Service;

@Service
public class ChoosePostAwardServiceModelPopulator {
    public ChoosePostAwardServiceViewModel populateModel(CompetitionResource competition) {
        return new ChoosePostAwardServiceViewModel(competition.getId(), competition.getName());
    }
}
