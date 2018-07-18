package org.innovateuk.ifs.competitionsetup.documents.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupFormPopulator;
import org.innovateuk.ifs.competitionsetup.documents.form.DocumentsForm;
import org.springframework.stereotype.Service;


/**
 * Form populator for the documents competition setup section.
 */
@Service
public class DocumentsFormPopulator implements CompetitionSetupFormPopulator {

    @Override
    public CompetitionSetupSection sectionToFill() {
        return CompetitionSetupSection.DOCUMENTS;
    }

    @Override
    public CompetitionSetupForm populateForm(CompetitionResource competitionResource) {
        DocumentsForm competitionSetupForm = new DocumentsForm();
        return competitionSetupForm;
    }
}
