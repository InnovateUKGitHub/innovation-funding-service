package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlyData;
import org.innovateuk.ifs.form.resource.QuestionResource;

public class GrantAgreementReadOnlyViewModel extends AbstractQuestionReadOnlyViewModel {

    private final String filename;

    public GrantAgreementReadOnlyViewModel(ApplicationReadOnlyData data, QuestionResource question, String filename) {
        super(data, question);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String getFragment() {
        return "grant-agreement";
    }

    @Override
    public boolean shouldDisplayMarkAsComplete() {
        return false;
    }

}
