package org.innovateuk.ifs.competitionsetup.documents.form;

import org.innovateuk.ifs.competition.resource.DocumentResource;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;

public class DocumentEditForm extends CompetitionSetupForm {

    public DocumentResource getDocument() {
        return document;
    }

    public void setDocument(DocumentResource document) {
        this.document = document;
    }

    private DocumentResource document;

}
