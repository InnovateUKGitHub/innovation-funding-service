package org.innovateuk.ifs.competitionsetup.documents.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.competitionsetup.documents.service.DocumentsService;
import org.innovateuk.ifs.competitionsetup.documents.viewmodel.DocumentsViewModel;
import org.springframework.stereotype.Service;

import java.util.List;
import org.innovateuk.ifs.competition.resource.DocumentResource;

/**
 * populates the model for the documents competition setup section.
 */
@Service
public class DocumentsModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.DOCUMENTS;
    }

    @Override
    public CompetitionSetupViewModel populateModel(
            GeneralSetupViewModel generalViewModel,
            CompetitionResource competitionResource
    ) {

        // TODO I think if we actually need a service it persists
        DocumentsService service = new DocumentsService();
        List<DocumentResource> allDocuments = service.getAllDocuments();

        return new DocumentsViewModel(
                generalViewModel,
                allDocuments
        );
    }
}


