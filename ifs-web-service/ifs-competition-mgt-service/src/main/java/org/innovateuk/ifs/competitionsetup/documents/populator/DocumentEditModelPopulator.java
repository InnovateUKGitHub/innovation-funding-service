package org.innovateuk.ifs.competitionsetup.documents.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.documents.viewmodel.DocumentEditViewModel;
import org.springframework.stereotype.Service;

@Service
public class DocumentEditModelPopulator {

    public CompetitionSetupViewModel populateModel(
            CompetitionResource competitionResource
    ) {
        return new DocumentEditViewModel();
    }
}
