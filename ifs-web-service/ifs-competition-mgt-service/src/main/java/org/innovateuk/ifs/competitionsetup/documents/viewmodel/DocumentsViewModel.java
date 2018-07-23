package org.innovateuk.ifs.competitionsetup.documents.viewmodel;

import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.competition.resource.DocumentResource;

import java.util.List;


public class DocumentsViewModel extends CompetitionSetupViewModel {

    private List<DocumentResource> allDocuments;

    public DocumentsViewModel(
            GeneralSetupViewModel generalSetupViewModel,
            List<DocumentResource> allDocuments
    ) {
        this.generalSetupViewModel = generalSetupViewModel;
        this.allDocuments = allDocuments;
    }

    public List<DocumentResource> getAllDocuments() {
        return allDocuments;
    }
}
