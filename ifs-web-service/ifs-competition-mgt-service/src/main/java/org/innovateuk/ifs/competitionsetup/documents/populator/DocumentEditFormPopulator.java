package org.innovateuk.ifs.competitionsetup.documents.populator;

import org.innovateuk.ifs.competition.resource.DocumentResource;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.documents.form.DocumentEditForm;
import org.innovateuk.ifs.competitionsetup.documents.service.DocumentsService;
import org.springframework.stereotype.Service;

/**
 * Form populator for the documents competition setup section.
 */
@Service
public class DocumentEditFormPopulator {

    public CompetitionSetupForm populateForm(Long documentId) {
        DocumentsService service = new DocumentsService();
        DocumentResource document;
        if(documentId == null) {
            document = new DocumentResource();
        }
        else {
            document = service.getDocumentByID(documentId);
        }

        DocumentEditForm competitionSetupForm = new DocumentEditForm();
        competitionSetupForm.setDocument(document);
        return competitionSetupForm;
    }
}