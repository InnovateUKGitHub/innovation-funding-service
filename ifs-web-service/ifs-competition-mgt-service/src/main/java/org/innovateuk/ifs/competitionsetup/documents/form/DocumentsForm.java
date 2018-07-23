package org.innovateuk.ifs.competitionsetup.documents.form;

import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;

import javax.validation.Valid;
import java.util.List;

/**
 * Form for the documents competition setup section.
 */
public class DocumentsForm extends CompetitionSetupForm {
    @Valid
    private List<CompetitionSetupForm> allDocuments;

    public List<CompetitionSetupForm> getAllDocuments() {
        return allDocuments;
    }

    public void setAllDocuments(List<CompetitionSetupForm> allDocuments) {
        this.allDocuments = allDocuments;
    }

}





